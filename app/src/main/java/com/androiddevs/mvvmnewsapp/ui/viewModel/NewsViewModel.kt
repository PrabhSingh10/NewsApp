package com.androiddevs.mvvmnewsapp.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.models.NewsResponse
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

//ViewModel is a class that is responsible for preparing
//and managing the data for an Activity or a Fragment .
//ViewModel classes are used to store the data even if
//there are configuration changes like rotating screen.
class NewsViewModel(private val newsRepository: NewsRepository)
    : ViewModel() {

    val breakingNews = MutableLiveData<Resource<NewsResponse>>()
    var breakingNewsPage = 1
    var breakingNewsResponse : NewsResponse? = null

    val searchNews = MutableLiveData<Resource<NewsResponse>>()
    var searchNewsPage = 1
    var searchNewsResponse : NewsResponse? = null

    init {
        gettingBreakingNews("in")
    }

    fun gettingBreakingNews(countryCode : String) = viewModelScope.launch {
        breakingNews.postValue(Resource.Loading())  //Loading to signify that we still
        //don't have a response

        val result = newsRepository.getBreakingNews(countryCode, breakingNewsPage)

        //We get a 'Response' data type result and now we have to convert it into
        //Resource<NewsResponse> type using handlingBreakingNews()
        breakingNews.postValue(handlingBreakingNews(result))
    }

    private fun handlingBreakingNews(result : Response<NewsResponse>) : Resource<NewsResponse>{

        if(result.isSuccessful){//If the response was successful
            result.body()?.let {
                breakingNewsPage++
                if(breakingNewsResponse == null){
                    breakingNewsResponse = it
                }else {
                    val oldArticles = breakingNewsResponse?.articles
                        //oldArticles is not a copy of breakingNewsResponse?.articles, instead
                        //it is a reference to it
                    val newsArticle = it.articles
                    oldArticles?.addAll(newsArticle)
                }

                //calling Resource.Success(DATA)
                return Resource.Success(breakingNewsResponse ?: it)
                    //it's impossible that breakingNewsResponse is null here.
                    //Still the compiler complains because theoretically, it could be null.
                    //In case we had some multithreading and after the else block another
                    //thread would run at the same time and change breakingNewsResponse to null
                    //again, it could potentially be null, so we must do that check here.
                    //But since we don't have another thread accessing newsResponse here,
                    //this can't happen for us
            }
        }

        return Resource.Failure(result.message())//If the response failed
    }

     fun searchingNews(searchQuery : String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())  //Loading to signify that we still
        //don't have a response

        val result = newsRepository.searchNews(searchQuery, searchNewsPage)

        //We get a 'Response' data type result and now we have tp convert it into
        //Resource<NewsResponse> type using handlingBreakingNews()
        searchNews.postValue(handlingSearchingNews(result))
    }

    private fun handlingSearchingNews(result : Response<NewsResponse>) : Resource<NewsResponse>{

        if(result.isSuccessful){//If the response was successful
            result.body()?.let {
                searchNewsPage++
                if(searchNewsResponse == null){
                    searchNewsResponse = it
                }else {
                    val oldArticles = searchNewsResponse?.articles
                    //oldArticles is not a copy of searchNewsResponse?.articles, instead
                    //it is a reference to it
                    val newsArticle = it.articles
                    oldArticles?.addAll(newsArticle)
                }

                return Resource.Success(searchNewsResponse ?: it)
                //calling Resource.Success(DATA)
            }
        }

        return Resource.Failure(result.message())//If the response failed
    }

    fun upsert(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.delete(article)
    }

    fun getSavedNews() = newsRepository.getAllArticles()

}