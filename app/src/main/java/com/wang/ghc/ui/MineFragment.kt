package com.wang.ghc.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.wang.ghc.R
import com.wang.ghc.adapter.RepoAdapter
import com.wang.ghc.databinding.FragmentMineBinding
import com.wang.ghc.model.Repo
import com.wang.ghc.network.GitHubApiService
import com.wang.ghc.repository.RepoRepository
import com.wang.ghc.util.SessionManager
import com.wang.ghc.viewmodel.MineViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MineFragment : Fragment() {

    @Inject
    lateinit var repoRepository: RepoRepository

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var gitHubService: GitHubApiService

    private val viewModel: MineViewModel by viewModels()

    private var _binding: FragmentMineBinding? = null
    private val binding: FragmentMineBinding
        get() = _binding!!

    private val repoAdapter by lazy {

        RepoAdapter { repo ->
            val intent = RepositoryContentActivity.newIntent(requireContext(), repo)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMineBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        with(binding) {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = repoAdapter
            loginButton.setOnClickListener {
                startActivityForResult(
                    Intent(requireContext(), OAuthActivity::class.java),
                    OAUTH_REQUEST_CODE
                )
            }
            logoutButton.setOnClickListener {
                sessionManager.logoff()
                viewModel.clearRepositories()
                repoAdapter.submitList(emptyList())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("MineFragment", "onActivityResult=======")
        if (requestCode == OAUTH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val authCode = data?.getStringExtra("AUTH_CODE") ?: return
            viewModel.handleOAuthCode(authCode)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager.accessToken.observe(viewLifecycleOwner) { token ->
            updateLoginState(token != null)
            updateUIState() // 强制刷新所有UI元素
        }
        viewModel.repoList.observe(viewLifecycleOwner) { list: List<Repo> ->
            Log.d("MineFragment", "Received repos: ${list.size}")
            if (list.isNotEmpty()) {
                repoAdapter.submitList(list)
                repoAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show()
                repoAdapter.notifyDataSetChanged()
            }
        }
        viewModel.loadMyRepositories()
    }

    override fun onResume() {
        super.onResume()
        Log.d(
            "MineFragment",
            "onResume sessionManager.currentUser.value=${sessionManager.currentUser.value}"
        )
        sessionManager.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                viewModel.loadMyRepositories()
            }
        }
        viewModel.authComplete.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.loadMyRepositories()
                viewModel.resetAuthComplete()
            }
        }
        updateUIState()
    }

    private fun updateUIState() {
        binding.textPrompt.text = if (sessionManager.accessToken.value != null) "我的仓库" else "请登录查看仓库"
        binding.loginButton.visibility = if (sessionManager.accessToken.value == null) View.VISIBLE else View.GONE
        binding.logoutButton.visibility = if (sessionManager.accessToken.value != null) View.VISIBLE else View.GONE
    }

    private fun updateLoginState(isLoggedIn: Boolean) {
        binding.textPrompt.visibility = if (isLoggedIn) View.GONE else View.VISIBLE
    }

    // 在onDestroyView中取消协程
    override fun onDestroyView() {
        super.onDestroyView()
        lifecycleScope.cancel()
    }

    companion object {
        private const val OAUTH_REQUEST_CODE = 1001
    }
}