package com.wang.ghc.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wang.ghc.R
import com.wang.ghc.model.GitHubContentItem

class FileAdapter(private val onClick: (GitHubContentItem) -> Unit) :
    RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    private val items = mutableListOf<GitHubContentItem>()

    inner class FileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.fileIcon)
        val name: TextView = view.findViewById(R.id.fileName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_file, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.icon.setImageResource(
            when (item.type) {
                "dir" -> R.drawable.ic_folder
                else -> R.drawable.ic_file
            }
        )
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<GitHubContentItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}