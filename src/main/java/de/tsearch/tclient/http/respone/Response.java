package de.tsearch.tclient.http.respone;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class Response<T> {
    @SerializedName("data")
    List<T> data;

    @SerializedName("pagination")
    Pagination pagination;
}
