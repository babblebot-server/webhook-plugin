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

/**
 * Edit me
 *
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
    public EmbedMessage config(DiscordMessage message, ICommandContext context) {
        try {
            WebhookProperties.Webhook hook = getWebhook(context.getParameter("hook"));

            if(hook == null) {
                throw new Exception("hook is null");
            }

            HttpClient client = HttpClient.newHttpClient();


            //Need to implement the method, currently it will only send POST
            HttpRequest request =  HttpRequest.newBuilder()
                    .uri(URI.create(hook.getUrl()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(hook.getBody()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            return EmbedMessage.builder().title("Hook Success")
                    .color(DiscordColor.GREEN)
                    .author(embedAuthor())
                    .description(String.valueOf(statusCode))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return EmbedMessage.builder().title("Hook error")
                    .author(embedAuthor())
                    .color(DiscordColor.RED)
                    .description("")
                    .build();
        }
    }
}
