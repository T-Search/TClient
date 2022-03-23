package de.tsearch.tclient.http.respone;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Date;

@Data
public class Clip {
    @SerializedName("id")
    private String id;
    @SerializedName("url")
    private String url;
    @SerializedName("embed_url")
    private String embedURL;
    @SerializedName("broadcaster_id")
    private String broadcasterID;
    @SerializedName("broadcaster_name")
    private String broadcasterName;
    @SerializedName("creator_id")
    private String creatorID;
    @SerializedName("creator_name")
    private String creatorName;
    @SerializedName("video_id")
    private String videoID;
    @SerializedName("game_id")
    private String gameID;
    @SerializedName("language")
    private String language;
    @SerializedName("title")
    private String title;
    @SerializedName("view_count")
    private long viewCount;
    @SerializedName("created_at")
    private Date createdAt;
    @SerializedName("thumbnail_url")
    private String thumbnailURL;
    @SerializedName("duration")
    private double duration;
}
