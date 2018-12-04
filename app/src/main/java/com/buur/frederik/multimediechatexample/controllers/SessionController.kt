package com.buur.frederik.multimediechatexample.controllers

import com.buur.frederik.multimediechatexample.models.User
import io.realm.Realm

class SessionController {

    private var user: User? = null

    fun getUser(): User? {
        return this.user
    }

    fun setUser(user: User) {
        this.user = user
    }

    fun isUserLoggedIn(realm: Realm): Boolean {
        return user?.name?.let {
            true
        } ?: kotlin.run {
            val userRealm = realm.where(User::class.java).findFirst()
            userRealm?.let { user ->
                val userCopy = realm.copyFromRealm(user)
                setUser(userCopy)
                true
            } ?: kotlin.run {
                false
            }
        }
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