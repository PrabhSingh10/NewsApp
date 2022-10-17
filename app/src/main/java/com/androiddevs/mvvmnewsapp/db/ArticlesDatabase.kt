package com.androiddevs.mvvmnewsapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androiddevs.mvvmnewsapp.models.Article

@Database(
    entities = [Article::class], version = 1
)
@TypeConverters(Converter::class)   //Adding typeConverters in database means
// entity, dao and db can use the type converter
abstract class ArticlesDatabase : RoomDatabase(){

    abstract fun getArticlesDao() : ArticlesDao

    companion object {

        @Volatile
        private var INSTANCE : ArticlesDatabase? = null

        //fun xyz() {return abc} is the same as
        //fun xyz() = abc
        fun getInstance(context : Context) =
            synchronized(this) {
                //Elvis operator is very common in many programming languages.
                //This is a binary expression that returns the first operand when
                //the expression value is True and it returns the second operand
                //when the expression value is False.
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ArticlesDatabase::class.java,
                    "Articles_Database"
                ).build()
            }
    }
}