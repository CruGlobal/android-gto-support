package org.ccci.gto.android.common.picasso.databinding.adapters

import androidx.databinding.BindingConversion
import com.squareup.picasso.Transformation

@BindingConversion
internal fun convertClassNameToTransformation(className: String?) =
    className?.let { Class.forName(it).newInstance() as Transformation }
