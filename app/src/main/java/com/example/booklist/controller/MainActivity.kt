package com.example.booklist.controller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import com.example.booklist.R
import com.example.booklist.data.dao.BookDAO
import com.example.booklist.model.Book
import kotlin.text.contains

class MainActivity : AppCompatActivity() {

    private lateinit var bookDAO: BookDAO
    private lateinit var listView: ListView
    private lateinit var emptyView: TextView
    private lateinit var booksRead: TextView
    private lateinit var rgFilter: RadioGroup
    private lateinit var searchEditText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.lvBooks)
        emptyView = findViewById(R.id.tvEmpty)
        booksRead = findViewById(R.id.booksRead)
        bookDAO = BookDAO(this)
        rgFilter = findViewById(R.id.rgFilter)
        searchEditText = findViewById(R.id.searchEditText)

        searchEditText.doAfterTextChanged {
            listAllBooks()
        }

        listAllBooks()

        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedBook = parent.getItemAtPosition(position) as Book
            val intent = Intent(this, NewBook::class.java).apply {
                putExtra("bookId", selectedBook.id)
            }
            startActivity(intent)
        }

    }

    fun newBook(view: View) {
        val intent = Intent(this, NewBook::class.java)
        startActivity(intent)
    }

    private fun listAllBooks() {
        val allBooks = bookDAO.getLivros()
        val searchText = searchEditText.text.toString()

        val filteredBooks = allBooks.filter { book ->
            val matchesSearch = book.titulo.contains(searchText, ignoreCase = true)
            matchesSearch
        }

        if (allBooks.isNotEmpty()) {
            val readCount = allBooks.count { it.lido }
            val percentage = (readCount.toDouble() / allBooks.size * 100).toInt()
            booksRead.text = "$percentage% de livros lidos"
        } else {
            booksRead.text = "0% de livros lidos"
        }

        if (filteredBooks.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            listView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            listView.visibility = View.VISIBLE
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, filteredBooks)
            listView.adapter = adapter
        }
    }

    override fun onResume() {
        super.onResume()
        listAllBooks()
    }
}