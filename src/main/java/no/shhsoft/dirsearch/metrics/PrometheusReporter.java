package no.shhsoft.dirsearch.metrics;

import io.prometheus.metrics.core.metrics.Counter;

public final class PrometheusReporter {

    private static final String PROMETHEUS_NAMESPACE = "dirsearch";
    private final Counter errorsTotal = Counter.builder()
                                                       .name(PROMETHEUS_NAMESPACE + "_errors_total")
                                                       .help("total number of requests resulting in error")
                                                       .register();
    private final Counter searchesTotal = Counter.builder()
                                               .name(PROMETHEUS_NAMESPACE + "_successful_searches_total")
                                               .help("total number of incoming search requests")
                                               .register();
    private final Counter dnLookupsTotal = Counter.builder()
                                                 .name(PROMETHEUS_NAMESPACE + "_successful_dn_lookups_total")
                                                 .help("total number of incoming DN lookup requests")
                                                 .register();
    private final Counter ldapServerRequestsTotal = Counter.builder()
                                                  .name(PROMETHEUS_NAMESPACE + "_ldap_server_requests_total")
                                                  .help("total number of LDAP server requests")
                                                  .register();
    private final Counter ldapCacheHitsTotal = Counter.builder()
                                                  .name(PROMETHEUS_NAMESPACE + "_ldap_cache_hits_total")
                                                  .help("total number of LDAP cache hits")
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

    public void incLdapServerRequestsTotal() {
        ldapServerRequestsTotal.inc();
    }

    public void incLdapCacheHitsTotal() {
        ldapCacheHitsTotal.inc();
    }

}
