package de.tsearch.tclient;

import de.tsearch.tclient.data.PagedResponse;
import de.tsearch.tclient.http.respone.Clip;
import kong.unirest.GetRequest;
import kong.unirest.HttpRequest;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class ClipClient extends GenericClient<Clip> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ClipClient(TClientInstance clientInstance) {
        super(clientInstance, Clip.class);
    }

    public List<Clip> getAllActiveClipsUncached(List<String> clipsIds) {
        ArrayList<Clip> clips = new ArrayList<>();
        List<Future<List<Clip>>> futures = new ArrayList<>();
        final int batchSize = 100;
        for (int round = 0; round < Math.ceil(((float) clipsIds.size()) / batchSize); round++) {
            List<String> list = clipsIds.subList(round * batchSize, Math.min(clipsIds.size(), (round + 1) * batchSize));
            GetRequest getRequest = Unirest
                    .get("https://api.twitch.tv/helix/clips")
                    .queryString("first", batchSize)
                    .queryString("id", list);
            futures.add(this.clientInstance.executorService.submit(() -> this.executeRequest(getRequest).getData()));
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) this.clientInstance.executorService;
            logger.debug("Active task count: {}", threadPoolExecutor.getActiveCount());
        }

        for (Future<List<Clip>> future : futures) {
            try {
                List<Clip> list = future.get();
                if (list != null) clips.addAll(list);
            } catch (ExecutionException | InterruptedException ignored) {
            }
        }

        return clips;
    }

    public List<Clip> getAllClipsInWindowUncached(long broadcasterId, Instant from, Instant to) {
        return getAllClipsInWindowUncached(broadcasterId, from, to, 0);
    }

    public List<Clip> getAllClipsInWindowUncached(long broadcasterId, Instant from, Instant to, int retryCount) {
        PagedResponse<Clip> currentClips = getAllClipsInWindowWithPaging(broadcasterId, from, to);

        if (currentClips.getData().size() >= 1000) {
            logger.debug("Found {} clips {} - {}. Split request", currentClips.getData().size(), from, to);
            //Devide
            long diff = to.getEpochSecond() - from.getEpochSecond();
            Instant middle = Instant.ofEpochSecond(from.getEpochSecond() + (diff / 2));

            Future<List<Clip>> part1 = this.clientInstance.executorService.submit(() -> getAllClipsInWindowUncached(broadcasterId, from, middle));
            Future<List<Clip>> part2 = this.clientInstance.executorService.submit(() -> getAllClipsInWindowUncached(broadcasterId, middle, to));

            List<Clip> clips = new ArrayList<>();
            try {
                clips.addAll(part1.get());
                clips.addAll(part2.get());
            } catch (InterruptedException | ExecutionException e) {
                if (retryCount > 2) throw new RuntimeException(e);
                return getAllClipsInWindowUncached(broadcasterId, from, to, ++retryCount);
            }
            return clips;
        } else {
            logger.debug("Found {} clips {} - {}", currentClips.getData().size(), from, to);
            return currentClips.getData();
        }
    }

    private PagedResponse<Clip> getAllClipsInWindowWithPaging(long broadcasterId, Instant from, Instant to) {
        HttpRequest<?> httpRequest = Unirest.get("https://api.twitch.tv/helix/clips")
                .queryString("broadcaster_id", broadcasterId)
                .queryString("first", 100)
                .queryString("started_at", from.toString())
                .queryString("ended_at", to.toString());

        return requestWithCursorFollowing(httpRequest, Integer.MAX_VALUE);
    }
}
