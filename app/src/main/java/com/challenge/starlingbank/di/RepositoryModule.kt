package com.challenge.starlingbank.di

import com.challenge.repository.account.AccountRepositoryImpl
import com.challenge.repository.savinggoals.SavingGoalsRepositoryImpl
import com.challenge.repository.transaction.TransactionsRepositoryImpl
import com.challenge.repositorycontract.AccountRepository
import com.challenge.repositorycontract.SavingsGoalsRepository
import com.challenge.repositorycontract.TransactionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {
    @Binds
    fun bindAccountRepository(accountRepositoryImpl: AccountRepositoryImpl): AccountRepository

    @Binds
    fun bindTransactionRepository(transactionRepoImpl: TransactionsRepositoryImpl): TransactionsRepository

    @Binds
    fun bindSavingGoalsRepository(savingGoalsRepositoryImpl: SavingGoalsRepositoryImpl): SavingsGoalsRepository
}