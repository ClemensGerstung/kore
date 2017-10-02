package com.typingsolutions.kore.common.data

import java.util.*

data class History(val id: Int, val password: String, val date: Date) {
    companion object {
        val CreateString: String = "CREATE TABLE IF NOT EXISTS history (id INTEGER PRIMARY KEY, mPasswordTextView TEXT, created DATE, passwordId INTEGER, FOREIGN KEY(passwordId) REFERENCES passwords(id))"
    }
}