package org.ccci.gto.android.common.jsonapi.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public final class Includes {
    @NonNull
    private final String mBase;
    @Nullable
    private final TreeSet<String> mInclude;

    public Includes(@Nullable final String... include) {
        this(include != null ? Arrays.asList(include) : null);
    }

    public Includes(@Nullable final Collection<String> include) {
        mBase = "";
        mInclude = include != null ? new TreeSet<>(include) : null;
    }

    private Includes(@NonNull final Includes base, @NonNull final String descendant) {
        mBase = base.mBase + descendant + ".";
        mInclude = base.mInclude;
    }

    /**
     * Merge two base Includes objects together. This should only ever be called on a base includes object.
     *
     * @param includes
     * @return
     */
    public Includes merge(@Nullable final Includes includes) {
        // throw an error if this is a descendant includes object
        if (!"".equals(mBase)) {
            throw new IllegalStateException("Cannot merge includes with a descendant Includes object");
        }

        // short-circuit if we aren't actually merging an Includes object
        if (includes == null) {
            return this;
        }

        // throw an error if the includes object being merged is a descendant includes object
        if (!"".equals(includes.mBase)) {
            throw new IllegalArgumentException("Cannot merge a descendant Includes object");
        }

        // merge rules: include all overrides everything, otherwise merge the includes
        if (mInclude == null) {
            return this;
        } else if (includes.mInclude == null) {
            return includes;
        } else {
            final List<String> values = new ArrayList<>(mInclude);
            values.addAll(includes.mInclude);
            return new Includes(values);
        }
    }

    public boolean include(@NonNull final String relationship) {
        if (mInclude == null) {
            return true;
        }

        // check for a direct include
        final String key = mBase + relationship;
        if (mInclude.contains(key)) {
            return true;
        }

        // check for an implicit include
        final String entry = mInclude.ceiling(key + ".");
        return entry != null && entry.startsWith(key + ".");
    }

    @NonNull
    public Includes descendant(@NonNull final String relationship) {
        if (mInclude == null) {
            return this;
        }

        return new Includes(this, relationship);
    }
}
