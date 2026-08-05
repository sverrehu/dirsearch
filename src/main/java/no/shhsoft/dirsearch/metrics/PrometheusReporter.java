package no.shhsoft.dirsearch.metrics;

import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.core.metrics.Gauge;

public final class PrometheusReporter {

    private static final String PROMETHEUS_NAMESPACE = "dirsearch";
    private final Counter errorsTotal = Counter.builder()
                                                       .name(PROMETHEUS_NAMESPACE + "_errors_total")
                                                       .help("total number of requests resulting in error")
                                                       .register();
    private final Counter searchesTotal = Counter.builder()
                                               .name(PROMETHEUS_NAMESPACE + "_searches_total")
                                               .help("total number of search requests")
                                               .register();
    private final Counter dnLookupsTotal = Counter.builder()
                                                 .name(PROMETHEUS_NAMESPACE + "_dn_lookups_total")
                                                 .help("total number of DN lookup requests")
                                                 .register();

    public void incErrorsTotal() {
        errorsTotal.inc();
    }

    public void incSearchesTotal() {
        searchesTotal.inc();
    }

    public void incDnLookupsTotal() {
        dnLookupsTotal.inc();
    }

}
