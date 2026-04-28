package com.example.booklist.data.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.booklist.data.db.DBHelper
import com.example.booklist.model.Book

class BookDAO(private val context: Context) {
    private val dbHelper = DBHelper(context)

    fun addLivro(livro: Book): Long{
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("titulo", livro.titulo)
            put("categoria", livro.categoria)
            put("autor", livro.autor)
            put("lido", if (livro.lido) 1 else 0)
        }
        val id = db.insert(DBHelper.TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun getLivros(): List<Book> {
        val db = dbHelper.readableDatabase
        val cursor : Cursor = db.query(DBHelper.TABLE_NAME,
            null, null, null,
            null, null, null
        )

        val bookList = mutableListOf<Book>()

        while(cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"))
            val categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
            val autor = cursor.getString(cursor.getColumnIndexOrThrow("autor"))
            val lido = cursor.getInt(cursor.getColumnIndexOrThrow("lido"))

            bookList.add(Book(id, titulo, categoria, autor, lido == 1))
        }

        cursor.close()
        db.close()
        return bookList
    }

    fun getLivroById(id: Int): Book? {
        val db = dbHelper.readableDatabase
        val cursor : Cursor = db.query(DBHelper.TABLE_NAME,
            null, "id = ?", arrayOf(id.toString()),
            null, null, null
        )
        var livro: Book? = null

        if(cursor.moveToFirst()){
            val idVal = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val titulo = cursor.getString(cursor.getColumnIndexOrThrow("titulo"))
            val categoria = cursor.getString(cursor.getColumnIndexOrThrow("categoria"))
            val autor = cursor.getString(cursor.getColumnIndexOrThrow("autor"))
            val lido = cursor.getInt(cursor.getColumnIndexOrThrow("lido"))

            livro = Book(idVal, titulo, categoria, autor, lido == 1)
        }
        cursor.close()
        db.close()
        return livro
    }

    fun updateLivro(livro: Book): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("titulo", livro.titulo)
            put("categoria", livro.categoria)
            put("autor", livro.autor)
            put("lido", if (livro.lido) 1 else 0)
        }
        val rowsAffected = db.update(DBHelper.TABLE_NAME, values, "id = ?", arrayOf(livro.id.toString()))

        db.close()
        return rowsAffected
    }

    fun deleteLivro(id: Int): Int {
        val db = dbHelper.writableDatabase
        val rowsDeleted = db.delete(DBHelper.TABLE_NAME, "id = ?", arrayOf(id.toString()))

        db.close()
        return rowsDeleted
    }
}
