package com.challenge.transaction.extension

import com.challenge.common.model.Amount
import com.challenge.common.model.transactiondomain.TransactionDomain
import com.challenge.common.model.transactiondomain.TransactionsDomain
import com.challenge.common.model.transactiondto.TransactionDto
import com.challenge.common.model.transactiondto.TransactionsDto
import com.challenge.repository.transaction.toTransactionsDomain
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TransactionsExtensionsTest {
    @Test
    fun `map transactionsDto to TransactionsDomain`() {
        val transactionsDto = TransactionsDto(
            listOf(
                TransactionDto(
                    "feedItemUid", "categoryUid",
                    Amount("GBP", 1234L, 0.0),
                    Amount("GBP", 1234L, 0.0),
                    "IN"
                )
            )
        )
        val transactionsDomain = TransactionsDomain(
            listOf(
                TransactionDomain(
                    "feedItemUid", "categoryUid",
                    Amount("GBP", 1234L, 0.0),
                    Amount("GBP", 1234L, 0.0),
                    "IN"
                )
            )
        )

        assertEquals(transactionsDomain,transactionsDto.toTransactionsDomain())
    }
}