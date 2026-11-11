package com.sgcodexs.premiumkeyboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EmojiAdapter(private val onClick: (SavedEmoji) -> Unit) :
    RecyclerView.Adapter<EmojiAdapter.VH>() {

    private var list = listOf<SavedEmoji>()
    fun submitList(newList: List<SavedEmoji>) { list = newList; notifyDataSetChanged() }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_emoji, parent, false)
        return VH(v, onClick)
    }
    
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(list[position])
    override fun getItemCount() = list.size

    class VH(itemView: View, private val onClick: (SavedEmoji) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val tv: TextView = itemView.findViewById(R.id.emoji_text)
        fun bind(e: SavedEmoji) { tv.text = e.fallbackText; itemView.setOnClickListener { onClick(e) } }
    }
}
