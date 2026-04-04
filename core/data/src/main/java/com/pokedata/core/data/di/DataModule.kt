package com.pokedata.core.data.di

import android.content.Context
import com.pokedata.core.data.local.PokemonDatabase
import com.pokedata.core.data.remote.PokemonApi
import com.pokedata.core.data.repository.PokemonRepository
import com.pokedata.core.data.repository.PokemonRepositoryInterface
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import java.io.File
import java.util.concurrent.TimeUnit

val dataModule = module {
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE
        }
    }

    single {
        val context: Context = get()
        val cacheSize = 10L * 1024 * 1024 // 10 MB
        val cache = Cache(File(context.cacheDir, "http_cache"), cacheSize)

        OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single<PokemonApi> {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .client(get())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(PokemonApi::class.java)
    }

    single {
        PokemonDatabase.getInstance(get())
    }

    single { get<PokemonDatabase>().pokemonDao() }
    single { get<PokemonDatabase>().pokemonTypeDao() }
    single { get<PokemonDatabase>().abilityDao() }
    single { get<PokemonDatabase>().baseStatsDao() }

    single<PokemonRepositoryInterface> {
        PokemonRepository(
            api = get(),
            pokemonDao = get(),
            pokemonTypeDao = get(),
            abilityDao = get(),
            baseStatsDao = get()
        )
    }
}
