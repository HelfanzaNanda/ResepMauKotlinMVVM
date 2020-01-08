package com.alfara.kotlinmvp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.alfara.kotlinmvp.R
import com.alfara.kotlinmvp.RecipeActivity
import com.alfara.kotlinmvp.models.Post
import kotlinx.android.synthetic.main.item.view.*

class PostAdapter (private var posts : MutableList<Post>, private var context : Context) : RecyclerView.Adapter<PostAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false))
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(posts[position], context)

    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind (post: Post, context: Context){
            itemView.tv_title.text = post.title
            itemView.tv_content.text = post.content
            itemView.setOnClickListener {
                context.startActivity(Intent(context, RecipeActivity::class.java).apply {
                    putExtra("RECIPE", post)
                    putExtra("IS_INSERT", false)
                })

            }
        }
    }

    fun changelist(ps : List<Post>){
        posts.clear()
        posts.addAll(ps)
        notifyDataSetChanged()
    }
}