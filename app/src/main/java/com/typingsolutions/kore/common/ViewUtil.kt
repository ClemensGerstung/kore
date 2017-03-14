package com.typingsolutions.kore.common

import android.util.Log
import android.widget.TextView
import java.util.*


class ViewUtil {
    companion object {
        fun <T : TextView> clearText(v: T) {
            try {
                val text = v.text
                val field = text.javaClass.getDeclaredField("mText")
                field.isAccessible = true
                var arr = field.get(text) as CharArray
                arr = Arrays.copyOf(Constants.CHARS, arr.size)
                field.set(text, arr)
                v.text = ""
            } catch (e: IllegalAccessException) {
                Log.d(javaClass.simpleName, e.message)
            } catch (e: NoSuchFieldException) {
                Log.d(javaClass.simpleName, e.message)
            }
        }
    }
}
