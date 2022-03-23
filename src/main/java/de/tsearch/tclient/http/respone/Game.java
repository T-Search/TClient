package de.tsearch.tclient.http.respone;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Game {
    @SerializedName("id")
    private long id;

    @SerializedName("box_art_url")
    private String boxArtUrl;

    @SerializedName("name")
    private String name;
}
