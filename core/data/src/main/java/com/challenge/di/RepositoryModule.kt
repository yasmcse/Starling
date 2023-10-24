package com.challenge.di

import com.challenge.repository.account.AccountRepository
import com.challenge.repository.account.AccountRepositoryImpl
import com.challenge.repository.savinggoals.SavingGoalsRepoImpl
import com.challenge.repository.savinggoals.SavingGoalsRepository
import com.challenge.repository.transaction.TransactionsRepository
import com.challenge.repository.transaction.TransactionsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {
    @Binds
    fun bindAccountRepositpry(accountRepositoryImpl: AccountRepositoryImpl): AccountRepository

    @Binds
    fun bindTransactionRepository(transactionRepoImpl: TransactionsRepositoryImpl): TransactionsRepository

    @Binds
    fun bindSavingGoalsRepository(savingGoalsRepositoryImpl: SavingGoalsRepoImpl): SavingGoalsRepository
}