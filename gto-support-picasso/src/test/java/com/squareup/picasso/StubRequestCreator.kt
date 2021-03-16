package com.squareup.picasso

open class StubRequestCreator(picasso: Picasso? = null) : RequestCreator() {
    init {
        picassoField?.set(this, picasso)
    }
}
