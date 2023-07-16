package net.babblebot.plugin.webhook.properties;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Configuration
@ConfigurationProperties
public class WebhookProperties {

    private List<Webhook> webhook;

    @Data
    public static class Webhook {
        private String name;
        private String method;
        private String url;
        @Nullable
        private String body;
        private Map<String, String> headers;
    }
}
