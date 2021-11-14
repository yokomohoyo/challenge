package org.cabro.cyverse.challenge.a.client;

import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Executes a get request to the friends quote api
 * GET https://friends-quotes-api.herokuapp.com/quotes
 */
public class FriendsQuoteApiClient {
    private static final String BASE_API_URL = "https://friends-quotes-api.herokuapp.com";
    private static final String RANDOM_ROUTE = "/quotes";
    private static final Logger log = LogManager.getLogger(FriendsQuoteApiClient.class);

    public static Response getQuotes(Integer count) {
        Response rv = null;
        try {
            log.debug("Making api request");
            rv = Request.get(BASE_API_URL + RANDOM_ROUTE + "/" + count)
                    .execute();
            log.debug("api request fulfilled.");
        } catch (IOException e) {
            log.error("Unable to communicate with endpoint...");
        }

        return rv;
    }
}
