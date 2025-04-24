package com.wang.ghc.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.wang.ghc.PopularFragment
import com.wang.ghc.R
import com.wang.ghc.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    // 修改导航栏初始化逻辑
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigationMenu()
        setupNavigation(savedInstanceState)
    }

    private fun setupNavigationMenu() {
        binding.bottomNavigationView.apply {
            menu.clear()
            inflateMenu(R.menu.bottom_nav_menu)
        }
    }

    private fun setupNavigation(savedInstanceState: Bundle?) {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.popularFragment -> replaceFragment(PopularFragment())
                R.id.trendingFragment -> replaceFragment(TrendingFragment())
                R.id.mineFragment -> replaceFragment(MineFragment())
            }
            true
        }

        // Set the default fragment to PopularFragment
        if (savedInstanceState == null) {
            replaceFragment(PopularFragment())
        }
        binding.bottomNavigationView.selectedItemId = R.id.popularFragment
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out_right
            )
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("MainActivity", "onNewIntent=======")
        if(intent != null) {
            val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            if (uri != null) {
                Log.d("MainActivity", "uri scheme=$uri.scheme, host=$uri.host")
            }
        }
        intent?.data?.let { uri ->
            if (uri.scheme == "ghclient" && uri.host == "oauth-callback") {
                binding.bottomNavigationView.selectedItemId = R.id.mineFragment
            }
        }
    }
}