package com.typingsolutions.kore.overview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import com.typingsolutions.kore.R
import com.typingsolutions.kore.common.DatabaseConnection
import com.typingsolutions.kore.common.KoreApplication
import com.typingsolutions.kore.common.data.Password

class OverviewActivity : AppCompatActivity() {

    private lateinit var mDatabaseConnector: DatabaseConnection
    private lateinit var mAdapter: OverviewAdapter
    private lateinit var mListRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.overview_layout)

        mDatabaseConnector = (this.applicationContext as KoreApplication).databaseConnection!!

        mListRecyclerView = findViewById(R.id.overviewlayout_recyclerview_list) as RecyclerView

        val loader = LoadPasswordAsyncTask()
        loader.execute(mDatabaseConnector)
        mAdapter = OverviewAdapter(loader.get(), applicationContext)
        mListRecyclerView.adapter = mAdapter
    }
}