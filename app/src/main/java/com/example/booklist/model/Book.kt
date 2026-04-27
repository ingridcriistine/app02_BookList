package com.example.booklist.model

data class Book(
  val id: Int = 0,
    val titulo: String,
    val categoria: String,
    val autor: String,
    val lido: Boolean = false
){}

