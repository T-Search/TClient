package de.tsearch.tclient.http.respone;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Date;

@Data
public class Stream {
    @SerializedName("id")
    private String id;
    @SerializedName("user_id")
    private Long userID;
    @SerializedName("user_login")
    private String userLogin;
    @SerializedName("user_name")
    private String userName;
    @SerializedName("game_id")
    private String gameID;
    @SerializedName("game_name")
    private String gameName;
    @SerializedName("type")
    private String type;
    @SerializedName("title")
    private String title;
    @SerializedName("viewer_count")
    private long viewerCount;
    @SerializedName("started_at")
    private Date startedAt;
    @SerializedName("language")
    private String language;
    @SerializedName("thumbnail_url")
    private String thumbnailURL;
    @SerializedName("is_mature")
    private boolean mature;
}
