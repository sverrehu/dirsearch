package no.shhsoft.dirsearch;

import no.shhsoft.dirsearch.model.Entry;
import no.shhsoft.dirsearch.model.EntryTranslator;
import no.shhsoft.utils.cache.TimeoutCache;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public final class LdapQuerier {

    private static final Logger LOG = Logger.getLogger(LdapQuerier.class.getName());
    private final LdapHelper ldapHelper;
    private final TimeoutCache<String, Entry> dnCache;
    private final long dnCacheTtlMs;
    private final boolean findIndirectMemberships;

    public LdapQuerier(final Config config) {
        ldapHelper = LdapHelper.forConfig(config);
        dnCache = new TimeoutCache<>();
        final int dnCacheTtlMin = config.getDnCacheTtlMin();
        LOG.info("Caching DN lookups for " + dnCacheTtlMin + " minutes");
        dnCacheTtlMs = dnCacheTtlMin * 60L * 1000L;
        findIndirectMemberships = config.isFindIndirectMemberships();
    }

    public Map<String, Entry> search(final String query) {
        final Map<String, Map<String, List<String>>> searchResult = ldapHelper.search(query);
        final Map<String, Entry> result = new HashMap<>();
        for (final Map.Entry<String, Map<String, List<String>>> searchResultEntry : searchResult.entrySet()) {
            final String dn = searchResultEntry.getKey();
            final Entry entry = EntryTranslator.fromDnAndAttributes(dn, searchResultEntry.getValue());
            addToCache(dn, entry);
            fillIndirectMembershipData(entry);
            result.put(dn, entry);
        }
        return result;
    }

    public Entry get(final String dn) {
        Entry entry = dnCache.get(dn);
        if (entry == null) {
            entry = EntryTranslator.fromDnAndAttributes(dn, ldapHelper.get(dn));
            addToCache(dn, entry);
            fillIndirectMembershipData(entry);
        }
        return entry;
    }

    private void fillIndirectMembershipData(final Entry entry) {
        if (!findIndirectMemberships) {
            return;
        }
        fillIndirectMemberOf(entry);
        // This next one leads to far too many queries. Commenting out for now (2025-09-04).
        //fillIndirectMembers(entry);
    }

    private void fillIndirectMemberOf(final Entry entry) {
        final Set<String> dnsSeen = new HashSet<>();
        dnsSeen.add(entry.getDn());
        for (final String memberOf : entry.getMemberOf()) {
            if (entry.getIndirectMemberOf().contains(memberOf)) {
                LOG.warning("Cyclic membership detected (checkpoint 1)");
                continue;
            }
            if (dnsSeen.contains(memberOf)) {
                LOG.warning("Cyclic membership detected (checkpoint 2)");
                continue;
            }
            dnsSeen.add(memberOf);
            final Entry groupEntry = get(memberOf);
            entry.addIndirectMemberOf(createSetExcept(groupEntry.getMemberOf(), entry.getMemberOf()));
            entry.addIndirectMemberOf(createSetExcept(groupEntry.getIndirectMemberOf(), entry.getMemberOf()));
        }
    }

    private void fillIndirectMembers(final Entry entry) {
        final Set<String> dnsSeen = new HashSet<>();
        dnsSeen.add(entry.getDn());
        for (final String member : entry.getMembers()) {
            if (entry.getIndirectMembers().contains(member)) {
                LOG.warning("Cyclic membership detected (checkpoint 3)");
                continue;
            }
            if (dnsSeen.contains(member)) {
                LOG.warning("Cyclic membership detected (checkpoint 4)");
                continue;
            }
            dnsSeen.add(member);
            final Entry memberEntry = get(member);
            entry.addIndirectMembers(createSetExcept(memberEntry.getMembers(), entry.getMembers()));
            entry.addIndirectMembers(createSetExcept(memberEntry.getIndirectMembers(), entry.getMembers()));
        }
    }

    private Collection<String> createSetExcept(final Collection<String> toInclude, final Collection<String> except) {
        final Set<String> result = new HashSet<>();
        for (final String s : toInclude) {
            if (!except.contains(s)) {
                result.add(s);
            }
        }
        return result;
    }

    private void addToCache(final String dn, final Entry value) {
        dnCache.put(dn, value, System.currentTimeMillis() + dnCacheTtlMs);
    }

}
