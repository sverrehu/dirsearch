package no.shhsoft.dirsearch.metrics;

import io.prometheus.metrics.exporter.common.PrometheusHttpExchange;
import io.prometheus.metrics.exporter.common.PrometheusHttpRequest;
import io.prometheus.metrics.exporter.common.PrometheusHttpResponse;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import no.shhsoft.utils.UncheckedIoException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

public final class HttpExchangeAdapter
implements PrometheusHttpExchange {

    private final Request request;
    private final Response response;

    private static final class Request
    implements PrometheusHttpRequest {

        private final HttpServerExchange exchange;

        public Request(final HttpServerExchange exchange) {
            this.exchange = exchange;
        }

        @Override
        public String getQueryString() {
            return exchange.getQueryString();
        }

        @Override
        public Enumeration<String> getHeaders(final String name) {
            final Collection<HttpString> headerNamesHS = exchange.getRequestHeaders().getHeaderNames();
            final Collection<String> headerNames = new ArrayList<>();
            for (final HttpString headerNameHS : headerNamesHS) {
                headerNames.add(headerNameHS.toString());
            }
            return Collections.enumeration(headerNames);
        }

        @Override
        public String getMethod() {
            return exchange.getRequestMethod().toString();
        }

        @Override
        public String getRequestPath() {
            return exchange.getRequestPath();
        }

    }

    private static final class Response
    implements PrometheusHttpResponse {

        private final HttpServerExchange exchange;

        public Response(final HttpServerExchange exchange) {
            this.exchange = exchange;
        }

        @Override
        public void setHeader(final String name, final String value) {
            exchange.getResponseHeaders().add(HttpString.tryFromString(name), value);
        }

        @Override
        public OutputStream sendHeadersAndGetBody(final int statusCode, final int contentLength)
        throws IOException {
            return exchange.getOutputStream();
        }

    }

    public HttpExchangeAdapter(final HttpServerExchange exchange) {
        this.request = new Request(exchange);
        this.response = new Response(exchange);
    }

    @Override
    public PrometheusHttpRequest getRequest() {
        return request;
    }

    @Override
    public PrometheusHttpResponse getResponse() {
        return response;
    }

    @Override
    public void handleException(final IOException e) {
        throw new UncheckedIoException(e);
    }

    @Override
    public void handleException(final RuntimeException e) {
        throw new RuntimeException(e);
    }

    @Override
    public void close() {
    }

}
