package com.example.booklist.controller

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.booklist.R
import com.example.booklist.data.dao.BookDAO
import com.example.booklist.model.Book

class NewBook : AppCompatActivity() {

    private lateinit var edtTitulo: EditText
    private lateinit var edtAutor: EditText
    private lateinit var edtCategoria: EditText
    private lateinit var cbLido: CheckBox
    private lateinit var btnSalvar: Button
    private lateinit var btnExcluir: Button
    private lateinit var btnBack: ImageView

    private lateinit var bookDAO: BookDAO
    private var bookId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_book)

        edtTitulo = findViewById(R.id.edtTitulo)
        edtAutor = findViewById(R.id.edtAutor)
        edtCategoria = findViewById(R.id.edtCategoria)
        cbLido = findViewById(R.id.cbLido)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnExcluir = findViewById(R.id.btnExcluir)
        btnBack = findViewById(R.id.btnBack)

        bookDAO = BookDAO(this)

        bookId = intent.getIntExtra("bookId", -1)

        if (bookId != -1) {
            carregarLivro(bookId)
            btnExcluir.visibility = View.VISIBLE
        } else {
            btnExcluir.visibility = View.GONE
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnSalvar.setOnClickListener {
            salvarLivro()
        }

        btnExcluir.setOnClickListener {
            excluirLivro()
        }
    }

    private fun carregarLivro(id: Int) {

        val livro = bookDAO.getLivroById(id)

        if (livro != null) {
            edtTitulo.setText(livro.titulo)
            edtAutor.setText(livro.autor)
            edtCategoria.setText(livro.categoria)
            cbLido.isChecked = livro.lido
        }
    }

    private fun salvarLivro() {

        val titulo = edtTitulo.text.toString().trim()
        val autor = edtAutor.text.toString().trim()
        val categoria = edtCategoria.text.toString().trim()
        val lido = cbLido.isChecked

        if (titulo.isEmpty()) {
            edtTitulo.error = "Digite o título"
            edtTitulo.requestFocus()
            return
        }

        if (autor.isEmpty()) {
            edtAutor.error = "Digite o autor"
            edtAutor.requestFocus()
            return
        }

        if (categoria.isEmpty()) {
            edtCategoria.error = "Digite a categoria"
            edtCategoria.requestFocus()
            return
        }

        val livro = Book(
            id = bookId,
            titulo = titulo,
            categoria = categoria,
            autor = autor,
            lido = lido
        )

        if (bookId == -1) {
            bookDAO.addLivro(livro)
            Toast.makeText(this, "Livro cadastrado!", Toast.LENGTH_SHORT).show()
        } else {
            bookDAO.updateLivro(livro)
            Toast.makeText(this, "Livro atualizado!", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    private fun excluirLivro() {

        if (bookId != -1) {
            bookDAO.deleteLivro(bookId)
            Toast.makeText(this, "Livro excluído!", Toast.LENGTH_SHORT).show()
        }

        finish()
    }
}