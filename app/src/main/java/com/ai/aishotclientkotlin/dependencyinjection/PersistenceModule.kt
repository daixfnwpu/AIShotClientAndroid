package com.ai.aishotclientkotlin.dependencyinjection

import android.content.Context
import androidx.room.Room
import com.ai.aishotclientkotlin.data.dao.AppDatabase
import com.ai.aishotclientkotlin.data.dao.MovieDao
import com.ai.aishotclientkotlin.data.dao.ReviewDao
import com.ai.aishotclientkotlin.data.dao.ShotConfigDao
import com.ai.aishotclientkotlin.data.dao.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideRoomDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, "MovieCompose.db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideMovieDao(appDatabase: AppDatabase): MovieDao {
        return appDatabase.movieDao()
    }

    @Provides
    @Singleton
    fun provideProductDao(appDatabase: AppDatabase): ProductDao {
        return appDatabase.productDao()
    }

    @Provides
    @Singleton
    fun provideShotConfigDao(appDatabase: AppDatabase): ShotConfigDao {
        return appDatabase.shotConfigDao()
    }

    @Provides
    @Singleton
    fun provideReviewDao(appDatabase: AppDatabase): ReviewDao {
        return appDatabase.reviewDao()
    }

}