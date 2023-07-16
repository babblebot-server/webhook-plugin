package net.babblebot.plugin.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import net.babblebot.api.command.Command;
import net.babblebot.api.command.CommandParam;
import net.babblebot.api.command.ICommandContext;
import net.babblebot.api.obj.DiscordColor;
import net.babblebot.api.obj.message.discord.DiscordMessage;
import net.babblebot.api.obj.message.discord.embed.EmbedAuthor;
import net.babblebot.api.obj.message.discord.embed.EmbedMessage;
import net.babblebot.api.plugins.Plugin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.babblebot.plugin.webhook.properties.WebhookProperties;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;


/**
 * @author email@aaronburt.co.uk (Aaron Michael Burt)
 * @since 1.0.0
 */
@Plugin
@RequiredArgsConstructor
@Slf4j
public class WebHookPlugin {

    public EmbedAuthor embedAuthor(){
        return EmbedAuthor.builder()
                .name("Webhook")
                .url("https://github.com/babblebot-server/webhook-plugin")
                .iconUrl("https://avatars.githubusercontent.com/u/138989349")
                .build();
    }

    public EmbedMessage createErrorEmbedMessage(String title, String description){
        return EmbedMessage.builder()
                .title(title)
                .description(description)
                .color(DiscordColor.RED)
                .author(embedAuthor())
                .build();
    }

    public WebhookProperties.Webhook getWebhook(String hookName){
        try {
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource("classpath:application.yaml");

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            WebhookProperties webhookProperties = mapper.readValue(resource.getInputStream(), WebhookProperties.class);

            return webhookProperties.getWebhook()
                    .stream()
                    .filter(webhook -> webhook.getName().equalsIgnoreCase(hookName))
                    .findFirst()
                    .orElseThrow();


        } catch(Exception error){
            error.printStackTrace();
        }
        return null;
    }

    @Command(aliases = "send", description = "Trigger the hook")
    @CommandParam(value = "hook", canBeEmpty = false, optional = false, exampleValue = "hook name")
    public EmbedMessage sendDiscordResponse(DiscordMessage message, ICommandContext context) {
        try {
            WebhookProperties.Webhook hook = getWebhook(context.getParameter("hook"));

            if(hook == null) {
                throw new Exception("hook is null");
            }

            LinkedList<String> resultList = new LinkedList<>();
            hook.getHeaders().forEach((name, value) -> { resultList.add(name); resultList.add(value); });
            HttpClient client = HttpClient.newHttpClient();
            URI uri = URI.create(hook.getUrl());
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .headers(resultList.toArray(String[]::new))
                    .uri(uri);

            switch (hook.getMethod()) {
                case "GET" -> requestBuilder.GET();
                case "POST" -> requestBuilder.POST(HttpRequest.BodyPublishers.ofString(hook.getBody()));
                default -> throw new IllegalArgumentException("Unsupported request method: " + hook.getMethod());
            }

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return EmbedMessage.builder().title("Hook " + hook.getName() + " " + hook.getUrl())
                    .color(DiscordColor.GREEN)
                    .author(embedAuthor())
                    .description(response.body())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return createErrorEmbedMessage("Hook [Error]", e.getMessage());
        }
    }
}

