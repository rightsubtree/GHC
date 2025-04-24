package com.wang.ghc.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wang.ghc.adapter.RepoAdapter
import com.wang.ghc.databinding.FragmentTrendingBinding
import com.wang.ghc.network.GitHubApiService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TrendingFragment : Fragment() {
    @Inject
    lateinit var gitHubService: GitHubApiService
    private var _binding: FragmentTrendingBinding? = null
    private val binding get() = _binding!!
    private val repoAdapter = RepoAdapter { repo ->
        val intent = RepositoryContentActivity.newIntent(requireContext(), repo)
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrendingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initFilters()
        loadTrendingRepositories()
    }

    private fun initRecyclerView() {
        binding.repoList.layoutManager = LinearLayoutManager(requireContext())
        binding.repoList.adapter = repoAdapter
    }

    private fun initFilters() {
        val languages = listOf("All", "C", "C++", "Java", "Kotlin", "Python", "JavaScript")
        val dateRanges = listOf("Today", "This Week", "This Month")
        val languageAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        val dateAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dateRanges)
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.languageSpinner.adapter = languageAdapter
        binding.dateSpinner.adapter = dateAdapter
        binding.languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                loadTrendingRepositories()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.dateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                loadTrendingRepositories()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadTrendingRepositories() {
        val language = binding.languageSpinner.selectedItem.toString()
        val dateRange = binding.dateSpinner.selectedItem.toString()
        lifecycleScope.launch {
            val query = buildQueryString(language, dateRange)
            val response = gitHubService.searchRepositories(query)
            if (response.isSuccessful) {
                val searchResponse = response.body()
                if (searchResponse != null) {
                    val repositories = searchResponse
                    repoAdapter.submitList(repositories?.items)
                } else {
                    Log.e("TrendingFragment", "Failed to load repositories: ${response.code()} ${response.message()}")
                }
            }
        }
    }

    private fun buildQueryString(language: String, dateRange: String): String {
        val languageQuery = if (language == "All") "" else "language:$language"
        val dateRangeQuery = convertDateRange(dateRange)
        return "$languageQuery created:>=${dateRangeQuery}"
    }

    private fun convertDateRange(dateRange: String): String {
        val today = java.time.LocalDate.now()
        return when (dateRange) {
            "Today" -> today.minusDays(1).toString()
            "This Week" -> today.minusWeeks(1).toString()
            "This Month" -> today.minusMonths(1).toString()
            else -> today.minusDays(1).toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}