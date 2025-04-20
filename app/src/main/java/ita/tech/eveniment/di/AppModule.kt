package ita.tech.eveniment.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ita.tech.eveniment.data.ApiEveniment
import ita.tech.eveniment.interceptor.Authentication
import ita.tech.eveniment.util.Constants.Companion.AUTH_PASS
import ita.tech.eveniment.util.Constants.Companion.AUTH_USER
import ita.tech.eveniment.util.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            // Autenticacion basica para el Webservices
            .client( OkHttpClient.Builder().connectTimeout(100, TimeUnit.SECONDS).addInterceptor(Authentication(AUTH_USER, AUTH_PASS)).build() )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun providesApiEveniment( retrofit: Retrofit ): ApiEveniment{
        return retrofit.create(ApiEveniment::class.java)
    }

}