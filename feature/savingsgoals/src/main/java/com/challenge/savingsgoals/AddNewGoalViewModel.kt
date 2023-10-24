package com.challenge.savingsgoals

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.challenge.di.ApiResponse
import com.challenge.di.NetworkResult
import com.challenge.domain.CreateNewSavingGoalUseCase
import com.challenge.model.NewSavingGoalResponse
import com.challenge.savingsgoals.mapper.NewSavingGoalMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNewGoalViewModel @Inject constructor(
    private val createNewSavingGoalUseCase: CreateNewSavingGoalUseCase,
    private val newSavingGoalMapper: NewSavingGoalMapper
) : ViewModel() {

    private val _response: MutableStateFlow<NetworkResult<NewSavingGoalResponse>> =
        MutableStateFlow(NetworkResult.Loading())
    val response = _response

    @VisibleForTesting
    fun postNewSavingGoal(
        tripName: String,
        currency: String,
        amount: Long
    ) = viewModelScope.launch {
        createNewSavingGoalUseCase.createNewSavingGoal(
            newSavingGoalMapper.map(tripName, currency, amount)
        ).collect {
            _response.value = it
        }
    }
}