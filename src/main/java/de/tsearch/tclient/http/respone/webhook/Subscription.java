package de.tsearch.tclient.http.respone.webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class Subscription {
    @SerializedName("id")
    @JsonProperty("id")
    private UUID id;
    @SerializedName("type")
    @JsonProperty("type")
    private String type;
    @SerializedName("version")
    @JsonProperty("version")
    private String version;
    @SerializedName("status")
    @JsonProperty("status")
    private String status;
    @SerializedName("cost")
    @JsonProperty("cost")
    private long cost;
    @SerializedName("condition")
    @JsonProperty("condition")
    private Condition condition;
    @SerializedName("created_at")
    @JsonProperty("created_at")
    private Date createdAt;
    @SerializedName("transport")
    @JsonProperty("transport")
    private Transport transport;
}
