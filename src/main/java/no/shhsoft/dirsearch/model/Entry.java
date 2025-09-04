package no.shhsoft.dirsearch.model;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public final class Entry {

    private static final Comparator<String> DN_COMPARATOR = String::compareToIgnoreCase;
    private final String dn;
    private final Set<String> members = new TreeSet<>(DN_COMPARATOR);
    private final Set<String> memberOf = new TreeSet<>(DN_COMPARATOR);
    private final Set<String> transitiveMemberOf = new TreeSet<>(DN_COMPARATOR);

    Entry(final String dn) {
        this.dn = dn;
    }

    public void addMember(final Collection<String> dns) {
        members.addAll(dns);
    }

    public void addMemberOf(final Collection<String> dns) {
        memberOf.addAll(dns);
    }

    public void addTransitiveMemberOf(final Collection<String> dns) {
        transitiveMemberOf.addAll(dns);
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

    public Collection<String> getTransitiveMemberOf() {
        return transitiveMemberOf;
    }

}
