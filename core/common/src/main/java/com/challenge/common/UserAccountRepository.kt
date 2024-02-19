package com.challenge.common


import javax.inject.Inject

// In memory cache to hold the user account details to be used in calling app wide end points
class UserAccountRepository @Inject constructor() {
    private var accountUid: String? = ""
    private var categoryUid: String? = ""

    fun getAccountUid() = accountUid
    fun setAccountId(accUid: String) {
        accountUid = accUid
    }
    fun setCategoryUid(catUid:String){
        categoryUid = catUid
    }
    fun getCategoryUid() = categoryUid
}

