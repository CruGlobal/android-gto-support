package org.ccci.gto.android.sync.event;

public class SyncFinishedEvent {
    public final int syncId;

    public SyncFinishedEvent(final int id) {
        this.syncId = id;
    }
}
