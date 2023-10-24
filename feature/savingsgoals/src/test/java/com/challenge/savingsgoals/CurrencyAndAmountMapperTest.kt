package com.challenge.savingsgoals

import com.challenge.model.SavingAmount
import com.challenge.model.SavingGoalAmount
import com.challenge.savingsgoals.mapper.CurrencyAndAmountMapper
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


@RunWith(JUnit4::class)
class CurrencyAndAmountMapperTest {

    lateinit var sut:CurrencyAndAmountMapper

    @Before
    fun setUp(){
        sut = CurrencyAndAmountMapper()
    }

    @Test
    fun `Given map is called then returns the SavingAmount`(){
        val expected = SavingAmount(SavingGoalAmount("GBP",1234))
        assertEquals(expected, sut.map("GBP",1234))
    }
}