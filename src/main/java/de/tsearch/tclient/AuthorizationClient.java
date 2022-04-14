package de.tsearch.tclient;

import de.tsearch.tclient.http.respone.TokenResponse;
import de.tsearch.tclient.http.respone.TokenValidateResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AuthorizationClient {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final TClientInstance clientInstance;


    public AuthorizationClient(TClientInstance clientInstance) {
        this.clientInstance = clientInstance;
        if (clientInstance.getConfig().getClientSecret() == null) {
            throw new RuntimeException("Cannot initialize authorization client without client secret!");
        }
    }

    public Optional<TokenResponse> getTokenFromAuthorizationCode(String code, String redirect_uri) {
        HttpResponse<TokenResponse> response = Unirest.post("https://id.twitch.tv/oauth2/token")
                .field("client_id", this.clientInstance.getConfig().getClientId())
                .field("client_secret", this.clientInstance.getConfig().getClientSecret())
                .field("code", code)
                .field("grant_type", "authorization_code")
                .field("redirect_uri", redirect_uri)
                .asObject(TokenResponse.class);

        if (response.isSuccess()) {
            return Optional.of(response.getBody());
        } else {
            return Optional.empty();
        }
    }

    public Optional<TokenValidateResponse> validateToken(String accessToken) {
        HttpResponse<TokenValidateResponse> response = Unirest.get("https://id.twitch.tv/oauth2/validate")
                .header("Authorization", "OAuth " + accessToken)
                .asObject(TokenValidateResponse.class);

        if (response.isSuccess()) {
            return Optional.of(response.getBody());
        } else {
            return Optional.empty();
        }
    }

    public boolean revokeToken(String token) {
        HttpResponse response = Unirest.post("https://id.twitch.tv/oauth2/revoke")
                .field("client_id", this.clientInstance.getConfig().getClientId())
                .field("token", token).asEmpty();

        return response.isSuccess();
    }
}
