package com.example.if570_lab_uts_hansphilemonlimanza_70710

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PostAdapter(
    private val posts: List<Post>,
    private val onLikeClick: (Post) -> Unit,
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImageView: ImageView = itemView.findViewById(R.id.postImage)
        val postTextView: TextView = itemView.findViewById(R.id.postText)
        val likeButton: ImageButton = itemView.findViewById(R.id.likeButton) // Ensure this matches your layout
        val likesCountView: TextView = itemView.findViewById(R.id.likesCountView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.postTextView.text = post.content
        holder.likesCountView.text = "${post.likes} likes"

        // Load image using Glide or Picasso
        Glide.with(holder.itemView.context)
            .load(post.imageUrl)
            .into(holder.postImageView)

        // Set like button click listener
        holder.likeButton.setOnClickListener {
            onLikeClick(post)
        }
    }

    override fun getItemCount(): Int = posts.size
}
