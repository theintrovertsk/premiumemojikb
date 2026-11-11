package com.sgcodexs.premiumkeyboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val imeId = "com.sgcodexs.premiumkeyboard/.CustomKeyboardService"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn_setup).setOnClickListener { setup() }
    }

    private fun setup() {
        if (!isEnabled(this, imeId)) {
            Toast.makeText(this, "Keyboard enable karo", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        } else if (!isDefault(this, imeId)) {
            Toast.makeText(this, "Default keyboard select karo", Toast.LENGTH_LONG).show()
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showInputMethodPicker()
        } else {
            Toast.makeText(this, "✅ Ready! AyuGram se copy karo.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isEnabled(c: Context, id: String): Boolean {
        val imm = c.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return imm.enabledInputMethodList.any { it.id == id }
    }
    private fun isDefault(c: Context, id: String): Boolean {
        return Settings.Secure.getString(c.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD) == id
    }
}
