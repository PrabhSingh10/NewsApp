package com.androiddevs.mvvmnewsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.databinding.ItemArticlePreviewBinding
import com.androiddevs.mvvmnewsapp.models.Article
import com.bumptech.glide.Glide

//We are not getting a list as a constructor arg because we will use the differ.submitList
//to obtain the list as it will send a new list everytime there is an update in it
class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(itemArticlePreviewBinding: ItemArticlePreviewBinding)  :
    RecyclerView.ViewHolder(itemArticlePreviewBinding.root){
        var ivArticleImage = itemArticlePreviewBinding.ivArticleImage
        var tvSource = itemArticlePreviewBinding.tvSource
        var tvTitle = itemArticlePreviewBinding.tvTitle
        var tvDescription = itemArticlePreviewBinding.tvDescription
        var tvPublishedAt = itemArticlePreviewBinding.tvPublishedAt
    }

    //DiffUtil is used when there is a change in the list.
    //It only changes the update item in the list unlike
    //NotifyDataSetChanged() which changes the whole list

    //DiffUtil.ItemCallback is the native class responsible for calculating
    //the difference between the two lists. Since the OS doesn't know which fields to edit,
    //it’s the app’s responsibility to override areItemsTheSame and areContentsTheSame
    //to provide this information.
    private val differCallBack = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            //Use the items which are unique to each article, i.e. url here
            //Usually we use id but since we are not getting id from the api
            //we use url which is going to be the same for the same news
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

    //AsyncListDiffer working :
    //Activates the ItemCallBack which compares the lists and returns the position/s
    //for updating
    ///Then the position/s are updated in the adapter
    //And all of this is done on the background thread
    val differ = AsyncListDiffer(this@NewsAdapter, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = ItemArticlePreviewBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        holder.tvSource.text = article.source.name
        holder.tvTitle.text = article.title
        holder.tvDescription.text = article.description
        holder.tvPublishedAt.text = article.publishedAt

        holder.itemView.apply {
            Glide.with(this)
                .load(article.urlToImage)
                .into(holder.ivArticleImage)

            setOnClickListener {
                itemClickListener?.let {
                    it(article)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var itemClickListener : ((Article) -> Unit)? = null
        //It will get an Article Item but return nothing

    fun setItemClickListener(Listener : ((Article) -> Unit)) {
        itemClickListener = Listener
    }
}