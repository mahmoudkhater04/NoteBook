package com.example.ver2

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NoteDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        val category = intent.getStringExtra("category")

        findViewById<TextView>(R.id.detailTitle).text = title
        findViewById<TextView>(R.id.detailContent).text = content
        findViewById<TextView>(R.id.detailCategory).text = category
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
