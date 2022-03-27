package de.tsearch.tclient;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import de.tsearch.tclient.data.PagedResponse;
import de.tsearch.tclient.http.respone.Game;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class GameClient extends GenericClient<Game> {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final LoadingCache<Long, Optional<Game>> cache;

    public GameClient(TClientInstance clientInstance) {
        super(clientInstance, Game.class);

        GameClient gameClient = this;
        CacheLoader<Long, Optional<Game>> loader = new CacheLoader<>() {
            @Override
            public Optional<Game> load(Long key) throws Exception {
                return gameClient.getGameByIdUncached(key);
            }
        };
        cache = CacheBuilder.newBuilder().maximumSize(25).build(loader);
    }

    public Optional<Game> getGameById(long gameId) {
        try {
            return this.cache.get(gameId);
        } catch (ExecutionException e) {
            return Optional.empty();
        }
    }

    public Optional<Game> getGameByIdUncached(long gameId) {
        PagedResponse<Game> games = executeRequest(Unirest.get("https://api.twitch.tv/helix/games").queryString("id", gameId));

        if (games.getData().size() == 1) {
            return Optional.of(games.getData().get(0));
        } else {
            return Optional.empty();
        }
    }

}
