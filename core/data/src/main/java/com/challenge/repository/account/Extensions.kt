package com.challenge.repository.account

import com.challenge.common.model.accountDomain.AccountDomain
import com.challenge.common.model.accountDto.AccountsDto
import okhttp3.internal.toImmutableList

fun AccountsDto.toAccountsDomain(): List<AccountDomain> {
    val accountDomainList = mutableListOf<AccountDomain>()
    for (account in this.accountDtos) {
        accountDomainList.add(
            AccountDomain(
                account.accountUid,
                account.accountType,
                account.defaultCategory
            )
        )
    }
    return accountDomainList.toImmutableList()
}