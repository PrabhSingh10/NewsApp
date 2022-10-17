package com.androiddevs.mvvmnewsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.androiddevs.mvvmnewsapp.R
import com.androiddevs.mvvmnewsapp.adapter.NewsAdapter
import com.androiddevs.mvvmnewsapp.databinding.FragmentArticleBinding
import com.androiddevs.mvvmnewsapp.databinding.FragmentBreakingNewsBinding
import com.androiddevs.mvvmnewsapp.ui.activity.NewsActivity
import com.androiddevs.mvvmnewsapp.ui.viewModel.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private lateinit var newsViewModel: NewsViewModel
    private var articleBinding : FragmentArticleBinding? = null

    private val args : ArticleFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        articleBinding = FragmentArticleBinding.inflate(
            layoutInflater, container!!, false)

        return articleBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsViewModel = (activity as NewsActivity).newsViewModel

        val article = args.article

        articleBinding?.webView?.apply {
            this.webViewClient = WebViewClient()
            this.loadUrl(article.url!!)
        }

        articleBinding?.fab?.setOnClickListener {
            newsViewModel.upsert(article)
            Snackbar.make(it, "Article Saved Successfully", Snackbar.LENGTH_SHORT).show()
        }

    }

}