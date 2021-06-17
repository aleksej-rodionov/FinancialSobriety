package space.rodionov.financialsobriety.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import space.rodionov.financialsobriety.data.FinDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFinDatabase(
        app: Application
    ) = Room.databaseBuilder(app, FinDatabase::class.java, "fin_database")
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideFinDao(db: FinDatabase) = db.finDao()



}














