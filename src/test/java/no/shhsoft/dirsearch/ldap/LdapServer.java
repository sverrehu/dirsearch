package no.shhsoft.dirsearch.ldap;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.sdk.LDAPException;
import no.shhsoft.dirsearch.Config;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class LdapServer {

    public static final String PERSON1_CN = "person1";
    public static final String PERSON2_CN = "person2";
    private static final String LDAP_DOMAIN = "example.com";
    private static final String LDAP_BASE_DN = "dc=example,dc=com";
    private static final String LDAP_ADMIN_USER = "cn=admin," + LDAP_BASE_DN;
    private static final String LDAP_ADMIN_PASSWORD = "admin";
    public static final String PERSONS_GROUP_CN = "persons";
    public static final String PERSONS_GROUP = "cn=" + PERSONS_GROUP_CN + ",ou=Groups," + LDAP_BASE_DN;
    public static final String LDAP_ADMIN_DN = "cn=admin," + LDAP_BASE_DN;
    private InMemoryDirectoryServer ldap;

    public void start() {
        try {
            final InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(LDAP_BASE_DN);
            config.addAdditionalBindCredentials(LDAP_ADMIN_DN, LDAP_ADMIN_PASSWORD);
            config.setSchema(null);
            ldap = new InMemoryDirectoryServer(config);
            ldap.importFromLDIF(true, resourceToFile("/ldap/unboundid-bootstrap.ldif"));
            ldap.startListening();
        } catch (final LDAPException e) {
            throw new RuntimeException(e);
        }
    }

    private static File resourceToFile(final String resource) {
        try {
            return Paths.get(LdapServer.class.getResource(resource).toURI()).toFile();
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        ldap.shutDown(true);
    }

    public String getLdapBaseDn() {
        return LDAP_BASE_DN;
    }

    public String getLdapHost() {
        return "127.0.0.1";
    }

    public int getLdapPort() {
        return ldap.getListenPort();
    }

    public Config getConfig() {
        final Map<String, String> props = new HashMap<>();
        props.put(Config.ENV_LDAP_HOST, getLdapHost());
        props.put(Config.ENV_LDAP_PORT, String.valueOf(getLdapPort()));
        props.put(Config.ENV_LDAP_BASE_DN, LDAP_BASE_DN);
        props.put(Config.ENV_LDAP_USER, LDAP_ADMIN_USER);
        props.put(Config.ENV_LDAP_PASSWORD, LDAP_ADMIN_PASSWORD);
        return new Config(props);
    }

}
