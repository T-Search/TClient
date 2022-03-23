package de.tsearch.tclient.http.respone;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Pagination {
    @SerializedName("cursor")
    private String cursor;
}
