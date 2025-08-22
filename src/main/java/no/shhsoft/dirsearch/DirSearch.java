package no.shhsoft.dirsearch;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import no.shhsoft.json.impl.generator.HumanReadableJsonGeneratorImpl;
import no.shhsoft.json.model.JsonArray;
import no.shhsoft.json.model.JsonObject;
import no.shhsoft.json.model.JsonString;
import no.shhsoft.security.MultiTrustStoreX509TrustManager;

import java.util.HashMap;
import java.util.List;
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
    private LdapHelper ldapHelper;

    private static void notFoundHandler(final HttpServerExchange exchange) {
        exchange.setStatusCode(404);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send("404 Not Found");
    }

    public void runServer(final Config config) {
        initHelper(config);
        initWebServer(config);
        LOG.info("Server available at http://localhost:" + HTTP_PORT);
    }

    private void initHelper(final Config config) {
        ldapHelper = LdapHelper.forConfig(config);
    }

    private void initWebServer(final Config config) {
        final String caCertsFile = config.getCaCertsFile();
        if (caCertsFile != null) {
            MultiTrustStoreX509TrustManager
                .withDefaultTrustStore()
                .withCaCertificateFile(caCertsFile)
                .installAsDefault();
        }
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
            .addHttpListener(HTTP_PORT, "localhost")
            .setHandler(routingHandler)
            .build();
        server.start();
    }

    private void sendJson(final HttpServerExchange exchange, final String json) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(json);
    }

    private String search(final String query) {
        final Map<String, Map<String, List<String>>> searchResult = ldapHelper.search(query);
        return searchResultToJsonString(searchResult);
    }

    private String get(final String dn) {
        final Map<String, List<String>> getResult = ldapHelper.get(dn);
        final Map<String, Map<String, List<String>>> searchResult = new HashMap<>();
        searchResult.put(dn, getResult);
        return searchResultToJsonString(searchResult);
    }

    public static String searchResultToJsonString(final Map<String, Map<String, List<String>>> searchResult) {
        final JsonObject json = new JsonObject();
        json.put("objects", searchResultToJson(searchResult));
        return jsonToString(json);
    }

    public static JsonObject searchResultToJson(final Map<String, Map<String, List<String>>> searchResult) {
        final JsonObject objects = new JsonObject();
        for (final Map.Entry<String, Map<String, List<String>>> objectEntry : searchResult.entrySet()) {
            final JsonObject attributes = attributesToJson(objectEntry.getValue());
            objects.put(objectEntry.getKey(), attributes);
        }
        return objects;
    }

    public static String attributesToJsonString(final Map<String, List<String>> attributes) {
        return jsonToString(attributesToJson(attributes));
    }

    public static JsonObject attributesToJson(final Map<String, List<String>> attributes) {
        final JsonObject attributesObject = new JsonObject();
        for (final Map.Entry<String, List<String>> attributeEntry : attributes.entrySet()) {
            final JsonArray attributeValues = new JsonArray();
            for (final String attributeValue : attributeEntry.getValue()) {
                attributeValues.add(JsonString.get(attributeValue));
            }
            attributesObject.put(attributeEntry.getKey(), attributeValues);
        }
        return attributesObject;
    }

    private static String errorMessageToJsonString(final String message) {
        final JsonObject json = new JsonObject();
        json.put("error", JsonString.get(message));
        return jsonToString(json);
    }

    private static String jsonToString(final JsonObject jsonObject) {
        return new HumanReadableJsonGeneratorImpl().generate(jsonObject);
    }

    public static void main(final String[] args) {
        new DirSearch().runServer(new Config());
    }

}
