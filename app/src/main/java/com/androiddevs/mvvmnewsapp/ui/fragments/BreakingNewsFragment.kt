package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapter.NewsAdapter
import com.androiddevs.mvvmnewsapp.databinding.FragmentBreakingNewsBinding
import com.androiddevs.mvvmnewsapp.ui.activity.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.viewModel.NewsViewModel
import com.androiddevs.mvvmnewsapp.util.Constants.Companion.QUERY_PAGE_SIZE
import com.androiddevs.mvvmnewsapp.util.Resource

class BreakingNewsFragment : Fragment() {

    private lateinit var newsViewModel: NewsViewModel
    private var breakingNewsBinding : FragmentBreakingNewsBinding? = null
    private val TAG : String = "Breaking News Fragment"
    lateinit var newsAdapter : NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        breakingNewsBinding = FragmentBreakingNewsBinding.inflate(
            layoutInflater, container!!, false)

        return breakingNewsBinding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        newsAdapter.setItemClickListener {

            val bundle = Bundle()

            bundle.putParcelable("article", it)

            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )

        }

        newsViewModel = (activity as NewsActivity).newsViewModel

        //viewLifeCycleOwner : A LifecycleOwner that represents the Fragment's View lifecycle.
        // In most cases, this mirrors the lifecycle of the Fragment itself, but in cases of
        // detached Fragments, the lifecycle of the Fragment can be considerably longer than
        // the lifecycle of the View itself.
        newsViewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
                        val totalPages = (it.totalResults / QUERY_PAGE_SIZE) + 2
                            //First +1 is because there is an automatic round-up of number,
                            //since the number is an integer.
                            //Second +1 is for the empty page which is the last page of response.
                        isLastPage = newsViewModel.breakingNewsPage == totalPages
                        if(isLastPage){
                            breakingNewsBinding?.rvBreakingNews?.setPadding(
                                0,0,0,0
                            )
                            //It's used to remove the space at the bottom to make the view/page
                            //look better. Otherwise, it will leave a space at the bottom.
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
    }

    private fun hideProgressBar() {
        breakingNewsBinding?.paginationProgressBar?.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        breakingNewsBinding?.paginationProgressBar?.visibility = View.VISIBLE
        isLoading = true
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
                breakingNewsBinding?.rvBreakingNews?.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItem = layoutManager.itemCount
                //Total number of items that are bound to the recycler view at the moment
                //(including items in scrapped view and waiting view) not necessarily
                //equal to RecyclerView.getItemCount()

            val isNotLoadingAndNotAtLastPage = !isLastPage && !isLoading
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItem
                //The last item of the page not necessarily the last item of list
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItem >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotAtLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling

            if(shouldPaginate) {
                newsViewModel.gettingBreakingNews("in")
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

        breakingNewsBinding?.rvBreakingNews?.adapter = newsAdapter
        breakingNewsBinding?.rvBreakingNews?.layoutManager = LinearLayoutManager(activity)
        breakingNewsBinding?.rvBreakingNews?.addOnScrollListener(
            this@BreakingNewsFragment.scrollListener
        )
    }
}