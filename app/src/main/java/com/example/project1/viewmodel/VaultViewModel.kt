package com.example.passwordmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.passwordmanager.model.Account
import com.example.passwordmanager.model.Vault
import com.example.passwordmanager.model.VaultIcon
import com.example.passwordmanager.repository.KeePassRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class VaultViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = KeePassRepository(application)

    private val _vaults = MutableStateFlow<List<Vault>>(emptyList())
    val vaults: StateFlow<List<Vault>> = _vaults.asStateFlow()

    private val _currentVault = MutableStateFlow<Vault?>(null)
    val currentVault: StateFlow<Vault?> = _currentVault.asStateFlow()

    private val _currentAccount = MutableStateFlow<Account?>(null)
    val currentAccount: StateFlow<Account?> = _currentAccount.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    init {
        loadVaults()
    }

    private fun loadVaults() {
        viewModelScope.launch {
            _vaults.value = repository.getVaults()
        }
    }

    fun loadVault(vaultId: String) {
        viewModelScope.launch {
            _currentVault.value = repository.getVault(vaultId)
            _isAuthenticated.value = false
        }
    }

    fun loadAccount(vaultId: String, accountId: String) {
        viewModelScope.launch {
            _currentAccount.value = repository.getAccount(vaultId, accountId)
        }
    }

    fun createVault(name: String, password: String, icon: VaultIcon) {
        viewModelScope.launch {
            val newVault = repository.createVault(name, password, icon)
            loadVaults()
        }
    }

    fun addAccount(
        vaultId: String,
        title: String,
        url: String,
        email: String,
        password: String,
        notes: String
    ) {
        viewModelScope.launch {
            val newAccount = Account(
                id = UUID.randomUUID().toString(),
                title = title,
                url = url,
                email = email,
                password = password,
                notes = notes
            )
            repository.addAccount(vaultId, newAccount)
            loadVault(vaultId)
        }
    }

    fun updateAccount(
        vaultId: String,
        accountId: String,
        title: String,
        url: String,
        email: String,
        password: String,
        notes: String
    ) {
        viewModelScope.launch {
            val updatedAccount = Account(
                id = accountId,
                title = title,
                url = url,
                email = email,
                password = password,
                notes = notes
            )
            repository.updateAccount(vaultId, updatedAccount)
            loadVault(vaultId)
        }
    }

    fun deleteAccount(vaultId: String, accountId: String) {
        viewModelScope.launch {
            repository.deleteAccount(vaultId, accountId)
            loadVault(vaultId)
        }
    }

    fun authenticateVault(vaultId: String, password: String): Boolean {
        var result = false
        viewModelScope.launch {
            result = repository.validateVaultPassword(vaultId, password)
            _isAuthenticated.value = result
        }
        return result
    }
}
