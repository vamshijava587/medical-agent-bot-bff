package com.vamshi.medicalagentbot;

import com.vamshi.medicalagentbot.common.MedicalAiAgentBotProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(MedicalAiAgentBotProperties.class)
public class MedicalAgentBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicalAgentBotApplication.class, args);
	}

}