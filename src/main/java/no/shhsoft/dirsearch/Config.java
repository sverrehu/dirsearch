package no.shhsoft.dirsearch;

import java.util.Map;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Config {

    public static final String ENV_LDAP_TLS = "LDAP_TLS";
    public static final String ENV_LDAP_HOST = "LDAP_HOST";
    public static final String ENV_LDAP_PORT = "LDAP_PORT";
    public static final String ENV_LDAP_BASE_DN = "LDAP_BASE_DN";
    public static final String ENV_LDAP_USER = "LDAP_USER";
    public static final String ENV_LDAP_PASSWORD = "LDAP_PASSWORD";
    public static final String ENV_DN_CACHE_TTL_MIN = "DN_CACHE_TTL_MIN";
    public static final String ENV_CA_CERTS_FILE = "CA_CERTS_FILE";
    public static final String ENV_FIND_INDIRECT_MEMBERSHIPS = "FIND_INDIRECT_MEMBERSHIPS";
    private static final int DEFAULT_DN_CACHE_TTL_MIN = 5;
    private static final boolean DEFAULT_FIND_INDIRECT_MEMBERSHIPS = true;

    private final Map<String, String> props;

    public Config(final Map<String, String> props) {
        this.props = props;
    }

    public Config() {
        this(System.getenv());
    }

    public String getLdapHost() {
        return getRequired(ENV_LDAP_HOST);
    }

    public boolean isLdapTls() {
        final String tlsProp = props.get(ENV_LDAP_TLS);
        if (tlsProp != null) {
            return "true".equals(tlsProp);
        }
        return getLdapPort() == 636;
    }

    public int getLdapPort() {
        return toInt(getRequired(ENV_LDAP_PORT), ENV_LDAP_PORT);
    }

    public String getLdapBaseDn() {
        return getRequired(ENV_LDAP_BASE_DN);
    }

    public String getLdapUser() {
        return getRequired(ENV_LDAP_USER);
    }

    public String getEnvLdapPassword() {
        return getRequired(ENV_LDAP_PASSWORD);
    }

    public String getCaCertsFile() {
        return props.get(ENV_CA_CERTS_FILE);
    }

    public int getDnCacheTtlMin() {
        final String value = props.get(ENV_DN_CACHE_TTL_MIN);
        if (value == null) {
            return DEFAULT_DN_CACHE_TTL_MIN;
        }
        return toInt(value, ENV_DN_CACHE_TTL_MIN);
    }

    public boolean isFindIndirectMemberships() {
        final String s = props.get(ENV_FIND_INDIRECT_MEMBERSHIPS);
        if (s == null) {
            return DEFAULT_FIND_INDIRECT_MEMBERSHIPS;
        }
        return "true".equalsIgnoreCase(s);
    }

    private static int toInt(final String value, final String property) {
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            throw new RuntimeException("Property \"" + property + "\" must be an integer");
        }
    }

    private String getRequired(final String name) {
        final String value = props.get(name);
        if (value == null) {
            throw new RuntimeException("Missing required property \"" + name + "\"");
        }
        return value;
    }

}
