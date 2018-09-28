package com.buur.frederik.multimediechat.views.inputfield.contentviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.buur.frederik.multimediechat.R


class ContentGalleryView: ContentSuperView {

    init {
        View.inflate(context, R.layout.view_content_gallery, this)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun closeAnimation() {

    }

}