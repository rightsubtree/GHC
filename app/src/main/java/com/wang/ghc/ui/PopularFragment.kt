package com.wang.ghc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wang.ghc.adapter.RepoAdapter
import com.wang.ghc.databinding.FragmentPopularBinding
import com.wang.ghc.ui.RepositoryContentActivity
import com.wang.ghc.viewmodel.RepoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PopularFragment : Fragment() {
    private var _binding: FragmentPopularBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private val viewModel: RepoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPopularBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchPopularRepos()
        val adapter = RepoAdapter { repo ->
            val intent = RepositoryContentActivity.newIntent(requireContext(), repo)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        viewModel.repos.observe(viewLifecycleOwner) { repos ->
            if (repos.isNullOrEmpty()) {
                recyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                binding.emptyView.visibility = View.GONE
                adapter.submitList(repos)
            }
        }
    }
}