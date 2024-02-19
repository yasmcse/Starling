package com.challenge.savingsgoals

import com.challenge.common.model.newsavingdto.NewSavingGoal
import com.challenge.common.model.savinggoaldto.SavingTarget
import com.challenge.savingsgoals.mapper.NewSavingGoalMapper
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.Assert.assertEquals

@RunWith(JUnit4::class)
class NewSavingGoalMapperTest {

    private lateinit var sut:NewSavingGoalMapper

    @Before
    fun setUp(){
        sut = NewSavingGoalMapper()
    }

    @Test
    fun `Given map is called then returns the NewSavingGoal`(){
        val expected = NewSavingGoal("France","GBP", SavingTarget("GBP",1234),null)

        assertEquals(expected, sut.map("France","GBP",1234))
    }
}