package de.tsearch.tclient.http.respone.webhook;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Transport {
    @SerializedName("method")
    private String method;
    @SerializedName("callback")
    private String callback;
    @SerializedName("secret")
    private String secret;
}
