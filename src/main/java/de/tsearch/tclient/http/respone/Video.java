package de.tsearch.tclient.http.respone;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Date;

@Data
public class Video {
    @SerializedName("id")
    private long id;

    @SerializedName("user_id")
    private long broadcasterId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("published_at")
    private Date publishedAt;

    @SerializedName("url")
    private String url;

    @SerializedName("thumbnail_url")
    private String thumbnailUrl;

    @SerializedName("view_count")
    private long viewCount;

    @SerializedName("language")
    private String language;

    @SerializedName("type")
    private String type;

    @SerializedName("duration")
    private String duration;
}
