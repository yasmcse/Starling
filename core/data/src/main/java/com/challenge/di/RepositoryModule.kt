package com.challenge.di

import com.challenge.repository.account.AccountRepositoryImpl
import com.challenge.repository.savinggoals.SavingGoalsRepositoryImpl
import com.challenge.repository.transaction.TransactionsRepositoryImpl
import com.challenge.repositories.AccountRepository
import com.challenge.repositories.SavingsGoalsRepository
import com.challenge.repositories.TransactionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
interface RepositoryModule {
    @Binds
    fun bindAccountRepository(accountRepositoryImpl: AccountRepositoryImpl): AccountRepository

    @Binds
    fun bindTransactionRepository(transactionRepoImpl: TransactionsRepositoryImpl): TransactionsRepository

    @Binds
    fun bindSavingGoalsRepository(savingGoalsRepositoryImpl: SavingGoalsRepositoryImpl): SavingsGoalsRepository
}