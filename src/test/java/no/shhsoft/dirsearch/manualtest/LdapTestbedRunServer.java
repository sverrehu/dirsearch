package no.shhsoft.dirsearch.manualtest;

import no.shhsoft.dirsearch.DirSearch;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class LdapTestbedRunServer {

    public static void main(final String[] args) {
        new DirSearch().runServer(LdapTestbed.getConfig());
    }

}
