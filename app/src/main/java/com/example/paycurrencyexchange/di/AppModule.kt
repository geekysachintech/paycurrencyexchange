package com.example.paycurrencyexchange.di

import android.content.Context
import androidx.room.Room
import com.example.paycurrencyexchange.BuildConfig
import com.example.paycurrencyexchange.data.local.ExchangeRateDao
import com.example.paycurrencyexchange.data.local.ExchangeRateDataBase
import com.example.paycurrencyexchange.data.remote.ApiService
import com.example.paycurrencyexchange.data.repository.ExchangeRateRepository
import com.example.paycurrencyexchange.data.repository.ExchangeRateRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
            context,
            ExchangeRateDataBase::class.java,
            "exchange_rate_database"
        ).fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideDao(exchangeRateDataBase: ExchangeRateDataBase): ExchangeRateDao {
        return exchangeRateDataBase.exchangeRateDao()
    }

    @Provides
    fun provideExchangeRateRepository(apiService: ApiService, exchangeRateDao: ExchangeRateDao) : ExchangeRateRepository =  ExchangeRateRepositoryImpl(exchangeRateDao, apiService)

}
