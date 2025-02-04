package com.example.exercise3
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE username = :username")
    suspend fun getUser(username: String): User?

    @Query("SELECT * FROM user ORDER BY uid DESC LIMIT 1") // ✅ Fetch latest user
    suspend fun getLatestUser(): User?

    @Insert
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun delete(user: User)
}