package org.cabro.cyverse.challenge.a;

import org.cabro.cyverse.challenge.a.model.Quote;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Need to have a local NATS listening on 4222.
 * This makes sure you can send traffic to it.
 */
class PublisherTest {
    @Test
    void testPushToNats() throws IOException, InterruptedException {
        Quote q = new Quote();
        q.character = "Chandler";
        q.quote = "Mary had a little lamb";

        Publisher p = new Publisher(1, "nats://localhost:4222");
        p.pushToNats(q);
    }
}