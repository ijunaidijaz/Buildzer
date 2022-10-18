package com.tradesk.utils

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.view.View
import android.widget.CheckBox


fun SpannableString.spanBold(start: Int, end: Int) {
    apply {
        setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}

fun SpannableString.spanBoldItalic(start: Int, end: Int) {
    apply {
        setSpan(StyleSpan(Typeface.BOLD_ITALIC), start, end, 0)
    }
}

fun SpannableString.spanForegroundColor(start: Int, end: Int, color: Int = Color.BLUE) {
    apply {
        setSpan(ForegroundColorSpan(color), start, end, 0)
    }
}

fun CheckBox.spanClickable(
    text: String,
    start: Int,
    end: Int,
    action: (View) -> Unit
) {
    val spannableString = SpannableString(text)
    spannableString.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    val click = object : ClickableSpan() {
        override fun onClick(widget: View) {
            action(this@spanClickable)
        }

    }
    spannableString.setSpan(click, start, end, 0)
    spannableString.setSpan(ForegroundColorSpan(Color.BLUE), start, end, 0)
    this.text = spannableString

    movementMethod = LinkMovementMethod.getInstance()

}


fun CheckBox.spanClickable(
    text: String,
    start: Int,
    end: Int,
    action: (View) -> Unit,
    start1: Int,
    end1: Int,
    action1: (View) -> Unit
) {
    val spannableString = SpannableString(text)
    spannableString.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    val click = object : ClickableSpan() {
        override fun onClick(widget: View) {
            action(this@spanClickable)
        }

    }
    val click1 = object : ClickableSpan() {
        override fun onClick(widget: View) {
            action1(this@spanClickable)
        }

    }
    spannableString.setSpan(click, start, end, 0)
    spannableString.setSpan(click1, start1, end1, 0)
    spannableString.setSpan(StyleSpan(Typeface.BOLD), 16, 20, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    spannableString.setSpan(ForegroundColorSpan(Color.BLUE), start, end, 0)
    spannableString.setSpan(ForegroundColorSpan(Color.BLUE), start1, end1, 0)
    this.text = spannableString

    movementMethod = LinkMovementMethod.getInstance()

}

fun String.getIndex(char: String) = this.lastIndexOf(char)

