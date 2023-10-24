package com.challenge.starlingbank.di

import com.challenge.common.StarlingNavigator
import com.challenge.starlingbank.navigation.StarlingNavigationImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
interface NavigationModule {
    @Binds
    fun bindStarlingNavigation(startlingNavImpl: StarlingNavigationImpl): StarlingNavigator
}
