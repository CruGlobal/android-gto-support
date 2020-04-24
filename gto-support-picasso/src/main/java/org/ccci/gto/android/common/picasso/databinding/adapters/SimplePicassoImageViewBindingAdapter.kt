package org.ccci.gto.android.common.picasso.databinding.adapters

import androidx.databinding.BindingAdapter
import com.squareup.picasso.Transformation
import org.ccci.gto.android.common.picasso.view.SimplePicassoImageView

@BindingAdapter("transform")
fun SimplePicassoImageView.setTransform(transformation: Transformation?) = setTransforms(listOfNotNull(transformation))
