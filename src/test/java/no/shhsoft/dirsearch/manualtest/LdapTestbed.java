package no.shhsoft.dirsearch.manualtest;

import no.shhsoft.dirsearch.Config;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

final class LdapTestbed {

    private static final String PROPERTIES_FILE = System.getProperty("user.home") + "/.ldap-testbed.properties";

    private LdapTestbed() {
    }

    public static Config getConfig() {
        final Properties props = new Properties();
        final Map<String, String> map = new HashMap<>();
        try {
            props.load(new FileReader(PROPERTIES_FILE));
            map.put(Config.ENV_LDAP_HOST, Objects.requireNonNull(props.getProperty("host")));
            map.put(Config.ENV_LDAP_PORT, Objects.requireNonNull(props.getProperty("port")));
            map.put(Config.ENV_LDAP_BASE_DN, Objects.requireNonNull(props.getProperty("baseDn")));
            map.put(Config.ENV_LDAP_USER, Objects.requireNonNull(props.getProperty("userDn")));
            map.put(Config.ENV_LDAP_PASSWORD, Objects.requireNonNull(props.getProperty("password")));
            return new Config(map);
        } catch (final IOException e) {
            throw new RuntimeException("Unable to read " + PROPERTIES_FILE);
        }
    }

}
