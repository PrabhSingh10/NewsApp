package com.androiddevs.mvvmnewsapp.ui.fragments

import android.content.ClipData.Item
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapter.NewsAdapter
import com.androiddevs.mvvmnewsapp.databinding.FragmentSavedNewsBinding
import com.androiddevs.mvvmnewsapp.db.ArticlesDatabase
import com.androiddevs.mvvmnewsapp.models.Article
import com.androiddevs.mvvmnewsapp.ui.activity.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.viewModel.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class SavedNewsFragment : Fragment() {

    lateinit var newsViewModel: NewsViewModel
    var savedNewsBinding : FragmentSavedNewsBinding? = null
    lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        savedNewsBinding = FragmentSavedNewsBinding.inflate(
            inflater, container!!, false)

        return savedNewsBinding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsViewModel = (activity as NewsActivity).newsViewModel
        setUpRecyclerView()

        newsViewModel.getSavedNews().observe(viewLifecycleOwner, Observer {
            newsAdapter.differ.submitList(it)
        })

        newsAdapter.onClick(object : NewsAdapter.ClickListener{
            override fun setItemClickListener(article: Article) {
                val bundle = Bundle()

                bundle.putParcelable("article", article)

                findNavController().navigate(
                    R.id.action_breakingNewsFragment_to_articleFragment,
                    bundle
                )
            }

        })

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                newsViewModel.deleteArticle(article)

                Snackbar.make(
                    view, "Article Successfully Deleted", Snackbar.LENGTH_SHORT
                ).apply {
                    this.setAction("Undo"){
                        newsViewModel.upsert(article)
                    }
                    this.show()
                }
            }

        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(
            savedNewsBinding?.rvSavedNews
        )

    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()

        savedNewsBinding?.rvSavedNews?.adapter = newsAdapter
        savedNewsBinding?.rvSavedNews?.layoutManager = LinearLayoutManager(activity)
    }
}