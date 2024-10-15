package no.shhsoft.dirsearch.model;

import java.util.Objects;

public final class Dn {

    private final String dn;

    public Dn(final String dn) {
        this.dn = Objects.requireNonNull(dn);
    }

    public String getDn() {
        return dn;
    }

    @Override
    public String toString() {
        return dn;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Dn dn1 = (Dn) o;
        return Objects.equals(dn, dn1.dn);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dn);
    }

}
