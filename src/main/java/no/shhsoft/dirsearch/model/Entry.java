package no.shhsoft.dirsearch.model;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public final class Entry {

    private static final Comparator<String> DN_COMPARATOR = String::compareToIgnoreCase;
    private final String dn;
    private final SortedSet<String> members = new TreeSet<>(DN_COMPARATOR);
    private final SortedSet<String> memberOf = new TreeSet<>(DN_COMPARATOR);

    Entry(final String dn) {
        this.dn = dn;
    }

    void addMember(final List<String> dns) {
        members.addAll(dns);
    }

    void addMemberOf(final List<String> dns) {
        memberOf.addAll(dns);
    }

    public String getDn() {
        return dn;
    }

}
