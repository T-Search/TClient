package de.tsearch.tclient.http.respone;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Date;

@Data
public class User {
    @SerializedName("id")
    private String id;

    @SerializedName("login")
    private String loginName;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("type")
    private String type;

    @SerializedName("broadcaster_type")
    private String broadcasterType;

    @SerializedName("description")
    private String description;

    @SerializedName("profile_image_url")
    private String profileImageUrl;

    @SerializedName("offline_image_url")
    private String offlineImageUrl;

    @SerializedName("view_count")
    private long viewCount;

    @SerializedName("created_at")
    private Date createdAt;
}
