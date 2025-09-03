package no.shhsoft.dirsearch.model;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public final class Entry {

    private static final Comparator<Entry> ENTRY_COMPARATOR = Comparator.comparing(Entry::getDn);
    private final String dn;
    private final SortedSet<Entry> members = new TreeSet<>(ENTRY_COMPARATOR);
    private final SortedSet<Entry> memberOf = new TreeSet<>(ENTRY_COMPARATOR);

    public Entry(final String dn) {
        this.dn = dn;
    }

    public String getDn() {
        return dn;
    }

}
