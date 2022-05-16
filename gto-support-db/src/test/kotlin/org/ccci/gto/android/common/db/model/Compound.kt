package org.ccci.gto.android.common.db.model

class Compound(val id1: String, val id2: String) {
    var data1: String? = null
    var data2: String? = null

    constructor(id1: String, id2: String, data1: String?, data2: String?) : this(id1, id2) {
        this.data1 = data1
        this.data2 = data2
    }
}
