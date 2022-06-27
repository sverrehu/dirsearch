package no.shhsoft.dirsearch.ldap;

import no.shhsoft.dirsearch.Config;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

public class LdapContainer
extends GenericContainer<LdapContainer> {

    public static final String PERSON1_CN = "person1";
    public static final String PERSON2_CN = "person2";
    private static final String LDAP_DOMAIN = "example.com";
    private static final String LDAP_BASE_DN = "dc=example,dc=com";
    private static final String LDAP_ADMIN_USER = "cn=admin," + LDAP_BASE_DN;
    private static final char[] LDAP_ADMIN_PASSWORD = "admin".toCharArray();
    public static final String PERSONS_GROUP_CN = "persons";
    public static final String PERSONS_GROUP = "cn=" + PERSONS_GROUP_CN + ",ou=Groups," + LDAP_BASE_DN;

    public LdapContainer() {
        this(DockerImageName.parse("osixia/openldap:1.5.0"));
    }

    @Override
    public void start() {
        super.start();
        /* It appears slapd (the OpenLDAP process) is not fully ready when the container has started.
         * Give it some slack to let it spin up. */
        try {
            Thread.sleep(1000L);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private LdapContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);
        withClasspathResourceMapping("/ldap/openldap-bootstrap.ldif", "/container/service/slapd/assets/config/bootstrap/ldif/50-openldap-bootstrap.ldif", BindMode.READ_ONLY);
        withClasspathResourceMapping("/ldap/openldap-access.ldif", "/container/service/slapd/assets/config/bootstrap/ldif/51-openldap-access.ldif", BindMode.READ_ONLY);
        withEnv("LDAP_DOMAIN", LDAP_DOMAIN);
        withEnv("LDAP_BASE_DN", LDAP_BASE_DN);
        withEnv("LDAP_ADMIN_PASSWORD", new String(LDAP_ADMIN_PASSWORD));
        withEnv("LDAP_TLS_VERIFY_CLIENT", "never");
        withEnv("LDAP_RFC2307BIS_SCHEMA", "true");
        withExposedPorts(389);
        withCommand("--copy-service");
    }

    public String getLdapBaseDn() {
        return LDAP_BASE_DN;
    }

    public String getLdapHost() {
        return getHost();
    }

    public int getLdapPort() {
        return getMappedPort(389);
    }

    public Config getConfig() {
        final Map<String, String> props = new HashMap<>();
        props.put(Config.ENV_LDAP_HOST, getLdapHost());
        props.put(Config.ENV_LDAP_PORT, String.valueOf(getLdapPort()));
        props.put(Config.ENV_LDAP_BASE_DN, LDAP_BASE_DN);
        props.put(Config.ENV_LDAP_USER, LDAP_ADMIN_USER);
        props.put(Config.ENV_LDAP_PASSWORD, new String(LDAP_ADMIN_PASSWORD));
        return new Config(props);
    }

}
