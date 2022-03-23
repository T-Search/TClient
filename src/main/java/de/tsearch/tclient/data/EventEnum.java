package de.tsearch.tclient.data;

import lombok.Getter;

@Getter
public enum EventEnum {
    STREAM_OFFLINE("stream.offline"),
    STREAM_ONLINE("stream.online"),
    USER_UPDATE("user.update");
    private final String webhookEventType;

    EventEnum(String webhookEventType) {
        this.webhookEventType = webhookEventType;
    }

    public static EventEnum getByWebhookEventType(String type) {
        for (EventEnum value : EventEnum.values()) {
            if (value.webhookEventType.equals(type)) return value;
        }

        return null;
    }
}
