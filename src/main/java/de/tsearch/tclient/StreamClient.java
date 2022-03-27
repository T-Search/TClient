package de.tsearch.tclient;

import de.tsearch.tclient.data.PagedResponse;
import de.tsearch.tclient.http.respone.Stream;
import kong.unirest.GetRequest;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class StreamClient extends GenericClient<Stream> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public StreamClient(TClientInstance clientInstance) {
        super(clientInstance, Stream.class);
    }

    public List<Stream> getOnlineStreams(List<Long> broadcasterIds) {
        ArrayList<Stream> onlineStreams = new ArrayList<>();
        final int batchSize = 100;
        for (int round = 0; round < Math.ceil(((float) broadcasterIds.size()) / batchSize); round++) {
            List<Long> list = broadcasterIds.subList(round * batchSize, Math.min(broadcasterIds.size(), (round + 1) * batchSize));
            GetRequest request = Unirest
                    .get("https://api.twitch.tv/helix/streams")
                    .queryString("first", batchSize)
                    .queryString("user_id", list);
            onlineStreams.addAll(executeRequest(request, 0, 0).getData());
        }

        return onlineStreams;
    }

    public PagedResponse<Stream> getStreams() {
        GetRequest request = Unirest
                .get("https://api.twitch.tv/helix/streams")
                .queryString("first", 100)
                .queryString("language", "de");
        return executeRequest(request, 0, 0);
    }
}
