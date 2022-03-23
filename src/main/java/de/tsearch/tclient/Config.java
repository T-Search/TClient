package de.tsearch.tclient;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Objects;

@Getter
public class Config {
    private final String clientId;
    private final String clientSecret;
    /**
     * Is login credential provided, to relogin.
     */
    private final boolean loginCredentials;
    @Setter
    private String accessToken;
    @Setter
    private Instant validUntil;
    @Setter
    private int maxCursorFollows;

    private Config(String clientId, String clientSecret, String accessToken, boolean loginCredentials, int maxCursorFollows) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.accessToken = accessToken;
        this.loginCredentials = loginCredentials;
        this.maxCursorFollows = maxCursorFollows;

        if (accessToken != null) {
            this.validUntil = Instant.MAX;
        }
    }

    public static class ConfigBuilder {
        private String clientId;
        private String clientSecret;
        private String accessToken;

        private int maxCursorFollows = 20;

        private ConfigBuilder() {
        }

        public static ConfigBuilder newInstance() {
            return new ConfigBuilder();
        }

        public ConfigBuilder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public ConfigBuilder setClientSecret(String clientSecret) {
            if (this.accessToken != null) {
                throw new RuntimeException("Access token and client secret cannot be passed.");
            }
            this.clientSecret = clientSecret;
            return this;
        }

        public ConfigBuilder setAccessToken(String accessToken) {
            if (this.clientSecret != null) {
                throw new RuntimeException("Access token and client secret cannot be passed.");
            }
            this.accessToken = accessToken;
            return this;
        }

        public ConfigBuilder setMaxCursorFollows(int follows) {
            this.maxCursorFollows = follows;
            return this;
        }

        public Config build() {
            Objects.requireNonNull(this.clientId, "Client Id cannot be null");
            if (this.clientSecret == null && this.accessToken == null) {
                throw new NullPointerException("Needed access token or client secret");
            }

            return new Config(this.clientId, this.clientSecret, this.accessToken, this.clientSecret != null, this.maxCursorFollows);
        }
    }
}
