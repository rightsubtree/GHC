package com.wang.ghc.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wang.ghc.R
import com.wang.ghc.model.GitHubContentItem
import com.wang.ghc.model.Repo
import com.wang.ghc.network.GitHubApiService
import com.wang.ghc.ui.adapter.FileAdapter
import com.wang.ghc.util.SessionManager

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class RepositoryContentActivity : AppCompatActivity() {

    @Inject
    lateinit var gitHubService: GitHubApiService
    private lateinit var currentPath: String
    private lateinit var repoFullName: String
    private lateinit var adapter: FileAdapter
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository_content)

        repoFullName = intent.getStringExtra("REPO_FULL_NAME") ?: ""
        currentPath = ""

        setupRecyclerView()
        setupFloatingActionButton()
        loadDirectoryContents()

    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.filesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FileAdapter { item ->
            when (item.type) {
                "dir" -> {
                    currentPath = "${item.path}"
                    loadDirectoryContents(currentPath)
                    updatePathNavigation(currentPath)
                }

                else -> showFileContent(item)
            }
        }
        recyclerView.adapter = adapter
    }

    private fun setupFloatingActionButton() {
        fab = findViewById<FloatingActionButton>(R.id.createIssueButton)
        fab.setOnClickListener {
            startActivity(Intent(this, IssueActivity::class.java).apply {
                putExtra("REPO_FULL_NAME", repoFullName)
            })
        }
    }

    private fun loadDirectoryContents(path: String = "") {
        lifecycleScope.launch {
            try {
                val (owner, repo) = repoFullName.split("/")
                val response = gitHubService.getRepositoryContents(owner, repo, path)
                if (response.isSuccessful) {
                    response.body()?.let { items ->
                        adapter.updateItems(items.map {
                            GitHubContentItem(
                                name = it.name,
                                path = it.path,
                                type = it.type,
                                downloadUrl = it.downloadUrl
                            )
                        })
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@RepositoryContentActivity,
                    "加载失败: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showFileContent(item: GitHubContentItem) {
        lifecycleScope.launch {
            var response: Response<ResponseBody>? = null
            try {
                val (owner, repo) = repoFullName.split("/")
                response = gitHubService.getFileContent(owner, repo, item.path)
                if (response.isSuccessful) {
                    val json = JSONObject(response.body()?.string()!!)
                    val content = json.getString("content")

                    //Log.d("FileContent", "文件内容：$content")

                    if (content.isNullOrEmpty()) {
                        Toast.makeText(
                            this@RepositoryContentActivity,
                            "文件内容为空",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@launch
                    }

                    try {
                        val cleanContent = content.replace("\\n", "")
                            .replace("[^A-Za-z0-9+/=]".toRegex(), "")

                        //Log.d("FileContent", "去除非base64字符后：$cleanContent")

                        startActivity(
                            Intent(
                                this@RepositoryContentActivity,
                                FileContentActivity::class.java
                            ).apply {
                                putExtra("CONTENT", cleanContent)
                                putExtra("FILE_PATH", item.path)
                            })
                    } catch (e: IllegalArgumentException) {
                        Toast.makeText(
                            this@RepositoryContentActivity,
                            "文件格式错误: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@RepositoryContentActivity,
                            "解码失败: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@RepositoryContentActivity,
                    "加载失败: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                response?.body()?.close()
            }
        }
    }

    fun updatePathNavigation(path: String) {
        val pathLayout = findViewById<LinearLayout>(R.id.pathLayout)
        pathLayout.removeAllViews()

        val paths = path.split('/').filter { it.isNotEmpty() }
        var currentPath = ""

        paths.forEachIndexed { index, segment ->
            currentPath += "$segment/"
            val textView = TextView(this).apply {
                text = segment
                setTextColor(ContextCompat.getColor(context, R.color.primary))
                setOnClickListener {
                    loadDirectoryContents(paths.take(index + 1).joinToString("/"))
                }
            }
            pathLayout.addView(textView)
        }

        if (pathLayout.childCount > 0) {
            val divider = TextView(this).apply {
                text = "/"
                setTextColor(ContextCompat.getColor(context, R.color.secondary))
            }
            pathLayout.addView(divider)
        }
    }

    private fun updateFabVisibility(isOwner: Boolean?) {
        fab.visibility = if (isOwner == true) View.VISIBLE else View.GONE
    }

    companion object {
        fun newIntent(context: Context, repo: Repo) = Intent(context, RepositoryContentActivity::class.java).apply {
            putExtra("REPO_FULL_NAME", "${repo.owner.login}/${repo.name}")
        }
    }
}