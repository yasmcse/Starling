package com.challenge.ui

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challenge.common.NetworkResult
import com.challenge.common.model.newsavingdomain.NewSavingGoalResponseDomain
import com.challenge.usecase.CreateNewSavingGoalUseCase
import com.challenge.savingsgoals.mapper.NewSavingGoalMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNewGoalViewModel @Inject constructor(
    private val createNewSavingGoalUseCase: CreateNewSavingGoalUseCase,
    private val newSavingGoalMapper: NewSavingGoalMapper
) : ViewModel() {

    private val _newGoal: MutableStateFlow<NetworkResult<NewSavingGoalResponseDomain>> =
        MutableStateFlow(NetworkResult.Loading())
    val newGoal = _newGoal.asStateFlow()

    fun postNewSavingGoal(
        tripName: String,
        currency: String,
        amount: Long
    ) = viewModelScope.launch {
        createNewSavingGoalUseCase.createNewSavingGoal(
            newSavingGoalMapper.map(tripName, currency, amount)
        ).collect {
            _newGoal.value = it
        }
    }

    fun isValidInput(trip: String?, amount: String?) =
        trip?.isNotEmpty() == true &&
                amount?.isDigitsOnly() == true
}