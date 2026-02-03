package ita.tech.eveniment.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ita.tech.eveniment.data.ApiEveniment
import ita.tech.eveniment.interceptor.Authentication
import ita.tech.eveniment.room.CalendarioAlarmaDatabaseDao
import ita.tech.eveniment.room.EvenimentDatabase
import ita.tech.eveniment.room.InformacionPantallaDatabaseDao
import ita.tech.eveniment.room.TextoAlarmaDatabaseDao
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

    // Configuracion Retrofit
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

    // Configuracion ROOM
    @Singleton
    @Provides
    fun providesEvenimentDatabase(@ApplicationContext context: Context): EvenimentDatabase{
        return Room.databaseBuilder(
            context,
            EvenimentDatabase::class.java, "eveniment_db",
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesInformacionPantallaDao( evenimentDatabase: EvenimentDatabase ): InformacionPantallaDatabaseDao{
        return evenimentDatabase.informacionPantallaDao()
    }

    @Singleton
    @Provides
    fun providesCalendarioAlarmaDao( evenimentDatabase: EvenimentDatabase ): CalendarioAlarmaDatabaseDao{
        return evenimentDatabase.calendarioAlarmaDao()
    }

    @Singleton
    @Provides
    fun providesTextoAlarmaDao( evenimentDatabase: EvenimentDatabase ): TextoAlarmaDatabaseDao {
        return evenimentDatabase.textoAlarmaDao()
    }

}