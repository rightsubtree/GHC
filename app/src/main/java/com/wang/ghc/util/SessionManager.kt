package com.wang.ghc.util

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.wang.ghc.model.User
import javax.inject.Inject


class SessionManager @Inject constructor(context: Context) {
    private val prefs = context.getSharedPreferences("ghc_prefs", Context.MODE_PRIVATE)
    val currentUser = MutableLiveData<User?>()
    val accessToken = MutableLiveData<String?>()

    init {
        currentUser.value = getSavedUser()
        accessToken.value = getAccessToken()
    }

    fun saveUser(user: User) {
        prefs.edit {
            putString("user", Gson().toJson(user))
        }
        currentUser.postValue(user)
    }

    fun logoff() {
        prefs.edit {
            remove("user")
            remove("access_token")
        }
        currentUser.postValue(null)
        accessToken.postValue(null)
    }

    private fun getSavedUser(): User? {
        return prefs.getString("user", null)?.let {
            Gson().fromJson(it, User::class.java)
        }
    }

    fun saveAccessToken(token: String) {
        Log.i("SessionManager", "saveAccessToken: $token")
        prefs.edit {
            putString("access_token", token)
        }
        accessToken.postValue(token)
    }

    fun getAccessToken(): String? {
        return prefs.getString("access_token", null)
    }
}