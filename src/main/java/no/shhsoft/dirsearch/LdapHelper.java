package no.shhsoft.dirsearch;

import no.shhsoft.ldap.LdapUtils;
import no.shhsoft.ldap.UncheckedNamingException;
import no.shhsoft.security.MultiTrustStoreX509TrustManager;

import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class LdapHelper {

    private static final Logger LOG = Logger.getLogger(LdapHelper.class.getName());
    private static final String[] SEARCH_ATTRIBUTES = {
        "cn",
        "displayName",
        "distinguishedName",
        "dn",
        "mail",
        "member",
        "memberOf",
        "name",
        "sAMAccountName",
        "uid",
        "uniqueMember",
        "userPrincipalName",
    };
    private final String baseDn;
    private final String ldapUrl;
    private final String userDn;
    private final char[] password;
    private final Object contextLock = new Object();
    private LdapContext context;

    private interface Searcher {

        NamingEnumeration<SearchResult> search(String query)
        throws NamingException;

    }

    private LdapHelper(final boolean isTls, final String host, final int port, final String baseDn, final String userDn, final char[] password) {
        this.ldapUrl = (isTls ? "ldaps" : "ldap") + "://" + host + ":" + port + "/" + baseDn;
        this.baseDn = baseDn;
        this.userDn = userDn;
        this.password = password;
    }

    public static LdapHelper forConfig(final Config config) {
        final boolean isTls = config.isLdapTls();
        final String host = config.getLdapHost();
        final int port = config.getLdapPort();
        final String baseDn = config.getLdapBaseDn();
        final String userDn = config.getLdapUser();
        final String password = config.getEnvLdapPassword();
        final String caCertsFile = config.getCaCertsFile();
        if (caCertsFile != null) {
            MultiTrustStoreX509TrustManager
            .withDefaultTrustStore()
            .withCaCertificateFile(caCertsFile)
            .installAsDefault();
        }
        return new LdapHelper(isTls, host, port, baseDn, userDn, password.toCharArray());
    }

    public Map<String, Map<String, List<String>>> search(final String query) {
        final NamingEnumeration<SearchResult> ne = runWithRetry(query1 -> {
            final SearchControls sc = new SearchControls();
            sc.setReturningAttributes(SEARCH_ATTRIBUTES);
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            return getContext().search("", "(|(cn={0})(mail={0})(sAMAccountName={0})(userPrincipalName={0}))",
                                       new String[] { query1 }, sc);
        }, query);
        return getAttributes(ne);
    }

    public Map<String, List<String>> get(final String dn) {
        final Attributes attributes = getWithRetry(dn);
        return getAttributes(attributes);
    }

    private Map<String, Map<String, List<String>>> getAttributes(final NamingEnumeration<SearchResult> ne) {
        final Map<String, Map<String, List<String>>> results = new LinkedHashMap<>();
        try {
            while (ne.hasMore()) {
                final SearchResult sr = ne.next();
                results.put(sr.getNameInNamespace(), getAttributes(sr.getAttributes()));
            }
        } catch (final PartialResultException e) {
            LOG.info("Ignoring PartialResultException: " + e.getMessage());
        } catch (final NamingException e) {
            throw new UncheckedNamingException(e);
        }
        return results;
    }

    private Map<String, List<String>> getAttributes(final Attributes attributes) {
        try {
            final Map<String, List<String>> attributeMap = new LinkedHashMap<>();
            final NamingEnumeration<? extends Attribute> ne = attributes.getAll();
            while (ne.hasMore()) {
                final ArrayList<String> valuesList = new ArrayList<>();
                final Attribute attribute = ne.next();
                final NamingEnumeration<?> values = attribute.getAll();
                while (values.hasMore()) {
                    valuesList.add(values.next().toString());
                }
                valuesList.sort(String::compareToIgnoreCase);
                attributeMap.put(attribute.getID(), valuesList);
            }
            return attributeMap;
        } catch (final NamingException e) {
            throw new UncheckedNamingException(e);
        }
    }

    private LdapContext getContext() {
        synchronized (contextLock) {
            if (context == null) {
                context = LdapUtils.connect(ldapUrl, userDn, password, true);
            }
            return context;
        }
    }

    private NamingEnumeration<SearchResult> runWithRetry(final Searcher searcher, final String query) {
        synchronized (contextLock) {
            final LdapContext ldapContext = getContext();
            try {
                return searcher.search(query);
            } catch (final NamingException | UncheckedNamingException e) {
                LOG.info("Got NamingException. Retrying. " + e.getMessage());
                try {
                    ldapContext.close();
                } catch (final Exception e2) {
                    LOG.fine("Ignoring exception when closing LdapContext");
                }
                context = null;
                try {
                    return searcher.search(query);
                } catch (final NamingException e3) {
                    throw new UncheckedNamingException(e3);
                }
            }
        }
    }

    private Attributes getWithRetry(final String dn) {
        synchronized (contextLock) {
            final LdapContext ldapContext = getContext();
            final String rdn = makeRelative(dn, baseDn);
            LOG.info("Looking up \"" + rdn + "\"");
            try {
                return ldapContext.getAttributes(rdn, SEARCH_ATTRIBUTES);
            } catch (final NamingException | UncheckedNamingException e) {
                LOG.info("Got NamingException. Retrying. " + e.getMessage());
                try {
                    ldapContext.close();
                } catch (final Exception e2) {
                    LOG.fine("Ignoring exception when closing LdapContext");
                }
                context = null;
                try {
                    return getContext().getAttributes(rdn, SEARCH_ATTRIBUTES);
                } catch (final NamingException e3) {
                    throw new UncheckedNamingException(e3);
                }
            }
        }
    }

    private static String makeRelative(final String dn, final String baseDn) {
        try {
            final LdapName dnName = new LdapName(dn);
            final LdapName baseDnName = new LdapName(baseDn);
            /* Note: In LdapName attributes are ordered "in reverse" compared to RFC 2253,
             * ie. the CN is at the end. Hence, startsWith and getSuffix, instead of the
             * opposite. */
            if (!dnName.startsWith(baseDnName)) {
                return dn;
            }
            return dnName.getSuffix(baseDnName.size()).toString();
        } catch (final InvalidNameException e) {
            throw new UncheckedNamingException(e);
        }
    }

}
