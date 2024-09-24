package com.challenge.mapper.account.model

data class AccountDomain(
    val accountUid: String,
    val accountType: String,
    val defaultCategory: String
)