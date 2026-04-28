package com.example.booklist.controller

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
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

class MainActivity : AppCompatActivity() {

    private lateinit var bookDAO: BookDAO
    private lateinit var listView: ListView
    private lateinit var emptyView: TextView
    private lateinit var booksRead: TextView
    private lateinit var rgFilter: RadioGroup
    private lateinit var searchEditText: EditText
    private lateinit var listaCompleta: MutableList<Book>
    private lateinit var listaExibida: MutableList<Book>
    private lateinit var adapter: ArrayAdapter<Book>

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

        listaCompleta = mutableListOf()
        listaExibida = mutableListOf()

        adapter = object : ArrayAdapter<Book>(this, android.R.layout.simple_list_item_1, listaExibida) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                val book = getItem(position)
                textView.text = book?.toSpannable()
                return view
            }
        }
        listView.adapter = adapter

        searchEditText.doAfterTextChanged {
            applyFilters()
        }

        rgFilter.setOnCheckedChangeListener { _, _ ->
            applyFilters()
        }

        listView.setOnItemClickListener { parent, _, position, _ ->
            val selectedBook = parent.getItemAtPosition(position) as Book
            val intent = Intent(this, NewBook::class.java).apply {
                putExtra("bookId", selectedBook.id)
            }
            startActivity(intent)
        }

        refreshData()
    }

    fun newBook(view: View) {
        val intent = Intent(this, NewBook::class.java)
        startActivity(intent)
    }

    private fun refreshData() {
        // listaCompleta = bookDAO.getLivros().toMutableList()
        listaCompleta = getMockBooks()
        
        updateStatistics()
        applyFilters()
    }

    private fun applyFilters() {
        val searchText = searchEditText.text.toString()
        val checkedId = rgFilter.checkedRadioButtonId

        val filtered = listaCompleta.filter { book ->
            val matchesSearch = book.titulo.contains(searchText, ignoreCase = true) || 
                               book.autor.contains(searchText, ignoreCase = true)
            
            val matchesFilter = when (checkedId) {
                R.id.rbRead -> book.lido
                R.id.rbUnread -> !book.lido
                else -> true
            }
            
            matchesSearch && matchesFilter
        }

        listaExibida.clear()
        listaExibida.addAll(filtered)
        adapter.notifyDataSetChanged()

        if (listaExibida.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            listView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            listView.visibility = View.VISIBLE
        }
    }

    private fun updateStatistics() {
        if (listaCompleta.isNotEmpty()) {
            val readCount = listaCompleta.count { it.lido }
            val percentage = (readCount.toDouble() / listaCompleta.size * 100).toInt()
            booksRead.text = "$percentage% de livros lidos"
        } else {
            booksRead.text = "0% de livros lidos"
        }
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    private fun getMockBooks(): MutableList<Book> {
        return mutableListOf(
            Book(1, "Harry Potter", "Fantasia", "J.K. Rowling", true),
            Book(2, "O Hobbit", "Fantasia", "Tolkien", false),
            Book(3, "Dom Casmurro", "Romance", "Machado de Assis", true),
            Book(4, "1984", "Distopia", "George Orwell", false),
            Book(5, "It", "Terror", "Stephen King", false),
            Book(6, "Orgulho e Preconceito", "Romance", "Jane Austen", true)
        )
    }
}
