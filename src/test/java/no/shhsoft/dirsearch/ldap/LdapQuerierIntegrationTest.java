package no.shhsoft.dirsearch.ldap;

import no.shhsoft.dirsearch.DirSearch;
import no.shhsoft.dirsearch.LdapQuerier;
import no.shhsoft.dirsearch.model.Entry;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class LdapQuerierIntegrationTest {

    private static LdapServer ldap;
    private static LdapQuerier ldapQuerier;

    @BeforeAll
    public static void beforeAll() {
        ldap = new LdapServer();
        ldap.start();
        ldapQuerier = new LdapQuerier(ldap.getConfig());
    }

    @AfterAll
    public static void afterAll() {
        ldap.stop();
    }

    @Test
    public void shouldFindOne() {
        final Map<String, Entry> result = ldapQuerier.search(LdapServer.PERSON2_CN);
        assertEquals(1, result.size());
    }

    @Test
    public void shouldPrintSomeJsonStructures() {
        System.out.println(DirSearch.searchResultToJsonString(ldapQuerier.search(LdapServer.PERSON2_CN)));
        System.out.println(DirSearch.searchResultToJsonString(ldapQuerier.search(LdapServer.PERSONS_GROUP_CN)));
        System.out.println(DirSearch.entryToJsonString(ldapQuerier.get(LdapServer.PERSONS_GROUP)));
    }

}
