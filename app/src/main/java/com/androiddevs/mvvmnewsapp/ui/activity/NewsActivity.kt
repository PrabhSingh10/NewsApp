package com.androiddevs.mvvmnewsapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.androiddevs.mvvmnewsapp.databinding.ActivityNewsBinding
import com.androiddevs.mvvmnewsapp.db.ArticlesDatabase
import com.androiddevs.mvvmnewsapp.repository.NewsRepository
import com.androiddevs.mvvmnewsapp.ui.viewModel.NewsViewModel
import com.androiddevs.mvvmnewsapp.ui.viewModel.NewsViewModelProviderFactory

class NewsActivity : AppCompatActivity() {

    private var newsBinding : ActivityNewsBinding? = null
    lateinit var newsViewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newsBinding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(newsBinding?.root)

        //val database = (application as NewsApplication).db

        //Repository serves as a mediator between the ViewModel and the sources of data.
        //First the ViewModel asks the Repository for data. The Repository checks what’s in
        //the database and returns the persisted data to the ViewModel for further visualization
        //in the View. While the Repository is getting data from the database, it also sends a
        //request to the remote service in parallel (asynchronously). When the response is back,
        //the Repository updates the database with the newest fetched data. Then this data is
        //consumed from the ViewModel which transforms it in an appropriate way and returns
        //it to the View.
        val newsRepository = NewsRepository(ArticlesDatabase.getInstance(this))

        //A ViewModelProvider.Factory is needed only when the viewModel has arg constructors
        val newsViewModelProviderFactory = NewsViewModelProviderFactory(newsRepository)

        //If there are no arg constructors in the viewModel, we can remove the factory here
        //and just instantiate the viewModel without it
        newsViewModel = ViewModelProvider(this@NewsActivity,
            newsViewModelProviderFactory)[NewsViewModel::class.java]

        //When creating the NavHostFragment using FragmentContainerView,
        //attempting to retrieve the NavController in onCreate() of an Activity
        //via Navigation.findNavController(Activity, @IdRes int) will fail.
        //You should retrieve the NavController directly from the NavHostFragment instead.
        newsBinding?.bottomNavigationView?.setupWithNavController(
            newsBinding?.newsNavHostFragment?.getFragment<NavHostFragment>()!!.navController)

        //newsBinding?.newsNavHostFragment?.getFragment<NavHostFragment>()
        //returns the NavHostFragment
    }
}
