package org.cabro.cyverse.challenge.a;

import com.google.gson.Gson;
import io.nats.client.Connection;
import io.nats.client.Nats;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cabro.cyverse.challenge.a.client.FriendsQuoteApiClient;
import org.cabro.cyverse.challenge.a.model.Quote;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Polls API endpoint to collect random friends quotes and
 * pops it into a NATS queue
 */
public class Publisher {
    private static final long POLLING_INTERVAL = 5000;
    private static int count;
    private static String natsServer;
    private final Logger log = LogManager.getLogger(Publisher.class);
    private final Gson gson = new Gson();

    public Publisher(int count, String ns) {
        Publisher.natsServer = ns;
        Publisher.count = count;
    }

    public void start() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            Response res = FriendsQuoteApiClient.getQuotes(count);
            try {
                Quote[] quotes = gson.fromJson(res.returnContent().asString(), Quote[].class);
                for (Quote quote : quotes) {
                    pushToNats(quote);
                }
            } catch (IOException | InterruptedException e) {
                log.error("Unable to process entry.", e);
            }
        }, 0, POLLING_INTERVAL, TimeUnit.MILLISECONDS);
    }

    public void pushToNats(Quote q) throws IOException, InterruptedException {
        log.debug("Sending quote to queue_a on + " + natsServer );
        Connection nc = Nats.connect(natsServer);
        nc.publish("queue_a", gson.toJson(q).getBytes(StandardCharsets.UTF_8));
        log.debug("Send complete.");
    }
}
