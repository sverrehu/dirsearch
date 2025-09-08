package no.shhsoft.dirsearch.model;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public final class Entry {

    private static final Comparator<String> DN_COMPARATOR = String::compareToIgnoreCase;
    private final String dn;
    private final Set<String> members = new TreeSet<>(DN_COMPARATOR);
    private final Set<String> memberOf = new TreeSet<>(DN_COMPARATOR);
    private final Set<String> indirectMembers = new TreeSet<>(DN_COMPARATOR);
    private final Set<String> indirectMemberOf = new TreeSet<>(DN_COMPARATOR);
    private boolean indirectMembersFound = false;
    private boolean indirectMembersOfFound = false;

    Entry(final String dn) {
        this.dn = dn;
    }

    public void addMember(final Collection<String> dns) {
        members.addAll(dns);
    }

    public void addMemberOf(final Collection<String> dns) {
        memberOf.addAll(dns);
    }

    public void addIndirectMemberOf(final Collection<String> dns) {
        indirectMemberOf.addAll(dns);
    }

    public void addIndirectMembers(final Collection<String> dns) {
        indirectMembers.addAll(dns);
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

    public Collection<String> getIndirectMemberOf() {
        return indirectMemberOf;
    }

    public Collection<String> getIndirectMembers() {
        return indirectMembers;
    }

    public boolean isIndirectMembersFound() {
        return indirectMembersFound;
    }
    public boolean isIndirectMembersOfFound() {
        return indirectMembersOfFound;
    }

    public void setIndirectMembersFound(final boolean indirectMembersFound) {
        this.indirectMembersFound = indirectMembersFound;
    }

    public void setIndirectMembersOfFound(final boolean indirectMembersOfFound) {
        this.indirectMembersOfFound = indirectMembersOfFound;
    }

}
