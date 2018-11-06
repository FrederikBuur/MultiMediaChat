package com.buur.frederik.multimediechat.messageviews

import android.content.ActivityNotFoundException
import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.buur.frederik.multimediechat.R
import com.buur.frederik.multimediechat.models.MMData
import kotlinx.android.synthetic.main.view_document.view.*
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.widget.Toast
import java.io.File
import java.text.DecimalFormat


class DocumentView : SuperView, View.OnClickListener {

    private val kb = 1024
    private val mb = kb * kb
    private val format = DecimalFormat("#.##")

    var file: File? = null

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.view_document, this)
    }

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun setup(isSender: Boolean, mmData: MMData?, time: Int?) {
        this.isSender = isSender
        this.mmData = mmData

        val path = this.mmData?.source
        file = File(path)

        setupFileDetails()

        documentImageView.setColorFilter(
                if (isSender) {
                    ContextCompat.getColor(context, R.color.textBright)
                } else {
                    ContextCompat.getColor(context, R.color.colorPrimary)
                })

        this.setParams(documentContainer, documentContentContainer)
        this.setupDateAndSender(docMsgTime, docMsgSender)
        this.setTextColor(documentTitle)

        documentContentContainer.setOnClickListener(this)

    }

    private fun setupFileDetails() {
        documentTitle.text = getFileTitle()
        documentType.text = getFileType()
        documentSize.text = getFileSize()
    }

    override fun onClick(v: View?) {
        if (v == documentContentContainer) {
            openDocument()
        }
    }

    private fun getFileTitle(): String {
        return file?.path?.let { path ->
            path.substring(path.lastIndexOf("/") + 1)
        } ?: kotlin.run {
            "Unknown"
        }
    }

    private fun getFileSize(): String {
        return file?.let {
            val size = it.length()
            documentSize.visibility = if (size < 1) {
                View.GONE
            } else {
                View.VISIBLE
            }
            when {
                size > mb -> format.format(size / mb) + " MB"
                size > kb -> format.format(size / kb) + " KB"
                else -> format.format(size) + " Byte"
            }
        } ?: kotlin.run {
            "Unknown"
        }
    }

    private fun getFileType(): String {
        return file?.path?.let {
            when {
                it.endsWith(".pdf") -> {
                    "PDF"
                }
                it.endsWith(".txt") -> {
                    "TXT"
                }
                it.endsWith(".doc") -> {
                    "DOC"
                }
                else -> {
                    "Unknown"
                }
            }
        } ?: kotlin.run {
            "Unknown"
        }
    }

    private fun openDocument() {
        val uri = if (file?.exists() == true) {
            file?.let {
                FileProvider.getUriForFile(context, context.applicationContext.packageName + ".fileprovider", it)
            }
        } else {
            Uri.parse(this.mmData?.source)
        }

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Not able to open document", Toast.LENGTH_SHORT).show()
        }
    }

}