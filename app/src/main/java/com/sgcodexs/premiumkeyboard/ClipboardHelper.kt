package com.sgcodexs.premiumkeyboard

import android.content.ClipboardManager
import android.content.Context
import android.text.Spanned
import android.text.style.ReplacementSpan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClipboardHelper(private val context: Context, private val scope: CoroutineScope) {
    private val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private val db = AppDatabase.getDatabase(context)

    init {
        clipboard.addPrimaryClipChangedListener {
            extractPremiumEmojis()
        }
    }

    private fun extractPremiumEmojis() {
        val clip = clipboard.primaryClip ?: return
        if (clip.itemCount == 0) return
        val text = clip.getItemAt(0).text ?: return

        if (text is Spanned) {
            val spans = text.getSpans(0, text.length, ReplacementSpan::class.java)
            for (span in spans) {
                if (span.javaClass.name.contains("CustomEmojiSpan")) {
                    try {
                        val field = span.javaClass.getDeclaredField("documentId")
                        field.isAccessible = true
                        val docId = field.getLong(span)
                        val start = text.getSpanStart(span)
                        val end = text.getSpanEnd(span)
                        val emojiText = text.subSequence(start, end).toString()

                        scope.launch(Dispatchers.IO) {
                            db.emojiDao().insertEmoji(SavedEmoji(documentId = docId, fallbackText = emojiText))
                        }
                    } catch (_: Exception) { }
                }
            }
        }
    }
}
