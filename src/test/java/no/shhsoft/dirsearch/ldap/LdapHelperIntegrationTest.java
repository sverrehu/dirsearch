package no.shhsoft.dirsearch.ldap;

import no.shhsoft.dirsearch.DirSearch;
import no.shhsoft.dirsearch.LdapHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class LdapHelperIntegrationTest {

    private static LdapServer ldap;
    private static LdapHelper ldapHelper;

    @BeforeAll
    public static void beforeAll() {
        ldap = new LdapServer();
        ldap.start();
        ldapHelper = LdapHelper.forConfig(ldap.getConfig());
    }

    @AfterAll
    public static void afterAll() {
        ldap.stop();
    }

    @Test
    public void shouldFindOne() {
        final Map<String, Map<String, List<String>>> result = ldapHelper.search(LdapServer.PERSON2_CN);
        assertEquals(1, result.size());
    }

    @Test
    public void shouldBar() {
        System.out.println(DirSearch.searchResultToJsonString(ldapHelper.search(LdapServer.PERSON2_CN)));
        System.out.println(DirSearch.searchResultToJsonString(ldapHelper.search(LdapServer.PERSONS_GROUP_CN)));
        System.out.println(DirSearch.attributesToJsonString(ldapHelper.get(LdapServer.PERSONS_GROUP)));
    }

}
