package org.cabro.cyverse.challenge.a.client;

import com.google.gson.Gson;
import org.apache.hc.client5.http.fluent.Response;
import org.cabro.cyverse.challenge.a.model.Quote;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class FriendsQuoteApiClientTest {
    private final Gson gson = new Gson();

    @Test
    void get5Quotes() throws IOException {
        Response r = FriendsQuoteApiClient.getQuotes(5);
        Quote[] quotes = gson.fromJson(r.returnContent().asString(), Quote[].class);
        Assertions.assertEquals(5, quotes.length);
    }

    @Test
    void get1Quote() throws IOException {
        Response r = FriendsQuoteApiClient.getQuotes(1);
        Quote[] quotes = gson.fromJson(r.returnContent().asString(), Quote[].class);
        Assertions.assertEquals(1, quotes.length);
    }

    @Test
    void get0Quotes() throws IOException {
        Response r = FriendsQuoteApiClient.getQuotes(0);
        Quote[] quotes = gson.fromJson(r.returnContent().asString(), Quote[].class);
        Assertions.assertEquals(0, quotes.length);
    }
}