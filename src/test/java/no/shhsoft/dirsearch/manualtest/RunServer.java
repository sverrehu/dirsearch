package no.shhsoft.dirsearch.manualtest;

import no.shhsoft.dirsearch.DirSearch;
import no.shhsoft.dirsearch.ldap.LdapServer;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class RunServer {

    public static void main(final String[] args) {
        final LdapServer ldap = new LdapServer();
        ldap.start();
        new DirSearch().runServer(ldap.getConfig());
    }

}
