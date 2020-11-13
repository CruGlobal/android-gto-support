package com.squareup.picasso

internal var picassoSingleton: Picasso?
    get() = Picasso.singleton
    set(value) {
        Picasso.singleton = value
    }
