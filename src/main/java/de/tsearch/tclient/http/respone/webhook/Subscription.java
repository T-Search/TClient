package de.tsearch.tclient.http.respone.webhook;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class Subscription {
    @SerializedName("id")
    private UUID id;
    @SerializedName("type")
    private String type;
    @SerializedName("version")
    private String version;
    @SerializedName("status")
    private String status;
    @SerializedName("cost")
    private long cost;
    @SerializedName("condition")
    private Condition condition;
    @SerializedName("created_at")
    private Date createdAt;
    @SerializedName("transport")
    private Transport transport;
}
