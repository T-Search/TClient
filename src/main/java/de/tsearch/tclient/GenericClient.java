package de.tsearch.tclient;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import de.tsearch.tclient.http.respone.Response;
import kong.unirest.GenericType;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import kong.unirest.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

class GenericClient<T> {
    protected final TClientInstance clientInstance;
    protected final SimpleDateFormat rfcDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
    protected final Gson gson = new Gson();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final Class<T> type;


    public GenericClient(TClientInstance clientInstance, Class<T> type) {
        this.clientInstance = clientInstance;
        this.type = type;
    }

    List<T> requestWithCursorFollowing(HttpRequest<?> request) {
        return this.requestWithCursorFollowing(request, Integer.MAX_VALUE);
    }

    List<T> requestWithCursorFollowing(HttpRequest<?> request, int maxLevel) {
        this.clientInstance.reLoginIfNecessary();
        String cursor = null;
        boolean retry = false;
        int currentLevel = 0;

        request.headers(clientInstance.standardHeader);

        List<T> data = new ArrayList<>();

        do {
            if (cursor != null) request.queryString("after", cursor);

            try {
                HttpResponse<Response<JsonElement>> response = request.asObject(new GenericType<>() {
                });
                String ratelimit = response.getHeaders().getFirst("Ratelimit-Remaining");
                if (ratelimit != null) LOGGER.debug("Ratelimit-Remaining: " + ratelimit);

                if (response.getStatus() == 200) {
                    for (JsonElement jsonElement : response.getBody().getData()) {
                        data.add(gson.fromJson(jsonElement, type));
                    }

                    if (response.getBody().getPagination() != null) {
                        cursor = response.getBody().getPagination().getCursor();
                    } else {
                        cursor = null;
                    }

                    retry = false;
                    currentLevel++;
                } else if (response.getStatus() == 429) {
                    retry = true;
                    LOGGER.warn("Rate-Limit reached!");
                    Thread.sleep(500);
                }
            } catch (UnirestException | InterruptedException e) {
                e.printStackTrace();
            }
        } while ((cursor != null || retry) && currentLevel < maxLevel);

        return data;
    }
}
