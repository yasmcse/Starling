package com.challenge.common

import com.challenge.common.utils.DefaultDispatcherProvider
import com.challenge.common.utils.DispatcherProvider
import com.challenge.common.utils.TestDispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
interface CommonModule {

    @Binds
    fun bindsDispatcherProvider(defaultDispatcherProvider: DefaultDispatcherProvider): DispatcherProvider

    @Binds
    fun bindsTestDispatcherProvider(testDispatcherProvider: TestDispatcherProvider): TestDispatcherProvider
}