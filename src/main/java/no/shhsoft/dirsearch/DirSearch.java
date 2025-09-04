package no.shhsoft.dirsearch;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import no.shhsoft.dirsearch.model.Entry;
import no.shhsoft.dirsearch.model.EntryTranslator;
import no.shhsoft.json.impl.generator.JsonGeneratorImpl;
import no.shhsoft.json.model.JsonObject;
import no.shhsoft.json.model.JsonString;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class DirSearch {

    private static final Logger LOG = Logger.getLogger(DirSearch.class.getName());
    private static final String API_PREFIX = "/api/v1/";
    private static final String DN_PREFIX = API_PREFIX + "dn/";
    private static final String SEARCH_PREFIX = API_PREFIX + "search/";
    private static final int HTTP_PORT = 8080;
    private LdapQuerier ldapQuerier;

    private static void notFoundHandler(final HttpServerExchange exchange) {
        exchange.setStatusCode(404);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send("404 Not Found");
    }

    public void runServer(final Config config) {
        initQuerier(config);
        initWebServer(config);
        LOG.info("Server available at http://localhost:" + HTTP_PORT);
    }

    private void initQuerier(final Config config) {
        ldapQuerier = new LdapQuerier(config);
    }

    private void initWebServer(final Config config) {
        final ClassPathResourceManager resourceManager = new ClassPathResourceManager(ClassLoader.getSystemClassLoader(), "web");
        final ResourceHandler resourceHandler = new ResourceHandler(resourceManager, DirSearch::notFoundHandler);

        final RoutingHandler routingHandler = Handlers.routing(false);
        routingHandler.add(Methods.GET, "/api/v1/foo", exchange -> {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("foo");
        });
        routingHandler.get(DN_PREFIX + "*", exchange -> {
            final String dn = exchange.getRelativePath().substring(DN_PREFIX.length());
            try {
                final String json = get(dn);
                sendJson(exchange, json);
            } catch (final Exception e) {
                LOG.log(Level.WARNING, "Unexpected error", e);
                sendJson(exchange, errorMessageToJsonString(e.getMessage()));
            }
        });
        routingHandler.get(SEARCH_PREFIX + "*", exchange -> {
            final String search = exchange.getRelativePath().substring(SEARCH_PREFIX.length());
            try {
                final String json = search(search);
                sendJson(exchange, json);
            } catch (final Exception e) {
                LOG.log(Level.WARNING, "Unexpected error", e);
                sendJson(exchange, errorMessageToJsonString(e.getMessage()));
            }
        });
        routingHandler.setFallbackHandler(resourceHandler);

        final Undertow server = Undertow.builder()
            .addHttpListener(HTTP_PORT, "0.0.0.0")
            .setHandler(routingHandler)
            .build();
        server.start();
    }

    private void sendJson(final HttpServerExchange exchange, final String json) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(json);
    }

    private String search(final String query) {
        final Map<String, Entry> searchResult = ldapQuerier.search(query);
        return searchResultToJsonString(searchResult);
    }

    private String get(final String dn) {
        final Entry entry = ldapQuerier.get(dn);
        final Map<String, Entry> searchResult = new HashMap<>();
        searchResult.put(dn, entry);
        return searchResultToJsonString(searchResult);
    }

    public static String searchResultToJsonString(final Map<String, Entry> searchResult) {
        final JsonObject json = new JsonObject();
        json.put("objects", EntryTranslator.toJson(searchResult));
        return jsonToString(json);
    }

    private static String errorMessageToJsonString(final String message) {
        final JsonObject json = new JsonObject();
        json.put("error", JsonString.get(message));
        return jsonToString(json);
    }

    private static String jsonToString(final JsonObject jsonObject) {
        return new JsonGeneratorImpl().generate(jsonObject);
    }

    public static void main(final String[] args) {
        new DirSearch().runServer(new Config());
    }

}
