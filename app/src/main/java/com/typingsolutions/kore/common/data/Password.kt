package com.typingsolutions.kore.common.data

import net.sqlcipher.database.SQLiteDatabase
import java.text.SimpleDateFormat
import java.util.*

data class Password(val id: Int, val username: String, val program: String, val position: Int, val passwords: ArrayList<History>) {
    override fun equals(other: Any?): Boolean {
        if(other == null) return false
        if(other.javaClass != javaClass) return false

        return (other as Password).id == id
    }

    override fun hashCode(): Int = id

    companion object {
        val CreateString: String = "CREATE TABLE IF NOT EXISTS passwords (id INTEGER PRIMARY KEY, mUsernameTextView TEXT, mProgramTextView TEXT, position INT)"

        fun loadPasswords(database: SQLiteDatabase): Array<Password> {
            val result = ArrayList<Password>()
            val query = "SELECT p.id, " +
                               "p.mUsernameTextView, " +
                               "p.mProgramTextView, " +
                               "p.position, " +
                               "h.id, " +
                               "h.mPasswordTextView, " +
                               "h.created " +
                        "FROM passwords p " +
                        "JOIN history h ON p.id = h.passwordId " +
                        "ORDER BY p.position, p.id, h.created"
            val cursor = database.rawQuery(query, arrayOf())

            var last: Password? = null

            while(cursor.moveToNext())
            {
                val id = cursor.getInt(0)
                val username = cursor.getString(1)
                val program = cursor.getString(2)
                val position = cursor.getInt(3)
                val historyId = cursor.getInt(4)
                val password = cursor.getString(5)
                val createString = cursor.getString(6)

                val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                val item = History(historyId, password, df.parse(createString))
                val list = ArrayList<History>()
                list.add(item)
                val current = Password(id, username, program, position, list)

                if (current == last) {
                    last.passwords.add(item)
                } else {
                    if(last != null) {
                        result.add(last)
                    }
                    last = current
                }

            }

            cursor.close()

            return result.toTypedArray()
        }
    }
}