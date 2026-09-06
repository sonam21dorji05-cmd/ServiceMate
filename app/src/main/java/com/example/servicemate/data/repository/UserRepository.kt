package com.example.servicemate.data.repository

import com.example.servicemate.data.dao.UserDao
import com.example.servicemate.data.model.User
import com.example.servicemate.util.PasswordUtil

class UserRepository(private val userDao: UserDao) {

    suspend fun register(fullName: String, email: String, password: String): Result<User> {
        val existing = userDao.getByEmail(email)
        if (existing != null) {
            return Result.failure(Exception("An account with this email already exists"))
        }
        val user = User(
            fullName = fullName,
            email = email,
            passwordHash = PasswordUtil.hash(password)
        )
        val id = userDao.insert(user)
        return Result.success(user.copy(id = id))
    }

    suspend fun login(email: String, password: String): Result<User> {
        val user = userDao.getByEmail(email)
            ?: return Result.failure(Exception("No account found with this email"))

        return if (PasswordUtil.matches(password, user.passwordHash)) {
            Result.success(user)
        } else {
            Result.failure(Exception("Incorrect password"))
        }
    }
}