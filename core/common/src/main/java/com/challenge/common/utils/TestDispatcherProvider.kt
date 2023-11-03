package com.challenge.common.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import javax.inject.Inject

class TestDispatcherProvider @Inject constructor() : DispatcherProvider {
    val testCoroutineDispatcher = TestCoroutineDispatcher()

    override val main: TestCoroutineDispatcher
        get() = testCoroutineDispatcher
    override val io: CoroutineDispatcher
        get() = testCoroutineDispatcher
    override val default: CoroutineDispatcher
        get() = testCoroutineDispatcher
}