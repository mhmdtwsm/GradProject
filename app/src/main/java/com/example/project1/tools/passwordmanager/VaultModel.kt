package com.example.passwordmanager.model

enum class VaultIcon {
    WORK, SOCIAL, WARNING, DOTS
}

data class Vault(
    val id: String,
    val name: String,
    val icon: VaultIcon,
    val password: String,
    val accounts: List<Account> = emptyList()
)

data class Account(
    val id: String,
    val title: String,
    val url: String,
    val email: String,
    val password: String,
    val notes: String
)
