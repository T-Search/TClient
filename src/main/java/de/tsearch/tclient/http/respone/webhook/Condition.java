package de.tsearch.tclient.http.respone.webhook;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Condition {
    @SerializedName("broadcaster_user_id")
    private String broadcasterUserID;

    @SerializedName("user_id")
    private String userId;
}
