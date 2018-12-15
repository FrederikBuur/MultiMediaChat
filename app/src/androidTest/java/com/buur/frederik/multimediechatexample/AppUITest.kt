package com.buur.frederik.multimediechatexample


import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.gifpicker.GifAdapter
import com.buur.frederik.multimediechatexample.activities.MainActivity
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class AppUITest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    @JvmField
    val grantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO)

    // only works on first launch
    @Test
    fun aLoginTest() {

        onView(withId(R.id.loginNameEditText))
                .perform(replaceText("Bob"))

        onView(withId(R.id.loginPrimaryAction))
                .perform(click())

        Thread.sleep(500)

        onView(withId(R.id.loginContainer))
                .check(ViewAssertions.doesNotExist())
    }

    // make sure server is up and running
    @Test
    fun bSendTextMessage() {

        val msg = "Test message"

        onView(withId(R.id.inputEditText))
                .perform(replaceText(msg))
        onView(withId(R.id.sendButton))
                .perform(click())
        onView(withId(R.id.chatRecyclerView))
                .check(RecyclerViewLastItemType(MMDataType.Text))
    }

    @Test
    fun cSendGif() {

        onView(withId(R.id.optionsViewGif))
                .perform(click())

        onView(withId(R.id.gifRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition<GifAdapter.GifViewHolder>(0, click()))

        Thread.sleep(500)

        onView(withId(R.id.chatRecyclerView))
                .check(RecyclerViewLastItemType(MMDataType.Gif)  )

    }

    @Test
    fun dSendImageFromCamera() {

        onView(withId(R.id.optionsViewCamera))
                .perform(click())
        onView(withId(R.id.cameraFacingDirectionButton))
                .perform(click())
        onView(withId(R.id.cameraCaptureButton))
                .perform(click())
        onView(withId(R.id.chatRecyclerView))
                .check(RecyclerViewLastItemType(MMDataType.Image))


    }

    inner class RecyclerViewLastItemType(private val mmDataType: MMDataType) : ViewAssertion {
        override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
            val rw = view as? RecyclerView
            val adapter = rw?.adapter
            val count = adapter?.itemCount

            val bool = count?.let {
                val type = adapter.getItemViewType(it)
                type == mmDataType.ordinal

            } ?: kotlin.run {
                false
            }
            assert(bool)
        }
    }

}


