package no.shhsoft.dirsearch.model;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public final class Entry {

    private static final Comparator<String> DN_COMPARATOR = String::compareToIgnoreCase;
    private final String dn;
    private final Set<String> members = new TreeSet<>(DN_COMPARATOR);
    private final Set<String> memberOf = new TreeSet<>(DN_COMPARATOR);

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

    public Collection<String> getMembers() {
        return members;
    }

    public Collection<String> getMemberOf() {
        return memberOf;
    }

}
