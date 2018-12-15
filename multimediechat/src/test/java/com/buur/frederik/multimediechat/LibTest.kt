package com.buur.frederik.multimediechat

import android.view.MotionEvent
import android.widget.Button
import com.buur.frederik.multimediechat.inputfield.MMInputController
import com.buur.frederik.multimediechat.messageviews.DocumentView
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class LibTest {

    @Test
    fun getFileTypeTest() {

        val dv = Mockito.mock(DocumentView::class.java)
        val path = "somefolder/anotherfolder/file.pdf"

        Assert.assertEquals("PDF", dv.getFileType(path))
        Assert.assertEquals(false, dv.getFileType(path) == "DOC")
    }

    @Test
    fun getFileSizeTest() {

        val v = Mockito.mock(DocumentView::class.java)
        val bytes1 = 50000L
        val bytes2 = 1258291L

        val size1 = v.getFileSize(bytes1)
        Assert.assertEquals("48,83 KB", size1)

        val size2 = v.getFileSize(bytes2)
        Assert.assertEquals("1,2 MB", size2)

    }

}