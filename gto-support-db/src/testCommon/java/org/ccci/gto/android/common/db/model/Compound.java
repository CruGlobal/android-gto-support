package org.ccci.gto.android.common.db.model;

import android.support.annotation.NonNull;

public class Compound {
    @NonNull
    public final String id1;
    @NonNull
    public final String id2;
    public String data1;
    public String data2;

    public Compound(@NonNull String id1, @NonNull String id2) {
        this.id1 = id1;
        this.id2 = id2;
    }

    public Compound(@NonNull String id1, @NonNull String id2, String data1, String data2) {
        this(id1, id2);
        this.data1 = data1;
        this.data2 = data2;
    }
}
