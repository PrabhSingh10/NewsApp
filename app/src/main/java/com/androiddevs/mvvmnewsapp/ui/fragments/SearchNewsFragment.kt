package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapter.NewsAdapter
import com.androiddevs.mvvmnewsapp.databinding.FragmentSearchNewsBinding
import com.androiddevs.mvvmnewsapp.ui.activity.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.viewModel.NewsViewModel
import com.androiddevs.mvvmnewsapp.util.Constants
import com.androiddevs.mvvmnewsapp.util.Constants.Companion.SEARCH_TIME_DELAY
import com.androiddevs.mvvmnewsapp.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    var searchNewsBinding : FragmentSearchNewsBinding? = null
    private lateinit var newsViewModel: NewsViewModel
    private val TAG : String = "Search News Fragment"
    lateinit var newsAdapter : NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        searchNewsBinding = FragmentSearchNewsBinding.inflate(
            inflater, container!!, false)

        return searchNewsBinding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsViewModel = (activity as NewsActivity).newsViewModel

        setUpRecyclerView()

        var job : Job? = null
            //Coroutine job is created with launch coroutine builder.
            //It runs a specified block of code and completes on completion of this block.
        searchNewsBinding?.etSearch?.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_TIME_DELAY)
                    //Delays coroutine for a given time without blocking a
                    //thread and resumes it after a specified time.
                editable?.let {
                    if(it.toString().isNotEmpty()){
                        newsViewModel.searchingNews(it.toString())
                    }
                }
            }
        }

        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
                        val totalPages = (it.totalResults / Constants.QUERY_PAGE_SIZE) + 2
                        //First +1 is because there is an automatic round-up of number,
                        //since the number is an integer.
                        //Second +1 is for the empty page which is the last page of response.
                        isLastPage = newsViewModel.breakingNewsPage == totalPages
                        if(isLastPage){
                            searchNewsBinding?.rvSearchNews?.setPadding(
                                0,0,0,0
                            )
                        }
                    }
                }

                is Resource.Failure -> {
                    hideProgressBar()
                    Log.e(TAG, "There was an error : ${response.message}")
                }

                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        newsAdapter.setItemClickListener {
            val bundle = Bundle()
            bundle.putParcelable("article", it)

            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

    }

    private fun hideProgressBar() {
        searchNewsBinding?.paginationProgressBar?.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        searchNewsBinding?.paginationProgressBar?.visibility = View.VISIBLE
    }

    var isLastPage = false
    var isLoading = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {

        //Callback method to be invoked when the RecyclerView has been scrolled.
        //This will be called after the scroll has completed.
        //This callback will also be called if visible item range changes after
        //a layout calculation.
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager =
                searchNewsBinding?.rvSearchNews?.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItem = layoutManager.itemCount
            //Total number of items that are bound to the recycler view at the moment
            //not necessarily equal to RecyclerView.getItemCount

            val isNotLoadingAndNotAtLastPage = !isLastPage && !isLoading
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItem
            //The last item of the page not necessarily the last item of list
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItem >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotAtLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling

            if(shouldPaginate) {
                newsViewModel.searchingNews(searchNewsBinding?.etSearch!!.text.toString())
                isScrolling = false
            }

        }

        //onScrollStateChanged() is invoked when recyclerview’s state is changed
        //and it has two parameters : recyclerView, who's scroll state has changed,
        //and newState, the updated scroll state.
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            //AbsListView.OnScrollListener : Interface definition for a callback
            //to be invoked when the list or grid has been scrolled.
            //SCROLL_STATE_TOUCH_SCROLL : The user is scrolling using touch,
            //and their finger is still on the screen
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }
    }

    private fun setUpRecyclerView(){
        newsAdapter = NewsAdapter()

        searchNewsBinding?.rvSearchNews?.adapter = newsAdapter
        searchNewsBinding?.rvSearchNews?.layoutManager = LinearLayoutManager(activity)
        searchNewsBinding?.rvSearchNews?.addOnScrollListener(
            this@SearchNewsFragment.scrollListener
        )
    }
}