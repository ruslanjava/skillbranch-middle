package ru.skillbranch.skillarticles.ui.dialogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_category_dialog.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.local.entities.CategoryData

class CategoryAdapter : RecyclerView.Adapter<CategoryVH>() {

    var items: Array<CategoryData> = arrayOf()
    var selectedCategories: Array<Boolean> = arrayOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVH {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_category_dialog, parent, false)
        return CategoryVH(view)
    }

    override fun onBindViewHolder(holder: CategoryVH, position: Int) {
        val category = getItem(position)
        val selected = selectedCategories[position]
        holder.bind(category, selected) { _, selected ->
            selectedCategories[position] = selected
        }
    }

    private fun getItem(position: Int): CategoryData {
        return items[position]
    }

    override fun getItemCount(): Int {
        return items.size
    }

}

class CategoryVH(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    val cbSelect: CheckBox = ch_select
    val ivIcon: ImageView = iv_icon
    private val tvCategory: TextView = tv_category
    val tvCount: TextView = tv_count

    fun bind(
        item: CategoryData?,
        selected: Boolean,
        listener: (CategoryData, Boolean) -> Unit
    ) {
        itemView.setOnClickListener(null)

        if (item != null) {
            containerView.visibility = View.VISIBLE

            cbSelect.isChecked = selected
            cbSelect.setOnCheckedChangeListener { buttonView, isChecked ->
                listener.invoke(item, isChecked)
            }

            Glide.with(containerView.context)
                .load(item.icon)
                .into(ivIcon)
            tvCategory.text = item.title
            tvCount.text = item.articlesCount.toString()

            itemView.setOnClickListener {
                cbSelect.isChecked = !cbSelect.isChecked
            }
        } else {
            containerView.visibility = View.INVISIBLE
        }
    }

}
