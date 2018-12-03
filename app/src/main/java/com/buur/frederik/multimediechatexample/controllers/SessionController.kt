package com.buur.frederik.multimediechatexample.controllers

import com.buur.frederik.multimediechatexample.models.User

class SessionController {

    private var user: User? = null

    fun getUser(): User? {
        return this.user
    }

    fun setUser(user: User) {
        this.user = user
    }

    companion object {
        private var instanceCompanion: SessionController? = null

        fun getInstance(): SessionController {
            if (instanceCompanion == null) {
                instanceCompanion = SessionController()
            }
            return instanceCompanion as SessionController
        }
    }
}