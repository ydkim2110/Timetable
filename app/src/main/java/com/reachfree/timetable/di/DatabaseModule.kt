package com.reachfree.timetable.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.reachfree.timetable.data.LocalDatabase
import com.reachfree.timetable.data.dao.SemesterDao
import com.reachfree.timetable.data.dao.SubjectDao
import com.reachfree.timetable.data.dao.TaskDao
import com.reachfree.timetable.data.dao.TestDao
import com.reachfree.timetable.data.repository.*
import com.reachfree.timetable.util.DispatcherProvider
import com.reachfree.timetable.util.LOCAL_DATABASE_NAME
import com.reachfree.timetable.util.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideSessionManager(
        @ApplicationContext context: Context
    ) = SessionManager(context)

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
    fun provideSemesterDao(database: LocalDatabase) = database.semesterDao()

    @Singleton
    @Provides
    fun provideSubjectDao(database: LocalDatabase) = database.subjectDao()

    @Singleton
    @Provides
    fun provideTestDao(database: LocalDatabase) = database.testDao()

    @Singleton
    @Provides
    fun provideTaskDao(database: LocalDatabase) = database.taskDao()

    @Singleton
    @Provides
    fun provideSemesterRepository(semesterDao: SemesterDao): SemesterRepository =
        SemesterRepositoryImpl(semesterDao)

    @Singleton
    @Provides
    fun provideSubjectRepository(subjectDao: SubjectDao): SubjectRepository =
        SubjectRepositoryImpl(subjectDao)

    @Singleton
    @Provides
    fun provideTestRepository(testDao: TestDao): TestRepository =
        TestRepositoryImpl(testDao)

    @Singleton
    @Provides
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository =
        TaskRepositoryImpl(taskDao)

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