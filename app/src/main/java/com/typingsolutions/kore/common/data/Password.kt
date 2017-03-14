package com.typingsolutions.kore.common.data

import net.sqlcipher.database.SQLiteDatabase

data class Password(val id: Int, val username: String, val program: String, val position: Int, val passwords: List<History>) {
    companion object {
        val CreateString: String = "CREATE TABLE IF NOT EXISTS passwords (id INTEGER PRIMARY KEY, username TEXT, program TEXT, position INT)"

        fun loadPasswords(database: SQLiteDatabase): Array<Password> {
            val result = ArrayList<Password>()
            val query = "SELECT p.id, " +
                               "p.username, " +
                               "p.program, " +
                               "p.position, " +
                               "h.id, " +
                               "h.password, " +
                               "h.created " +
                        "FROM passwords p " +
                        "JOIN history h ON p.id = h.passwordId " +
                        "ORDER BY p.position, p.id, h.created"
            val cursor = database.rawQuery(query, arrayOf())
            // TODO:

            return result.toTypedArray()
        }
    }
}