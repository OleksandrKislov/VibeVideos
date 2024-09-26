package com.pet.shorts.di

import androidx.media3.common.MediaItem
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import com.pet.shorts.data.database.FavoriteVideoDao
import com.pet.shorts.data.database.FavoriteVideoDataBase
import com.pet.shorts.data.database.PicturesDao
import com.pet.shorts.data.favoriteVideoDataBase
import com.pet.shorts.data.network.RetryInterceptor
import com.pet.shorts.data.pexelsApiBaseUrl
import com.pet.shorts.data.network.pexelsapi.PexelsApiService
import com.pet.shorts.data.repository.VideoRepoImpl
import com.pet.shorts.domain.repository.VideoRepo
import com.pet.shorts.ui.screen.favorite.FavoriteViewModel
import com.pet.shorts.ui.screen.home.HomeViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.pet.shorts.ui.navigation.BottomBarVisibilityManager

val koinModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::FavoriteViewModel)

    single<Retrofit> {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(RetryInterceptor(maxRetryCount = 3))
            .build()

        Retrofit.Builder()
            .baseUrl(pexelsApiBaseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<PexelsApiService> {
        val retrofit: Retrofit = get()
        retrofit.create(PexelsApiService::class.java)
    }

    single<FavoriteVideoDataBase> {
        Room.databaseBuilder(
            androidApplication(),
            FavoriteVideoDataBase::class.java,
            favoriteVideoDataBase
        ).build()
    }

    single<FavoriteVideoDao> {
        val db: FavoriteVideoDataBase = get()
        db.favoriteVideoDao()
    }

    single<PicturesDao> {
        val db: FavoriteVideoDataBase = get()
        db.picturesDao()
    }

    singleOf(::VideoRepoImpl) bind VideoRepo::class

    singleOf(::BottomBarVisibilityManager)

    factory<ExoPlayer> {params ->
        val url: String = params.get()
        ExoPlayer.Builder(androidContext())
            .setHandleAudioBecomingNoisy(true)
            .build().apply {
                repeatMode = REPEAT_MODE_ONE
                setMediaItem(MediaItem.fromUri(url))
                prepare()
            }
    }
}