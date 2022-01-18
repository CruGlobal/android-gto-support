package org.ccci.gto.android.common.androidx.viewpager2.widget

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import org.ccci.gto.android.common.androidx.viewpager2.R

// This works around https://issuetracker.google.com/issues/153805142 by requesting a transform after the next layout
// when the adapter data changes.
private class PageTransformerFix(private val pager: ViewPager2) :
    RecyclerView.AdapterDataObserver(), View.OnLayoutChangeListener {
    var dataAdapterChanged = false

    init {
        pager.addOnLayoutChangeListener(this)
    }

    override fun onChanged() {
        dataAdapterChanged = true
    }

    override fun onLayoutChange(v: View?, l: Int, t: Int, r: Int, b: Int, oL: Int, oT: Int, oR: Int, oB: Int) {
        if (!dataAdapterChanged) return
        pager.requestTransform()
        dataAdapterChanged = false
    }
}

private val ViewPager2.pageTransformerFix
    get() = getTag(R.id.viewpager2_pagetransformer_fix) as? PageTransformerFix
        ?: PageTransformerFix(this).also { setTag(R.id.viewpager2_pagetransformer_fix, this) }

fun ViewPager2.registerPageTransformerFix(adapter: RecyclerView.Adapter<*>) {
    adapter.registerAdapterDataObserver(pageTransformerFix)
}

fun ViewPager2.unregisterPageTransformerFix(adapter: RecyclerView.Adapter<*>) {
    adapter.unregisterAdapterDataObserver(pageTransformerFix)
}
