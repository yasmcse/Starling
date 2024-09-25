package com.challenge.mapper.account.extensions

import com.challenge.model.account.AccountDomain
import com.challenge.common.model.accountDto.AccountDto
import com.challenge.common.model.accountDto.AccountsDto
import com.challenge.mapper.account.toAccountsDomain
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AccountsDtoToAccountsDomainTest {

    @Test
    fun `map AccountsDto to AccountsDomain`() {

        val accountsDto = AccountsDto(listOf(AccountDto("accountUid", "accountType", "defaultCategory","GBP","","")))
        val accountsDomain =
            listOf(AccountDomain("accountUid", "accountType", "defaultCategory"))
        assertEquals(accountsDomain,accountsDto.toAccountsDomain())
    }
}