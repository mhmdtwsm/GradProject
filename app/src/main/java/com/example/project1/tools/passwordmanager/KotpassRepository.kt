package com.example.passwordmanager.repository

import android.content.Context
import com.example.passwordmanager.model.Account
import com.example.passwordmanager.model.Vault
import com.example.passwordmanager.model.VaultIcon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Repository that interfaces with KeePassDroid
 */
class KeePassRepository(private val context: Context) {

    private val vaults = mutableListOf<Vault>()

    init {
        // Add some sample data for testing
        vaults.add(
            Vault(
                id = "1",
                name = "Work Passwords Vault",
                icon = VaultIcon.WORK,
                password = "password123",
                accounts = listOf(
                    Account(
                        id = "1",
                        title = "YouTube Account",
                        url = "https://youtube.com",
                        email = "example@gmail.com",
                        password = "password123",
                        notes = "Work YouTube account"
                    )
                )
            )
        )

    }

    suspend fun getVaults(): List<Vault> = withContext(Dispatchers.IO) {
        return@withContext vaults
    }

    suspend fun getVault(vaultId: String): Vault? = withContext(Dispatchers.IO) {
        return@withContext vaults.find { it.id == vaultId }
    }

    suspend fun createVault(name: String, password: String, icon: VaultIcon): Vault =
        withContext(Dispatchers.IO) {
            val newVault = Vault(
                id = UUID.randomUUID().toString(),
                name = name,
                icon = icon,
                password = password
            )
            vaults.add(newVault)
            return@withContext newVault
        }

    suspend fun addAccount(vaultId: String, account: Account): Boolean =
        withContext(Dispatchers.IO) {
            val vaultIndex = vaults.indexOfFirst { it.id == vaultId }
            if (vaultIndex != -1) {
                val vault = vaults[vaultIndex]
                val updatedAccounts = vault.accounts.toMutableList()
                updatedAccounts.add(account)
                vaults[vaultIndex] = vault.copy(accounts = updatedAccounts)
                return@withContext true
            }
            return@withContext false
        }

    suspend fun getAccount(vaultId: String, accountId: String): Account? =
        withContext(Dispatchers.IO) {
            val vault = vaults.find { it.id == vaultId }
            return@withContext vault?.accounts?.find { it.id == accountId }
        }

    suspend fun updateAccount(vaultId: String, account: Account): Boolean =
        withContext(Dispatchers.IO) {
            val vaultIndex = vaults.indexOfFirst { it.id == vaultId }
            if (vaultIndex != -1) {
                val vault = vaults[vaultIndex]
                val accountIndex = vault.accounts.indexOfFirst { it.id == account.id }
                if (accountIndex != -1) {
                    val updatedAccounts = vault.accounts.toMutableList()
                    updatedAccounts[accountIndex] = account
                    vaults[vaultIndex] = vault.copy(accounts = updatedAccounts)
                    return@withContext true
                }
            }
            return@withContext false
        }

    suspend fun deleteAccount(vaultId: String, accountId: String): Boolean =
        withContext(Dispatchers.IO) {
            val vaultIndex = vaults.indexOfFirst { it.id == vaultId }
            if (vaultIndex != -1) {
                val vault = vaults[vaultIndex]
                val updatedAccounts = vault.accounts.filterNot { it.id == accountId }
                vaults[vaultIndex] = vault.copy(accounts = updatedAccounts)
                return@withContext true
            }
            return@withContext false
        }

    suspend fun validateVaultPassword(vaultId: String, password: String): Boolean =
        withContext(Dispatchers.IO) {
            val vault = vaults.find { it.id == vaultId }
            return@withContext vault?.password == password
        }
}
