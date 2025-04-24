package com.wang.ghc.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wang.ghc.model.Repo
import com.wang.ghc.repository.AuthRepository
import com.wang.ghc.repository.RepoRepository
import com.wang.ghc.util.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val repoRepository: RepoRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _shouldLoadRepos = MutableLiveData<Unit>()
    val shouldLoadRepos: LiveData<Unit> = _shouldLoadRepos

    private val _repoList = MutableLiveData<List<Repo>>()
    val repoList: LiveData<List<Repo>> = _repoList

    private val _tokenError = MutableLiveData<String>()
    val tokenError: LiveData<String> = _tokenError

    private val _authComplete = MutableLiveData<Boolean>()
    val authComplete: LiveData<Boolean> = _authComplete

    init {
        _authComplete.value = false
    }

    fun handleOAuthCode(authCode: String) {
        viewModelScope.launch {
            try {
                authRepository.exchangeOAuthToken(authCode)
                _shouldLoadRepos.postValue(Unit)
                _authComplete.postValue(true)
            } catch (e: Exception) {
                Log.e("MineViewModel", "Error exchanging OAuth token")
            }
        }
    }

    fun clearRepositories() {
        _repoList.value = emptyList()
    }

    fun loadMyRepositories() {
        viewModelScope.launch {
            try {
                sessionManager.getAccessToken()?.let { accessToken ->
                    val repositories = repoRepository.getUserRepositories(accessToken)
                    _repoList.postValue(repositories)
                } ?: run {
                    _tokenError.postValue("需要重新登录")
                    Log.e("MineViewModel", "空access token")
                }
            } catch (e: Exception) {
                Log.e("MineViewModel", "Error loading repositories", e)
            }
        }
    }

    fun resetAuthComplete() {
        _authComplete.value = false
    }
}