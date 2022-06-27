package no.shhsoft.dirsearch;

import java.util.Map;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Config {

    public static final String ENV_LDAP_HOST = "LDAP_HOST";
    public static final String ENV_LDAP_PORT = "LDAP_PORT";
    public static final String ENV_LDAP_BASE_DN = "LDAP_BASE_DN";
    public static final String ENV_LDAP_USER = "LDAP_USER";
    public static final String ENV_LDAP_PASSWORD = "LDAP_PASSWORD";

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

    public int getLdapPort() {
        final String value = getRequired(ENV_LDAP_PORT);
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            throw new RuntimeException("Property \"" + ENV_LDAP_PORT + "\" must be an integer");
        }
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

    private String getRequired(final String name) {
        final String value = props.get(name);
        if (value == null) {
            throw new RuntimeException("Missing required property \"" + name + "\"");
        }
        return value;
    }

}
