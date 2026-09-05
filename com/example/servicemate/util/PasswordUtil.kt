package com.example.servicemate.util

import java.security.MessageDigest

object PasswordUtil {
    // Simple SHA-256 hash. Good enough for a coursework demo;
    // production apps would use bcrypt/Argon2 with a per-user salt.
    fun hash(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun matches(password: String, hash: String): Boolean {
        return hash(password) == hash
    }
}
