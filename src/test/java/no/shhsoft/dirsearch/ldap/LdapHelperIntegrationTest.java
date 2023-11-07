package no.shhsoft.dirsearch.ldap;

import no.shhsoft.dirsearch.DirSearch;
import no.shhsoft.dirsearch.LdapHelper;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class LdapHelperIntegrationTest {

    private static LdapServer ldap;
    private static LdapHelper ldapHelper;

    @BeforeClass
    public static void beforeClass() {
        ldap = new LdapServer();
        ldap.start();
        ldapHelper = LdapHelper.forConfig(ldap.getConfig());
    }

    @AfterClass
    public static void afterClass() {
        ldap.stop();
    }

    @Test
    public void shouldFindOne() {
        final Map<String, Map<String, List<String>>> result = ldapHelper.search(LdapServer.PERSON2_CN);
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void shouldBar() {
        System.out.println(DirSearch.searchResultToJsonString(ldapHelper.search(LdapServer.PERSON2_CN)));
        System.out.println(DirSearch.searchResultToJsonString(ldapHelper.search(LdapServer.PERSONS_GROUP_CN)));
        System.out.println(DirSearch.attributesToJsonString(ldapHelper.get(LdapServer.PERSONS_GROUP)));
    }

}
