package com.example.schedulog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.schedulog.databinding.PostItemBinding
import timber.log.Timber

/* This class is responsible for holding onto an instance of the view and
 * binding the PostItem(post_item.xml) to the RecyclerView(fragment_feed.xml). */
class PostViewHolder (
    private val binding: PostItemBinding
) : RecyclerView.ViewHolder(binding.root){
    fun bind(postItem: PostItem){
        binding.postDescription.text = postItem.description
        Glide.with(binding.root).load(postItem.image_url).into(binding.postImage)    // not sure if binding.root is correct
    }
}


/* This class is responsible for providing the PostViewHolder instances with a PostItem.
 * Also responsible for the communicating between RecyclerView and data. */
class PostListAdapter(
    private val postItems: List<PostItem>
) : RecyclerView.Adapter<PostViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostItemBinding.inflate(inflater, parent, false)
        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postItems.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        Timber.tag("PostListAdapter").i(postItems.toString())
        val post = postItems[position]
        holder.bind(post)
    }
}


