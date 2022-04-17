package de.tsearch.tclient.http.respone.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Transport {
    @SerializedName("method")
    @JsonProperty("method")
    private String method;
    @SerializedName("callback")
    @JsonProperty("callback")
    private String callback;
    @SerializedName("secret")
    @JsonProperty("secret")
    private String secret;
}
