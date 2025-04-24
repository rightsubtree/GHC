package com.wang.ghc.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wang.ghc.databinding.ActivityIssueBinding
import com.wang.ghc.model.IssueRequest
import com.wang.ghc.network.GitHubApiService
import com.wang.ghc.util.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class IssueActivity : AppCompatActivity() {

    @Inject
    lateinit var gitHubService: GitHubApiService

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var binding: ActivityIssueBinding
    private lateinit var repoFullName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIssueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repoFullName = intent.getStringExtra("REPO_FULL_NAME") ?: ""

        setupUI()
    }

    private fun setupUI() {
        binding.submitButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val body = binding.bodyEditText.text.toString()

            if (title.isBlank() || body.isBlank()) {
                android.widget.Toast.makeText(this, "标题和内容不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            createIssue(title, body)
        }
    }

    private fun createIssue(title: String, body: String) {
        val (owner, repo) = repoFullName.split("/")

        val issueRequest = IssueRequest(title, body)

        val token = sessionManager.accessToken.value ?: return

        CoroutineScope(Dispatchers.IO).launch {
            val response = gitHubService.createIssue(owner, repo, issueRequest, "token $token")

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    android.widget.Toast.makeText(
                        this@IssueActivity,
                        "提交成功",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    android.widget.Toast.makeText(
                        this@IssueActivity,
                        "提交失败, 失败原因: ${response.errorBody()?.string()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}