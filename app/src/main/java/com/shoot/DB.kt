package com.shoot

import android.content.Context
import androidx.room.*
import kotlin.properties.Delegates

@Entity(tableName = "user")
data class User (@PrimaryKey var id: Int, var name: String) {

}

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(vararg user: User)

    @Query("SELECT * FROM user WHERE id = 0")
    fun getUser(): User
}

@Database(entities = arrayOf(User::class), version = 1)
abstract class DB : RoomDatabase() {
    abstract fun userDao() : UserDao

    companion object {
        private var db : DB? = null
        fun newInstance(context: Context) : DB? {
            if (db == null) {
                db = Room.databaseBuilder(context, DB::class.java, "shoot-db").build()
            }
            return db
        }

        fun destroyInstance() {
            db = null
        }
    }
}
