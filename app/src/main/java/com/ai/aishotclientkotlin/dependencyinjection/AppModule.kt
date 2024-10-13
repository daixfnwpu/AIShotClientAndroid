package com.ai.aishotclientkotlin.dependencyinjection


import android.content.Context
import android.util.Log
import coil3.ImageLoader
import com.ai.aishotclientkotlin.data.remote.Api
import com.ai.aishotclientkotlin.data.remote.DeviceProfileService
import com.ai.aishotclientkotlin.data.remote.MovieService
import com.ai.aishotclientkotlin.data.remote.ProductService
import com.ai.aishotclientkotlin.data.remote.ReviewService
import com.ai.aishotclientkotlin.data.remote.ShotConfigService
import com.ai.aishotclientkotlin.data.remote.TheDiscoverService
import com.ai.aishotclientkotlin.data.remote.UploadService
import com.ai.aishotclientkotlin.data.remote.UserService
import com.ai.aishotclientkotlin.data.repository.UserRepository
import com.ai.aishotclientkotlin.data.repository.UserRepositoryInterface
import com.ai.aishotclientkotlin.util.SpManager
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
//import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory


@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun userApiRepository(api: UserService) = UserRepository(api) as UserRepositoryInterface

    @Singleton
    @Provides
    fun injectBackendRetrofitApi() : UserService {

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Api.BASE_URL)
            .build()
            .create(UserService::class.java)
    }
    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            //TODO: your_token_here is neened
            .addInterceptor(AuthInterceptor(context))
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
    fun provideProductService(retrofit: Retrofit): ProductService {
        return retrofit.create(ProductService::class.java)
    }

    @Provides
    @Singleton
    fun provideShotConfigService(retrofit: Retrofit): ShotConfigService {
        return retrofit.create(ShotConfigService::class.java)
    }


    @Provides
    @Singleton
    fun provideReviewService(retrofit: Retrofit): ReviewService {
        return retrofit.create(ReviewService::class.java)
    }

    @Provides
    @Singleton
    fun provideDeviceProfileService(retrofit: Retrofit): DeviceProfileService {
        return retrofit.create(DeviceProfileService::class.java)
    }

    @Provides
    @Singleton
    fun provideUploadService(retrofit: Retrofit): UploadService {
        return retrofit.create(UploadService::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieService(retrofit: Retrofit): MovieService {
        return retrofit.create(MovieService::class.java)
    }


}
class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return try {


            val token =
                SpManager(context).getSharedPreference(SpManager.Sp.JWT_TOKEN, "Null").toString()
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")  // 将 JWT Token 添加到请求头
                .build()
            chain.proceed(request)
        }catch (e: Exception)
        {
         Log.e("Exception","${e.stackTraceToString()}")
            chain.proceed(chain.request().newBuilder().build())
                .newBuilder()
                .code(500) // 返回 500 状态码表示服务器错误
                .message("Internal Server Error")
                .build()
        }

     //   return TODO("Provide the return value")
    }
}


