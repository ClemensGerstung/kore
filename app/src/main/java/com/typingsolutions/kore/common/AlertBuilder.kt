package com.typingsolutions.kore.common

import android.content.Context
import android.content.DialogInterface
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.annotation.StyleRes
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import com.typingsolutions.kore.R

class AlertBuilder {

    var dialog: AlertDialog? = null
        private set

    fun setPositiveButton(text: CharSequence, listener: (dialog: DialogInterface, which: Int) -> Unit = { _, _ -> }): AlertBuilder {
        dialog?.setButton(AlertDialog.BUTTON_POSITIVE, text, listener)
        return this
    }

    fun setNegativeButton(text: CharSequence, listener: (dialog: DialogInterface, which: Int) -> Unit = { _, _ -> }): AlertBuilder {
        dialog?.setButton(AlertDialog.BUTTON_NEGATIVE, text, listener)
        return this
    }

    fun setNeutralButton(text: CharSequence, listener: (dialog: DialogInterface, which: Int) -> Unit = { _, _ -> }): AlertBuilder {
        dialog?.setButton(AlertDialog.BUTTON_NEUTRAL, text, listener)
        return this
    }


    fun setView(@LayoutRes layout: Int): AlertBuilder {
        return setView(View.inflate(dialog?.context, layout, null))
    }

    fun setView(view: View): AlertBuilder {
        dialog?.setView(view)
        return this
    }

    fun setSecurityFlags(): AlertBuilder {
        dialog?.window?.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        return this
    }

    fun setTitle(title: CharSequence): AlertBuilder {
        dialog?.setTitle(title)
        return this
    }

    fun setMessage(message: CharSequence): AlertBuilder {
        dialog?.setMessage(message)
        return this
    }

    fun setMessage(@StringRes message: Int): AlertBuilder {
        dialog?.setMessage(dialog?.context?.resources?.getString(message))

        return this
    }

    fun setItems(itemClickListener: AlertBuilder.OnItemClickListener, vararg items: String): AlertBuilder {
        val context = dialog?.context
        val lv = ListView(context)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val dimension = context?.resources?.getDimension(R.dimen.md)?.toInt()!!
        lv.layoutParams = params
        lv.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, items)
        lv.setOnItemClickListener { adapterView, view, i, l -> itemClickListener.onItemClick(dialog!!, adapterView, view, i, l) }

        val layout = LinearLayout(context)
        layout.addView(lv)
        layout.setPadding(dimension, dimension, dimension, dimension)

        return setView(layout)
    }

    fun show() {
        dialog?.show()
    }

    interface OnItemClickListener {
        fun onItemClick(dialog: DialogInterface, parent: AdapterView<*>, view: View, position: Int, id: Long)
    }

    companion object {
        private val builder = AlertBuilder()

        @JvmOverloads fun create(context: Context, @StyleRes theme: Int = R.style.ColoredAlertDialog): AlertBuilder {
            builder.dialog = AlertDialog.Builder(context, theme).create()
            return builder
        }

        val lastCreated: AlertDialog
            get() = builder.dialog!!
    }
}
