package com.buur.frederik.multimediechatexample.models

import com.buur.frederik.multimediechatexample.controllers.SessionController
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class User : RealmObject() {

    @PrimaryKey
    var id: Long = 0

    var name: String? = null

    companion object {

        fun isLoggedIn(realm: Realm): Boolean {
            val user = SessionController.getInstance().getUser()
            return user?.name?.let {
                true
            } ?: kotlin.run {
                val userRealm = realm.where(User::class.java).findFirst()
                userRealm?.let { user ->
                    val userCopy = realm.copyFromRealm(user)
                    SessionController.getInstance().setUser(userCopy)
                    true
                } ?: kotlin.run {
                    false
                }
            }
        }

        fun createUser(realm: Realm, userName: String) {
            realm.executeTransaction { innerRealm ->
                val user = User()
                user.id = System.currentTimeMillis()
                user.name = userName
                innerRealm.copyToRealmOrUpdate(user)
                SessionController.getInstance().setUser(user)
            }
        }

    }

}