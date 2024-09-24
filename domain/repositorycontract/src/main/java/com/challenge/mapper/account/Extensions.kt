package com.challenge.mapper.account

import com.challenge.mapper.account.model.AccountDomain
import com.challenge.common.model.accountDto.AccountsDto

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
    return accountDomainList.toList()
}