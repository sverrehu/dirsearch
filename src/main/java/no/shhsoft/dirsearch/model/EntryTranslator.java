package no.shhsoft.dirsearch.model;

import java.util.List;
import java.util.Map;

public final class EntryTranslator {

    private EntryTranslator() {
    }

    public static Entry fromDnAndAttributes(final String dn, final Map<String, List<String>> attributes) {
        final Entry entry = new Entry(dn);
    }

    public static String toJson(final Entry entry) {

    }

}
