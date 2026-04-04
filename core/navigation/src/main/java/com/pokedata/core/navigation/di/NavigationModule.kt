package com.pokedata.core.navigation.di

import androidx.navigation.NavHostController
import org.koin.dsl.module

val navigationModule = module {
    factory<NavHostController> {
        error("NavHostController must be provided by the Activity")
    }
}
