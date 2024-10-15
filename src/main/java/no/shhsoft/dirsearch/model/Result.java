package no.shhsoft.dirsearch.model;

import java.util.Set;

public final class Result {

    private final Dn dn;
    private final Set<Dn> members;
    private final Set<Dn> groups;

    public Result(final Dn dn, final Set<Dn> members, final Set<Dn> groups) {
        this.dn = dn;
        this.members = members;
        this.groups = groups;
    }

    public Dn getDn() {
        return dn;
    }

    public Set<Dn> getMembers() {
        return members;
    }

    public Set<Dn> getGroups() {
        return groups;
    }

}
