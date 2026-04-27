package com.example.booklist.model

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan

data class Book(
  val id: Int = 0,
    val titulo: String,
    val categoria: String,
    val autor: String,
    val lido: Boolean = false
){
  override fun toString(): String {
    val status = if (lido) "Lido" else "Não lido"
    return "$titulo | $autor\n$status"
  }

  fun toSpannable(): SpannableString {
    val status = if (lido) "Lido" else "Não lido"
    val texto = toString()
    val spannable = SpannableString(texto)

    val start = texto.indexOf(status)
    val end = start + status.length

    val color = if (lido) Color.parseColor("#2E7D32") else Color.parseColor("#C62828")

    spannable.setSpan(
      ForegroundColorSpan(color),
      start,
      end,
      Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannable
  }
}

