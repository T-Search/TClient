package de.tsearch.tclient;

import com.google.gson.JsonElement;
import de.tsearch.tclient.data.PagedResponse;
import de.tsearch.tclient.http.respone.Response;
import kong.unirest.GenericType;
import kong.unirest.HttpRequest;
import kong.unirest.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GenericClient<T> {
    protected final TClientInstance clientInstance;
    private final Class<T> type;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public GenericClient(TClientInstance clientInstance, Class<T> type) {
        this.clientInstance = clientInstance;
        this.type = type;
    }

    PagedResponse<T> requestWithCursorFollowing(HttpRequest<?> request, int maxLevel) {
        String cursor = null;
        byte retryCount = 0;
        int currentLevel = 0;
        List<T> data = new ArrayList<>();

        do {
            if (cursor != null) request.queryString("after", cursor);
            PagedResponse<T> pagedResponse = executeRequest(request, currentLevel, maxLevel);
            cursor = pagedResponse.getNextPage();
            if (pagedResponse.getStatusCode() == 200) {
                retryCount = 0;
                currentLevel++;
                data.addAll(pagedResponse.getData());
            } else {
                retryCount++;
            }
        } while ((cursor != null || retryCount != 0) && currentLevel < maxLevel && retryCount < 3);
        PagedResponse.PagedResponseBuilder<T> builder = PagedResponse.builder();
        builder.cursorFollows(currentLevel);
        builder.maxCursorFollows(maxLevel);
        builder.statusCode(200);
        builder.hasNext(cursor != null);
        builder.nextPage(cursor);
        builder.data(data);
        return builder.build();
    }

    protected PagedResponse<T> executeRequest(HttpRequest<?> request) {
        return executeRequest(request, 0, 0);
    }

    protected PagedResponse<T> executeRequest(HttpRequest<?> request, int cursorDepth, int maxCursorDepth) {
        this.clientInstance.reLoginIfNecessary();
        this.clientInstance.rateLimiter.acquire();
        request.headersReplace(clientInstance.standardHeader);

        HttpResponse<Response<JsonElement>> response = request.asObject(new GenericType<>() {
        });

        PagedResponse.PagedResponseBuilder<T> builder = PagedResponse.builder();
        builder.cursorFollows(cursorDepth);
        builder.maxCursorFollows(maxCursorDepth);
        builder.statusCode(response.getStatus());
        String ratelimit = response.getHeaders().getFirst("Ratelimit-Remaining");
        if (ratelimit != null) logger.trace("Ratelimit-Remaining: " + ratelimit);
        logger.trace("Status-Code: " + response.getStatus());
        if (response.isSuccess()) {
            List<T> data = new ArrayList<>(response.getBody().getData().size());
            for (JsonElement jsonElement : response.getBody().getData()) {
                data.add(this.clientInstance.gson.fromJson(jsonElement, type));
            }
            builder.data(data);

            if (response.getBody().getPagination() != null && response.getBody().getPagination().getCursor() != null) {
                builder.nextPage(response.getBody().getPagination().getCursor());
                builder.hasNext(true);
            } else {
                builder.hasNext(false);
            }
        }
        return builder.build();
    }
}
