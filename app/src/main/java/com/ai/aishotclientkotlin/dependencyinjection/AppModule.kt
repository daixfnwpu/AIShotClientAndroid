package com.ai.aishotclientkotlin.dependencyinjection


import android.content.Context
import coil3.ImageLoader
import com.ai.aishotclientkotlin.data.remote.Api
import com.ai.aishotclientkotlin.data.remote.MovieService
import com.ai.aishotclientkotlin.data.remote.PeopleService
import com.ai.aishotclientkotlin.data.remote.ShopService
import com.ai.aishotclientkotlin.data.remote.TheDiscoverService
import com.ai.aishotclientkotlin.data.remote.WordsApi
import com.ai.aishotclientkotlin.data.repository.WordsRepository
import com.ai.aishotclientkotlin.data.repository.WordsRepositoryInterface
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
//import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory


@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun wordApiRepository(api: WordsApi) = WordsRepository(api) as WordsRepositoryInterface

    @Singleton
    @Provides
    fun injectBackendRetrofitApi() : WordsApi {

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Api.BASE_URL)
            .build()
            .create(WordsApi::class.java)
    }
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }
    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ImageLoader {
        return ImageLoader.Builder(context)

            // TODO : check okHttpClient is needed?
         //   .okHttpClient { okHttpClient }
            .build()
    }


    @Provides
    @Singleton
    fun provideRetrofit(okhHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okhHttpClient)
            .baseUrl(Api.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            ///TODO:  need add this ApiResponseCallAdapterFactory;
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTheDiscoverService(retrofit: Retrofit): TheDiscoverService {
        return retrofit.create(TheDiscoverService::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieService(retrofit: Retrofit): MovieService {
        return retrofit.create(MovieService::class.java)
    }

    @Provides
    @Singleton
    fun provideShopService(retrofit: Retrofit): ShopService {
        return retrofit.create(ShopService::class.java)
    }

    @Provides
    @Singleton
    fun providePeopleService(retrofit: Retrofit): PeopleService {
        return retrofit.create(PeopleService::class.java)
    }
}


