package de.tsearch.tclient;

import de.tsearch.tclient.data.PagedResponse;
import de.tsearch.tclient.http.respone.Video;
import kong.unirest.GetRequest;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HighlightClient extends GenericClient<Video> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public HighlightClient(TClientInstance clientInstance) {
        super(clientInstance, Video.class);
    }

    public List<Video> getAllActiveHighlightsUncached(List<String> clipsIds) {
        ArrayList<Video> clips = new ArrayList<>();
        List<Future<List<Video>>> futures = new ArrayList<>();
        final int batchSize = 100;
        for (int round = 0; round < Math.ceil(((float) clipsIds.size()) / batchSize); round++) {
            List<String> list = clipsIds.subList(round * batchSize, Math.min(clipsIds.size(), (round + 1) * batchSize));
            GetRequest getRequest = Unirest
                    .get("https://api.twitch.tv/helix/videos")
                    .queryString("first", batchSize)
                    .queryString("id", list)
                    .queryString("type", "highlight");
            futures.add(this.clientInstance.executorService.submit(() -> this.executeRequest(getRequest).getData()));
        }

        for (Future<List<Video>> future : futures) {
            try {
                List<Video> list = future.get();
                if (list != null) clips.addAll(list);
            } catch (ExecutionException | InterruptedException ignored) {
            }
        }

        return clips;
    }

    public PagedResponse<Video> getAllHighlightsUncached(long broadcasterId) {
        GetRequest request = Unirest
                .get("https://api.twitch.tv/helix/videos")
                .queryString("user_id", broadcasterId)
                .queryString("first", 100)
                .queryString("type", "highlight");
        return requestWithCursorFollowing(request, Integer.MAX_VALUE);
    }

    public PagedResponse<Video> getLatestHighlightsUncached(long broadcasterId) {
        GetRequest request = Unirest
                .get("https://api.twitch.tv/helix/videos")
                .queryString("user_id", broadcasterId)
                .queryString("first", 100)
                .queryString("type", "highlight");
        return requestWithCursorFollowing(request, 1);
    }
}
