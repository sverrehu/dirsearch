package no.shhsoft.dirsearch.ldap;

import no.shhsoft.dirsearch.LdapQuerier;
import no.shhsoft.dirsearch.model.Entry;
import no.shhsoft.dirsearch.model.EntryTranslator;
import no.shhsoft.json.impl.generator.HumanReadableJsonGeneratorImpl;
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
        final Entry entry = result.values().iterator().next();
        assertEquals(2, entry.getMemberOf().size());
        assertEquals(1, entry.getTransitiveMemberOf().size());
    }

    @Test
    public void shouldFindPersonInCyclicGroups() {
        final Map<String, Entry> result = ldapQuerier.search(LdapServer.PERSON4_CN);
        assertEquals(1, result.size());
        final Entry entry = result.values().iterator().next();
        assertEquals(2, entry.getMemberOf().size());
        assertEquals(3, entry.getTransitiveMemberOf().size());
    }

    @Test
    public void shouldPrintSomeJsonStructures() {
        System.out.println(toJsonString(ldapQuerier.search(LdapServer.PERSON2_CN)));
        System.out.println(toJsonString(ldapQuerier.search(LdapServer.PERSON4_CN)));
        System.out.println(toJsonString(ldapQuerier.search(LdapServer.PERSONS_GROUP_CN)));
        System.out.println(toJsonString(ldapQuerier.get(LdapServer.PERSONS_GROUP)));
    }

    private static String toJsonString(final Entry entry) {
        return new HumanReadableJsonGeneratorImpl().generate(EntryTranslator.toJson(entry));
    }

    private static String toJsonString(final Map<String, Entry> entries) {
        return new HumanReadableJsonGeneratorImpl().generate(EntryTranslator.toJson(entries));
    }

}
