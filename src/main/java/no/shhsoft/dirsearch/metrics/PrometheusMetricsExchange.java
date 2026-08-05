package no.shhsoft.dirsearch.metrics;

import io.prometheus.metrics.exporter.common.PrometheusScrapeHandler;
import io.prometheus.metrics.model.registry.PrometheusRegistry;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public final class PrometheusMetricsExchange
implements HttpHandler {

    private final PrometheusScrapeHandler handler;

    public PrometheusMetricsExchange() {
        this.handler = new PrometheusScrapeHandler(PrometheusRegistry.defaultRegistry);
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange)
    throws Exception {
        handler.handleRequest(new HttpExchangeAdapter(exchange));
    }

}
