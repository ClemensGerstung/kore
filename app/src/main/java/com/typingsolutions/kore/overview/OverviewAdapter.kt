package com.typingsolutions.kore.overview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.typingsolutions.kore.R
import com.typingsolutions.kore.common.data.Password


class OverviewAdapter(private val passwords: Array<Password>, val context: Context) : RecyclerView.Adapter<OverviewAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val password = passwords[position]

        val viewHolder: ViewHolder = holder!!
        viewHolder.mTeaserTextView.text = password.program.substring(0, 1)
        viewHolder.mProgramTextView.text = password.program
        viewHolder.mUsernameTextView.text = password.username
        viewHolder.mPasswordTextView.text = password.passwords[passwords.size - 1].password
    }

    override fun getItemCount(): Int = passwords.size

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)

        return ViewHolder(inflater.inflate(R.layout.overview_item_layout, parent, false))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mTeaserTextView: TextView = itemView.findViewById(R.id.overviewitemlayout_textview_teaser) as TextView
        var mProgramTextView: TextView = itemView.findViewById(R.id.overviewitemlayout_textview_program) as TextView
        var mUsernameTextView: TextView = itemView.findViewById(R.id.overviewitemlayout_textview_username) as TextView
        var mPasswordTextView: TextView = itemView.findViewById(R.id.overviewitemlayout_textview_password) as TextView
    }
}