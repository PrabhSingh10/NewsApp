package com.androiddevs.mvvmnewsapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.androiddevs.mvvmnewsapp.models.Article

@Dao
interface ArticlesDao {

    //Whenever the id of the article we are inserting is the same as one of the already existing
    //article, we get a conflict
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun upsert(article : Article) : Long //up-sert = Update - Insert articles in the db

    @Query("SELECT * From Articles")
    fun getAllArticles() : LiveData<List<Article>>

    @Delete
    suspend fun delete(article: Article)

}