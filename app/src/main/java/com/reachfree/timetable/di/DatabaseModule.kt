package com.reachfree.timetable.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.reachfree.timetable.data.LocalDatabase
import com.reachfree.timetable.data.dao.TimetableDao
import com.reachfree.timetable.data.repository.TimetableRepository
import com.reachfree.timetable.data.repository.TimetableRepositoryImpl
import com.reachfree.timetable.util.DispatcherProvider
import com.reachfree.timetable.util.LOCAL_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideLocalDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        LocalDatabase::class.java,
        LOCAL_DATABASE_NAME
    ).addCallback(defaultData)
        .build()

    private val defaultData = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

        }
    }

    @Singleton
    @Provides
    fun provideTimetableDao(database: LocalDatabase) = database.timetableDao()

    @Singleton
    @Provides
    fun provideTimetableRepository(timetableDao: TimetableDao): TimetableRepository =
        TimetableRepositoryImpl(timetableDao)

    @Singleton
    @Provides
    fun provideDispatchers(): DispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }

}