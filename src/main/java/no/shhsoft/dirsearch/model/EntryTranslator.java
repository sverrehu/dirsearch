package no.shhsoft.dirsearch.model;

import no.shhsoft.json.model.JsonArray;
import no.shhsoft.json.model.JsonObject;
import no.shhsoft.json.model.JsonString;

import java.util.Collection;
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

    public static JsonObject toJson(final Map<String, Entry> entriesByDn) {
        final JsonObject object = new JsonObject();
        for (final Map.Entry<String, Entry> entry : entriesByDn.entrySet()) {
            object.put(entry.getKey(), toJson(entry.getValue()));
        }
        return object;
    }

    public static JsonObject toJson(final Entry entry) {
        final JsonObject object = new JsonObject();
        object.put("dn", JsonString.get(entry.getDn()));
        if (!entry.getMemberOf().isEmpty()) {
            object.put("memberOf", toJsonArray(entry.getMemberOf()));
        }
        if (!entry.getMembers().isEmpty()) {
            object.put("members", toJsonArray(entry.getMembers()));
        }
        if (!entry.getIndirectMembers().isEmpty()) {
            object.put("indirectMembers", toJsonArray(entry.getIndirectMembers()));
        }
        if (!entry.getIndirectMemberOf().isEmpty()) {
            object.put("indirectMemberOf", toJsonArray(entry.getIndirectMemberOf()));
        }
        return object;
    }

    private static JsonArray toJsonArray(final Collection<String> collection) {
        final JsonArray array = new JsonArray();
        for (final String string : collection) {
            array.add(JsonString.get(string));
        }
        return array;
    }
}
