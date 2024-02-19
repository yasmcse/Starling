package com.challenge.common.model.newsavingdto

import com.challenge.common.model.savinggoaldto.SavingTarget

data class NewSavingGoal(
    val name: String,
    val currency: String,
    val target: SavingTarget,
    val base64EncodedPhoto: String?
)