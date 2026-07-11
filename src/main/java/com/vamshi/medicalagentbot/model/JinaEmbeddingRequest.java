package com.vamshi.medicalagentbot.model;

import java.util.List;

public record JinaEmbeddingRequest(
            String model,
            String task,
            boolean normalized,
            int dimensions,
            List<String> input) {}