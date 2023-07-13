package net.babblebot.plugin.webhook.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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
        private String body;
    }
}
