package com.pokedata

import android.app.Application
import com.pokedata.core.data.di.dataModule
import com.pokedata.feature.favorites.di.favoritesModule
import com.pokedata.feature.pokemondetail.di.pokemonDetailModule
import com.pokedata.feature.pokemonlist.di.pokemonListModule
import com.pokedata.feature.search.di.searchModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PokedexApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@PokedexApplication)
            modules(
                dataModule,
                pokemonListModule,
                pokemonDetailModule,
                searchModule,
                favoritesModule
            )
        }
    }
}
