package de.tsearch.tclient;

import de.tsearch.tclient.http.respone.Clip;
import de.tsearch.tclient.http.respone.Response;
import de.tsearch.tclient.http.respone.TimeWindow;
import kong.unirest.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ClipClient extends GenericClient<Clip> {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ClipClient(TClientInstance clientInstance) {
        super(clientInstance, Clip.class);
    }

    public List<Clip> getAllActiveClipsUncached(List<String> clipsIds) {
        ArrayList<Clip> clips = new ArrayList<>();
        final int batchSize = 100;
        for (int round = 0; round < Math.ceil(((float) clipsIds.size()) / batchSize); round++) {
            try {
                List<String> list = clipsIds.subList(round * batchSize, Math.min(clipsIds.size(), (round + 1) * batchSize));
                LOGGER.debug("Get active Clips index " + round * batchSize + " from " + clipsIds.size());
                this.clientInstance.reLoginIfNecessary();
                HttpResponse<Response<Clip>> response = Unirest
                        .get("https://api.twitch.tv/helix/clips")
                        .headers(this.clientInstance.standardHeader)
                        .queryString("first", batchSize)
                        .queryString("id", list)
                        .asObject(new GenericType<>() {
                        });
                if (response.getStatus() == 200) {
                    clips.addAll(response.getBody().getData());
                }
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        }

        return clips;
    }

    public List<Clip> getAllClipsInWindowUncached(long broadcasterId, Date from, Date to) {
        return this.getAllClipsInWindowUncached(broadcasterId, from, to, TimeWindow.YEAR);
    }

    public List<Clip> getAllClipsInWindowUncached(long broadcasterId, Date from, Date to, TimeWindow baseWindow) {
        LOGGER.debug("Start searching for clips with window " + baseWindow);
        Date currentFrom = from;
        List<Clip> clips = new ArrayList<>();

        while (currentFrom != to) {
            Date currentTo = getMinDate(baseWindow.getEndOfWindow(currentFrom), to);
            LOGGER.debug("Get clips in window " + rfcDate.format(currentFrom) + " - " + rfcDate.format(currentTo));
            List<Clip> currentClips = getAllClipsInWindowWithPaging(broadcasterId, currentFrom, currentTo);
            if (currentClips.size() >= 1000) {
                LOGGER.warn("To many clips in time window " + baseWindow + ". " + currentClips.size() + " clips founded");
                Optional<TimeWindow> smallerWindow = baseWindow.getSmallerWindow();
                if (smallerWindow.isPresent()) {
                    currentClips = getAllClipsInWindowUncached(broadcasterId, currentFrom, currentTo, smallerWindow.get());
                } else {
                    LOGGER.error("Cannot search more accurately for clips for broadcaster id " + broadcasterId + " from " + rfcDate.format(currentFrom) + " to " + rfcDate.format(currentTo));
                }
            }
            clips.addAll(currentClips);
            currentFrom = currentTo;
        }
        return clips;
    }

    private List<Clip> getAllClipsInWindowWithPaging(long broadcasterId, Date from, Date to) {
        HttpRequest<?> httpRequest = Unirest.get("https://api.twitch.tv/helix/clips")
                .queryString("broadcaster_id", broadcasterId)
                .queryString("first", 100)
                .queryString("started_at", rfcDate.format(from))
                .queryString("ended_at", rfcDate.format(to));

        return requestWithCursorFollowing(httpRequest);
    }

    private Date getMinDate(Date d1, Date d2) {
        if (d1.before(d2)) {
            return d1;
        } else {
            return d2;
        }
    }
}
