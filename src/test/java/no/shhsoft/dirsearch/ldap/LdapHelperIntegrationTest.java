package no.shhsoft.dirsearch.ldap;

import no.shhsoft.dirsearch.DirSearch;
import no.shhsoft.dirsearch.LdapHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class LdapHelperIntegrationTest {

    private static LdapContainer ldapContainer;
    private static LdapHelper ldapHelper;

    @BeforeClass
    public static void beforeClass() {
        ldapContainer = new LdapContainer();
        ldapContainer.start();
        ldapHelper = LdapHelper.forConfig(ldapContainer.getConfig());
    }

    @AfterClass
    public static void afterClass() {
        ldapContainer.stop();
    }

    @Test
    public void shouldFoo() {
        final Map<String, Map<String, List<String>>> result = ldapHelper.search(LdapContainer.PERSON2_CN);
        System.out.println(result.size());
    }

    @Test
    public void shouldBar() {
        System.out.println(DirSearch.searchResultToJsonString(ldapHelper.search(LdapContainer.PERSON2_CN)));
        System.out.println(DirSearch.searchResultToJsonString(ldapHelper.search(LdapContainer.PERSONS_GROUP_CN)));
        System.out.println(DirSearch.attributesToJsonString(ldapHelper.get(LdapContainer.PERSONS_GROUP)));
    }

}
