package com.example.ver2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ver2.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.NotesList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = AppDatabase.getDatabase(this)
        loadCategories()
        loadAllNotes()
        val SaveButton = findViewById<Button>(R.id.SaveNoteButton)

        val showAllBtn = findViewById<Button>(R.id.ShowAllButton)
        val filterBtn = findViewById<Button>(R.id.FilterButton)
        val spinner = findViewById<Spinner>(R.id.spinner)

        // Show all listener
        showAllBtn.setOnClickListener {
            loadAllNotes()
        }

        // Filter listener
        filterBtn.setOnClickListener {
            val selectedCategory = spinner.selectedItem?.toString()

            if (selectedCategory.isNullOrEmpty()) {
                Toast.makeText(this, "No category selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val filteredNotes = db.noteDao().getNotesByCategory(selectedCategory)

                runOnUiThread {
                    recyclerView.adapter = NotesAdapter(filteredNotes) { selectedNote ->
                        val intent = Intent(this@MainActivity, NoteDetailActivity::class.java)
                        intent.putExtra("title", selectedNote.title)
                        intent.putExtra("content", selectedNote.content)
                        intent.putExtra("category", selectedNote.category)
                        startActivity(intent)
                    }
                }
            }
        }


        SaveButton.setOnClickListener {
            val title = findViewById<EditText>(R.id.NoteTitle)
            val cont = findViewById<EditText>(R.id.NoteContent)
            val cat = findViewById<EditText>(R.id.Category)

            if (title.text.isEmpty() || cont.text.isEmpty() || cat.text.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
            else{
                lifecycleScope.launch(Dispatchers.IO){

                    db.noteDao().insertNote(Note(
                    title=title.text.toString(),
                    content = cont.text.toString(),
                    category = cat.text.toString()))

                    loadCategories()
                    loadAllNotes()
                    runOnUiThread {
                        title.text.clear()
                        cont.text.clear()
                        cat.text.clear()

                        Toast.makeText(this@MainActivity, "Note saved!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }

    private fun loadCategories() {
        lifecycleScope.launch(Dispatchers.IO) {
            val categories = db.noteDao().getAllCategories()

            runOnUiThread {
                val adapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_item,
                    categories
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                findViewById<Spinner>(R.id.spinner).adapter = adapter
            }
        }
    }
    private fun loadAllNotes(){
        val recyclerView = findViewById<RecyclerView>(R.id.NotesList)
        lifecycleScope.launch(Dispatchers.IO) {
            val notes = db.noteDao().getAllNotes()

            runOnUiThread {
                recyclerView.adapter = NotesAdapter(notes) { selectedNote ->
                    val intent = Intent(this@MainActivity, NoteDetailActivity::class.java)
                    intent.putExtra("title", selectedNote.title)
                    intent.putExtra("content", selectedNote.content)
                    intent.putExtra("category", selectedNote.category)
                    startActivity(intent)
                }
            }
        }
    }
}

class NotesAdapter(
    private val notes: List<Note>,
    private val onItemClick: (Note) -> Unit
) :
    RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    class NoteViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.itemTitle)
        val content = view.findViewById<TextView>(R.id.itemContent)
        val category = view.findViewById<TextView>(R.id.itemCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.title.text = note.title
        holder.content.text = note.content
        holder.category.text = note.category

        holder.view.setOnClickListener {
            onItemClick(note) // send the clicked note
        }
    }


    override fun getItemCount(): Int = notes.size
}
