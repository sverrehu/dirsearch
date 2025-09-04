package no.shhsoft.dirsearch;

import no.shhsoft.utils.cache.TimeoutCache;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class LdapQuerier {

    private static final Logger LOG = Logger.getLogger(LdapQuerier.class.getName());
    private final LdapHelper ldapHelper;
    private final TimeoutCache<String, Map<String, List<String>>> dnCache;
    private final long dnCacheTtlMs;

    public LdapQuerier(final Config config) {
        ldapHelper = LdapHelper.forConfig(config);
        dnCache = new TimeoutCache<>();
        final int dnCacheTtlMin = config.getDnCacheTtlMin();
        LOG.info("Caching DN lookups for " + dnCacheTtlMin + " minutes");
        dnCacheTtlMs = dnCacheTtlMin * 60L * 1000L;
    }

    public Map<String, Map<String, List<String>>> search(final String query) {
        return ldapHelper.search(query);
    }

    public Map<String, List<String>> get(final String dn) {
        Map<String, List<String>> value = dnCache.get(dn);
        if (value == null) {
            value = ldapHelper.get(dn);
            dnCache.put(dn, value, System.currentTimeMillis() + dnCacheTtlMs);
        }
        return value;
    }

}
