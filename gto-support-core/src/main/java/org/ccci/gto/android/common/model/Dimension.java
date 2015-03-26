package org.ccci.gto.android.common.model;

public class Dimension {
    public final int width;
    public final int height;

    public Dimension(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Dimension) {
            final Dimension that = (Dimension) o;
            return this.width == that.width && this.height == that.height;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + this.width;
        result = 31 * result + this.height;
        return result;
    }
}
