package com.challenge.model

data class NewSavingGoal(
    val name: String,
    val currency: String,
    val target: SavingTarget,
    val base64EncodedPhoto: String?
)