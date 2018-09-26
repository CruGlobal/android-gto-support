package org.ccci.gto.android.common.db;

import android.content.ContentValues;
import android.database.Cursor;
import androidx.annotation.NonNull;

import org.ccci.gto.android.common.db.model.Root;
import org.ccci.gto.android.common.db.util.CursorUtils;

import static org.ccci.gto.android.common.db.Contract.RootTable.COLUMN_ID;
import static org.ccci.gto.android.common.db.Contract.RootTable.COLUMN_TEST;

class RootMapper extends AbstractMapper<Root> {
    @Override
    protected void mapField(@NonNull ContentValues values, @NonNull String field, @NonNull Root obj) {
        switch (field) {
            case COLUMN_ID:
                values.put(field, obj.id);
                break;
            case COLUMN_TEST:
                values.put(field, obj.test);
                break;
            default:
                super.mapField(values, field, obj);
                break;
        }
    }

    @NonNull
    @Override
    protected Root newObject(@NonNull Cursor c) {
        return new Root();
    }

    @NonNull
    @Override
    public Root toObject(@NonNull Cursor c) {
        Root root = super.toObject(c);

        root.id = CursorUtils.getLong(c, COLUMN_ID);
        root.test = CursorUtils.getString(c, COLUMN_TEST);

        return root;
    }
}
