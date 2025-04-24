package com.wang.ghc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.wang.ghc.model.Repo
import com.wang.ghc.repository.RepoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepoViewModel @Inject constructor(
    private val repository: RepoRepository
) : ViewModel() {

    private val _repos = MutableLiveData<List<Repo>>()
    val repos: LiveData<List<Repo>> = _repos

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val TAG = "RepoViewModel"

    fun fetchPopularRepos() {
        viewModelScope.launch {
            try {
                val repos = repository.getPopularRepos()
                _repos.value = repos
                Log.d(TAG, "成功获取${repos.size}个仓库")
            } catch (e: Exception) {
                _error.postValue(e.message ?: "未知错误")
                Log.e(TAG, "获取仓库失败", e)
            }
        }
    }
}