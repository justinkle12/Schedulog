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

/*class PostAdapter(private val context: Context, private val postList: ArrayList<Post>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.postImage)
        val postDescription: TextView = itemView.findViewById(R.id.postDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postList[position]

        // Load the image using Glide
        Glide.with(context).load(post.imageUrl).into(holder.postImage)

        holder.postDescription.text = post.description
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}*/

class PostViewHolder (
    private val binding: PostItemBinding
) : RecyclerView.ViewHolder(binding.root){
    fun bind(postItem: PostItem){
        Glide.with(binding.root).load(postItem.imageUrl).into(binding.postImage)    // not sure if binding.root is correct
    }
}

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
        val post = postItems[position]
        holder.bind(post)
    }
}


