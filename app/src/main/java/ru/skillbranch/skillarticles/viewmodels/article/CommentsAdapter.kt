package ru.skillbranch.skillarticles.viewmodels.article

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_comment.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.models.CommentItemData
import ru.skillbranch.skillarticles.extensions.format

class CommentsAdapter(private val listener: (CommentItemData) -> Unit):
        PagedListAdapter<CommentItemData, CommentVH>(CommentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentVH {
        val containerView = LayoutInflater.from(parent.context).inflate(
                R.layout.item_comment,
                parent,
                false
        )
        return CommentVH(containerView, listener)
    }

    override fun onBindViewHolder(holder: CommentVH, position: Int) {
        holder.bind(getItem(position))
    }

}

class CommentVH(override val containerView: View, val listener: (CommentItemData) -> Unit)
    : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(item: CommentItemData?) {
        // if item null show placeholder
        if (item != null) {
            itemView.setOnClickListener { listener(item) }
            tv_author_name.text = item.user.name

            Glide.with(containerView.context)
                    .load(item.user.avatar)
                    .into(iv_author)
            tv_date.text = item.date.format()
            tv_description.text = item.body
        } else {
            // if item null show placeholder
            tv_author_name.text = "Loading - nned placeholder this"
        }
    }
}

class CommentDiffCallback(): DiffUtil.ItemCallback<CommentItemData>() {

    override fun areItemsTheSame(oldItem: CommentItemData, newItem: CommentItemData): Boolean =
            oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: CommentItemData, newItem: CommentItemData): Boolean =
            oldItem == newItem

}