package no.shhsoft.dirsearch;

import no.shhsoft.dirsearch.model.Entry;
import no.shhsoft.dirsearch.model.EntryTranslator;
import no.shhsoft.utils.cache.TimeoutCache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class LdapQuerier {

    private static final Logger LOG = Logger.getLogger(LdapQuerier.class.getName());
    private final LdapHelper ldapHelper;
    private final TimeoutCache<String, Entry> dnCache;
    private final long dnCacheTtlMs;

    public LdapQuerier(final Config config) {
        ldapHelper = LdapHelper.forConfig(config);
        dnCache = new TimeoutCache<>();
        final int dnCacheTtlMin = config.getDnCacheTtlMin();
        LOG.info("Caching DN lookups for " + dnCacheTtlMin + " minutes");
        dnCacheTtlMs = dnCacheTtlMin * 60L * 1000L;
    }

    public Map<String, Entry> search(final String query) {
        final Map<String, Map<String, List<String>>> searchResult = ldapHelper.search(query);
        final Map<String, Entry> result = new HashMap<>();
        for (final Map.Entry<String, Map<String, List<String>>> searchResultEntry : searchResult.entrySet()) {
            final String dn = searchResultEntry.getKey();
            final Entry entry = EntryTranslator.fromDnAndAttributes(dn, searchResultEntry.getValue());
            result.put(dn, entry);
            addToCache(dn, entry);
        }
        return result;
    }

    public Entry get(final String dn) {
        Entry value = dnCache.get(dn);
        if (value == null) {
            value = EntryTranslator.fromDnAndAttributes(dn, ldapHelper.get(dn));
            addToCache(dn, value);
        }
        return value;
    }

    private void addToCache(final String dn, final Entry value) {
        dnCache.put(dn, value, System.currentTimeMillis() + dnCacheTtlMs);
    }

}
