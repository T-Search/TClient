package de.tsearch.tclient;

import de.tsearch.tclient.http.respone.User;
import kong.unirest.GetRequest;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class UserClient extends GenericClient<User> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public UserClient(TClientInstance clientInstance) {
        super(clientInstance, User.class);
    }

    public List<User> getUserByUsername(List<String> usernames) {
        ArrayList<User> clips = new ArrayList<>();
        List<Future<List<User>>> futures = new ArrayList<>();
        final int batchSize = 100;
        for (int round = 0; round < Math.ceil(((float) usernames.size()) / batchSize); round++) {
            List<String> list = usernames.subList(round * batchSize, Math.min(usernames.size(), (round + 1) * batchSize));
            GetRequest getRequest = Unirest
                    .get("https://api.twitch.tv/helix/users")
                    .queryString("login", list);
            futures.add(this.clientInstance.executorService.submit(() -> this.executeRequest(getRequest).getData()));
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) this.clientInstance.executorService;
            logger.debug("Active task count: {}", threadPoolExecutor.getActiveCount());
        }

        for (Future<List<User>> future : futures) {
            try {
                List<User> list = future.get();
                if (list != null) clips.addAll(list);
            } catch (ExecutionException | InterruptedException ignored) {
            }
        }

        return clips;
    }
}
