package com.wang.ghc.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wang.ghc.databinding.ItemRepoBinding
import com.wang.ghc.model.Repo

class RepoAdapter(private val onClick: (Repo) -> Unit) : ListAdapter<Repo, RepoAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRepoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repo = getItem(position)
        holder.bind(repo)
        holder.itemView.setOnClickListener { onClick(repo) }
    }

    override fun getItemCount() = currentList.size

    class ViewHolder(private val binding: ItemRepoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(repo: Repo) {
            binding.tvRepoName.text = "${repo.owner.login}/${repo.name}"
            binding.tvOwner.text = repo.owner.login
            binding.tvDescription.text = repo.description ?: "No description"
            binding.tvLanguage.text = repo.language ?: "Unknown"
            binding.tvStars.text = repo.stargazers_count.toString()
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Repo>() {
        override fun areItemsTheSame(oldItem: Repo, newItem: Repo) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Repo, newItem: Repo) = oldItem == newItem
    }
}