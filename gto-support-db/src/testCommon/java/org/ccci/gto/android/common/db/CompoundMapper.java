package org.ccci.gto.android.common.db;

import android.content.ContentValues;
import android.database.Cursor;
import androidx.annotation.NonNull;

import org.ccci.gto.android.common.db.model.Compound;

import static org.ccci.gto.android.common.db.Contract.CompoundTable.COLUMN_DATA1;
import static org.ccci.gto.android.common.db.Contract.CompoundTable.COLUMN_DATA2;
import static org.ccci.gto.android.common.db.Contract.CompoundTable.COLUMN_ID1;
import static org.ccci.gto.android.common.db.Contract.CompoundTable.COLUMN_ID2;

class CompoundMapper extends AbstractMapper<Compound> {
    @Override
    protected void mapField(@NonNull ContentValues values, @NonNull String field, @NonNull Compound obj) {
        switch (field) {
            case COLUMN_ID1:
                values.put(field, obj.id1);
                break;
            case COLUMN_ID2:
                values.put(field, obj.id2);
                break;
            case COLUMN_DATA1:
                values.put(field, obj.data1);
                break;
            case COLUMN_DATA2:
                values.put(field, obj.data2);
                break;
            default:
                super.mapField(values, field, obj);
        }
    }

    @NonNull
    @Override
    protected Compound newObject(@NonNull Cursor c) {
        return new Compound(getString(c, COLUMN_ID1, ""), getString(c, COLUMN_ID2, ""));
    }

    @NonNull
    @Override
    public Compound toObject(@NonNull Cursor c) {
        final Compound obj = super.toObject(c);
        obj.data1 = getString(c, COLUMN_DATA1);
        obj.data2 = getString(c, COLUMN_DATA2);
        return obj;
    }
}
