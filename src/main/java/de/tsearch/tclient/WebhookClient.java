package de.tsearch.tclient;

import de.tsearch.tclient.data.EventEnum;
import de.tsearch.tclient.data.PagedResponse;
import de.tsearch.tclient.http.respone.webhook.Condition;
import de.tsearch.tclient.http.respone.webhook.Subscription;
import de.tsearch.tclient.http.respone.webhook.Transport;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class WebhookClient extends GenericClient<Subscription> {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public WebhookClient(TClientInstance clientInstance) {
        super(clientInstance, Subscription.class);
    }

    public PagedResponse<Subscription> getAllSubscriptions() {
        return requestWithCursorFollowing(Unirest.get("https://api.twitch.tv/helix/eventsub/subscriptions"), Integer.MAX_VALUE);
    }

    public void requestNewWebhook(long broadcasterId, EventEnum eventEnum, String secret, String webhookCallback) throws UnirestException {
        LOGGER.debug("Request new webhook for broadcaster id " + broadcasterId + " for event " + eventEnum);

        Subscription subscription = new Subscription();
        subscription.setType(eventEnum.getWebhookEventType());
        subscription.setVersion("1");
        subscription.setCondition(new Condition(String.valueOf(broadcasterId), String.valueOf(broadcasterId)));

        Transport transport = new Transport();
        transport.setMethod("webhook");
        transport.setCallback(webhookCallback);
        transport.setSecret(secret);
        subscription.setTransport(transport);

        this.clientInstance.reLoginIfNecessary();
        this.clientInstance.rateLimiter.acquire();
        HttpResponse<String> response = Unirest
                .post("https://api.twitch.tv/helix/eventsub/subscriptions")
                .headers(this.clientInstance.standardHeader)
                .header("Content-Type", "application/json")
                .body(this.clientInstance.gson.toJson(subscription))
                .asString();
        if (response.getStatus() == 202) {
            LOGGER.info("Requested new webhook for broadcaster id " + broadcasterId + " for event " + eventEnum);
        } else {
            LOGGER.error("Cannot request new webhook for broadcaster id " + broadcasterId + ". Status: " + response.getStatus() + " - Body: " + response.getBody());
        }
    }

    public void deleteWebhook(UUID webhookId) {
        try {
            this.clientInstance.reLoginIfNecessary();
            this.clientInstance.rateLimiter.acquire();
            Unirest
                    .delete("https://api.twitch.tv/helix/eventsub/subscriptions")
                    .headers(this.clientInstance.standardHeader).queryString("id", webhookId.toString())
                    .asString();
            LOGGER.info("Deleted webhook id " + webhookId);
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}
