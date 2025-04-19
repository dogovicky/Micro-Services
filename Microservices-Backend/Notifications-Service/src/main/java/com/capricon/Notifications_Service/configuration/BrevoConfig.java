package com.capricon.Notifications_Service.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.xdev.brevo.api.TransactionalEmailsApi;
import software.xdev.brevo.client.ApiClient;

@Configuration
public class BrevoConfig {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Bean
    public ApiClient brevoApiClient() {
        ApiClient apiClient = software.xdev.brevo.client.Configuration.getDefaultApiClient();
        apiClient.setApiKey(apiKey);
        return apiClient;
    }

    @Bean
    public TransactionalEmailsApi transactionalEmailsApi(ApiClient apiClient) {
        return new TransactionalEmailsApi(apiClient);
    }


}
