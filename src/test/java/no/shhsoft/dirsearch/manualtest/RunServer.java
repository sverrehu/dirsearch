package no.shhsoft.dirsearch.manualtest;

import no.shhsoft.dirsearch.DirSearch;
import no.shhsoft.dirsearch.ldap.LdapContainer;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class RunServer {

    public static void main(final String[] args) {
        final LdapContainer ldapContainer = new LdapContainer();
        ldapContainer.start();
        new DirSearch().runServer(ldapContainer.getConfig());
    }

}
