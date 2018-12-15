package com.buur.frederik.multimediechatexample

import com.buur.frederik.multimediechat.enums.MMDataType
import com.buur.frederik.multimediechat.models.MMData
import com.buur.frederik.multimediechatexample.fragments.chatfragment.ChatController
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

class AppTest {

    @Test
    fun sendTextMessage() {

        val con = Mockito.mock(ChatController::class.java)
        val mmd = MMData(11, "testmsg", MMDataType.Text.ordinal, null, null, null, null)
        con.sendMessageToServer(mmd)
                .subscribeOn(Schedulers.trampoline())
                .observeOn(Schedulers.trampoline())
                .subscribe({
                    Assert.assertEquals(true, it is Int)
                }, {
                    it
                })

    }

}