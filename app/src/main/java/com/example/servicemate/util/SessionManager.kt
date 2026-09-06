package com.example.servicemate.util

import android.content.Context

object SessionManager {
    private const val PREFS_NAME = "servicemate_session"
    private const val KEY_USER_ID = "logged_in_user_id"
    private const val NO_USER = -1L

    fun saveSession(context: Context, userId: Long) {
        prefs(context).edit().putLong(KEY_USER_ID, userId).apply()
    }

    fun getLoggedInUserId(context: Context): Long {
        return prefs(context).getLong(KEY_USER_ID, NO_USER)
    }

    fun isLoggedIn(context: Context): Boolean = getLoggedInUserId(context) != NO_USER

    fun clearSession(context: Context) {
        prefs(context).edit().clear().apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}