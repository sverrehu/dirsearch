package no.shhsoft.dirsearch.model;

import no.shhsoft.json.impl.generator.HumanReadableJsonGeneratorImpl;
import no.shhsoft.json.model.JsonObject;

import java.util.List;
import java.util.Map;

public final class EntryTranslator {

    private EntryTranslator() {
    }

    public static Entry fromDnAndAttributes(final String dn, final Map<String, List<String>> attributes) {
        final Entry entry = new Entry(dn);
        for (final Map.Entry<String, List<String>> attribute : attributes.entrySet()) {
            final String attributeName = attribute.getKey();
            if ("member".equals(attributeName) || "uniqueMember".equals(attributeName)) {
                entry.addMember(attribute.getValue());
            } else if ("memberOf".equals(attributeName)) {
                entry.addMemberOf(attribute.getValue());
            }
        }
        return entry;
    }

    public static String toJson(final Entry entry) {
        final JsonObject object = new JsonObject();
        return new HumanReadableJsonGeneratorImpl().generate(object);
    }

}
