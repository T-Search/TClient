package de.tsearch.tclient.http.respone;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.ArrayList;

@Data
public class TokenValidateResponse {
    @SerializedName("client_id")
    private String clientId;

    @SerializedName("login")
    private String login;

    @SerializedName("scopes")
    private ArrayList<String> scopes = new ArrayList<>();

    @SerializedName("user_id")
    private Long userId;

    @SerializedName("expires_in")
    private Integer expiresIn;
}
