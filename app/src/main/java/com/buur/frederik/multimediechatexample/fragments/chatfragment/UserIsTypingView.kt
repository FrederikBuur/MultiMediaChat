package com.buur.frederik.multimediechatexample.fragments.chatfragment

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.buur.frederik.multimediechatexample.R
import com.buur.frederik.multimediechatexample.models.User
import kotlinx.android.synthetic.main.view_user_is_typing.view.*

class UserIsTypingView : FrameLayout {

    private var users = ArrayList<User>()

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.view_user_is_typing, this)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setup(users: ArrayList<User>) {
        this.users = users

        var text = String()
        val size = this.users.size
        if (size < 1) {
            userIsTypingContainer.visibility = View.GONE
            text = ""
        } else {
            userIsTypingContainer.visibility = View.VISIBLE
            users.forEach { user ->
                text += "${user.name}, "
            }
            text += "is typing..."
        }
        userIsTypingTextView.text = text
    }

}