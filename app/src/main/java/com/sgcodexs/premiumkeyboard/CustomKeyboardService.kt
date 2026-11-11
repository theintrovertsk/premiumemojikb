package com.sgcodexs.premiumkeyboard

import android.inputmethodservice.InputMethodService
import android.text.SpannableString
import android.text.style.ReplacementSpan
import android.view.View
import android.view.inputmethod.InputConnection
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomKeyboardService : InputMethodService() {
    private lateinit var clipboardHelper: ClipboardHelper
    private val scope = CoroutineScope(Dispatchers.Main)
    private var adapter: EmojiAdapter? = null

    override fun onCreate() {
        super.onCreate()
        clipboardHelper = ClipboardHelper(this, scope)
    }

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_layout, null)
        val recycler = view.findViewById<RecyclerView>(R.id.emoji_recycler)
        recycler.layoutManager = GridLayoutManager(this, 5)
        adapter = EmojiAdapter { emoji -> insertEmoji(emoji) }
        recycler.adapter = adapter
        loadEmojis()
        return view
    }

    private fun loadEmojis() {
        scope.launch {
            val emojis = withContext(Dispatchers.IO) {
                AppDatabase.getDatabase(this@CustomKeyboardService).emojiDao().getAllEmojis()
            }
            adapter?.submitList(emojis)
        }
    }

    private fun insertEmoji(emoji: SavedEmoji) {
        val ic: InputConnection = currentInputConnection ?: return
        val spannable = SpannableString(emoji.fallbackText)
        spannable.setSpan(
            TelegramSpan(emoji.documentId),
            0, emoji.fallbackText.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ic.commitText(spannable, 1)
    }
}

class TelegramSpan(val documentId: Long) : ReplacementSpan() {
    override fun getSize(paint: android.graphics.Paint, text: CharSequence, start: Int, end: Int, fm: android.graphics.Paint.FontMetricsInt?): Int {
        return paint.measureText(text, start, end).toInt()
    }
    override fun draw(canvas: android.graphics.Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: android.graphics.Paint) {
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }
}
