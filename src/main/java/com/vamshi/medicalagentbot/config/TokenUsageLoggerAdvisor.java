package com.vamshi.medicalagentbot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Replaces SimpleLoggerAdvisor.
 * Logs ONLY: model, promptTokens, completionTokens, totalTokens,
 * remaining rate-limit tokens (if the provider returns them), response time.
 *
 * NOTE on why earlier versions logged once per chunk:
 * ChatClientMessageAggregator's callback fires on every intermediate merge
 * step while streaming, not just the final one - most intermediate steps
 * simply have empty/zero Usage, which is why the logs showed
 * "Prompt Tokens: 0" repeatedly and only the LAST line had real numbers.
 *
 * Fix: don't log inside any per-chunk callback at all. Instead, track the
 * latest ChatClientResponse seen via doOnNext (cheap, no logging there),
 * and log exactly once in doOnComplete, using only that final captured value.
 */
public class TokenUsageLoggerAdvisor implements CallAdvisor, StreamAdvisor {

    private static final Logger log = LoggerFactory.getLogger(TokenUsageLoggerAdvisor.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        long start = System.currentTimeMillis();
        ChatClientResponse response = chain.nextCall(request);
        logUsage(response, System.currentTimeMillis() - start);
        return response;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        long start = System.currentTimeMillis();
        AtomicReference<ChatClientResponse> lastSeen = new AtomicReference<>();
        AtomicReference<ChatClientResponse> lastWithUsage = new AtomicReference<>();

        return chain.nextStream(request)
                .doOnNext(item -> {
                    lastSeen.set(item);
                    if (hasUsage(item)) {
                        lastWithUsage.set(item);
                    }
                })
                .doOnComplete(() -> {
                    ChatClientResponse toLog = lastWithUsage.get() != null ? lastWithUsage.get() : lastSeen.get();
                    if (toLog != null) {
                        logUsage(toLog, System.currentTimeMillis() - start);
                    }
                });
    }

    private boolean hasUsage(ChatClientResponse response) {
        ChatResponse chatResponse = response.chatResponse();
        if (chatResponse == null || chatResponse.getMetadata() == null) {
            return false;
        }
        Usage usage = chatResponse.getMetadata().getUsage();
        return usage != null && usage.getTotalTokens() != null && usage.getTotalTokens() > 0;
    }

    private void logUsage(ChatClientResponse response, long responseTimeMs) {
        try {
            ChatResponse chatResponse = response.chatResponse();
            if (chatResponse == null || chatResponse.getMetadata() == null) {
                log.warn("TokenUsageLoggerAdvisor: no chat response metadata to log");
                return;
            }

            var metadata = chatResponse.getMetadata();
            Usage usage = metadata.getUsage();
            String model = metadata.getModel();

            String promptTokens = (usage != null && usage.getPromptTokens() != null)
                    ? String.valueOf(usage.getPromptTokens()) : "N/A";
            String completionTokens = (usage != null && usage.getCompletionTokens() != null)
                    ? String.valueOf(usage.getCompletionTokens()) : "N/A";
            String totalTokens = (usage != null && usage.getTotalTokens() != null)
                    ? String.valueOf(usage.getTotalTokens()) : "N/A";

            String remaining = "N/A";
            if (metadata.getRateLimit() != null && metadata.getRateLimit().getTokensRemaining() != null) {
                Long tokensRemaining = metadata.getRateLimit().getTokensRemaining();
                if (tokensRemaining > 0) {
                    remaining = String.valueOf(tokensRemaining);
                }
            }

            log.info("Model:{},Prompt Tokens:[{}],Completion Tokens:[{}],Total Tokens:[{}],Remaining tokens:[{}],Response Time:{} ms",
                    model, promptTokens, completionTokens, totalTokens, remaining, responseTimeMs);

        } catch (Exception e) {
            log.warn("TokenUsageLoggerAdvisor failed to log usage", e);
        }
    }
}