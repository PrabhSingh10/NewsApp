package com.androiddevs.mvvmnewsapp.repository

import com.androiddevs.mvvmnewsapp.api.RetrofitInstance
import com.androiddevs.mvvmnewsapp.db.ArticlesDatabase
import com.androiddevs.mvvmnewsapp.models.Article

//Repository is a class which purpose is to provide a clean API for accessing data.
//What that means is that the Repository can gather data from different data sources(different
//REST APIs, cache, local database storage) and it provides this data to the rest of the app.
//The other components don’t know where the data comes from, they just consume it. It also
// serves as a single source of truth. Its role is to keep the local database up to date
//with the newest fetched data from remote service so that the application can still provide
//its functionalities with bad Internet connection or no connection at all.
class NewsRepository(
    private val db : ArticlesDatabase
) {

    suspend fun getBreakingNews(countryCode : String, page : Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, page)

    suspend fun searchNews(searchQuery : String, page : Int) =
        RetrofitInstance.api.searchNews(searchQuery, page)

    suspend fun upsert(article : Article) =
        db.getArticlesDao().upsert(article)

    suspend fun delete(article: Article) =
        db.getArticlesDao().delete(article)

    fun getAllArticles() =
        db.getArticlesDao().getAllArticles()

}