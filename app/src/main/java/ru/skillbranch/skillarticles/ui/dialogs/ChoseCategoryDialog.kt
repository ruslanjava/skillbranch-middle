package ru.skillbranch.skillarticles.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_category.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.local.entities.CategoryData
import ru.skillbranch.skillarticles.viewmodels.articles.ArticlesViewModel

class ChoseCategoryDialog : DialogFragment() {

    private val viewModel: ArticlesViewModel by activityViewModels()
    private val args: ChoseCategoryDialogArgs by navArgs()

    private lateinit var categoriesList: RecyclerView
    private val adapter = CategoryAdapter()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_categories_list, null)
        categoriesList = view.findViewById(R.id.categories_list)
        categoriesList.adapter = adapter

        // TODO save checked state and implement custom items
        adapter.items = args.categories

        val checked = Array(args.categories.size) {
            args.selectedCategories.contains(args.categories[it].categoryId)
        }
        adapter.selectedCategories = checked

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Choose category")
            .setView(view)
            .create()

        val applyButton = view.findViewById<Button>(R.id.apply_button)
        applyButton.setOnClickListener {
            val selectedCategories = adapter.items
                .filterIndexed { index, category -> adapter.selectedCategories[index] }
                .map { category -> category.categoryId }
            viewModel.applyCategories(selectedCategories)
            dialog.dismiss()
        }

        val resetButton = view.findViewById<Button>(R.id.reset_button)
        resetButton.setOnClickListener {
            viewModel.applyCategories(emptyList())
            dialog.dismiss()
        }

        return dialog
    }

    class CategoryAdapter: RecyclerView.Adapter<CategoryVH>() {

        var items: Array<CategoryData> = arrayOf()
        var selectedCategories: Array<Boolean> = arrayOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryVH {
            val inflater: LayoutInflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.item_category, parent, false)
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

        val cbSelect: CheckBox
        val ivCategory: ImageView
        val tvCategory: TextView
        val tvCount: TextView

        init {
            cbSelect = ch_select
            ivCategory = iv_category
            tvCategory = tv_category
            tvCount = tv_count
        }

        fun bind(item: CategoryData?, selected: Boolean, listener: (CategoryData, Boolean) -> Unit) {
            if (item != null) {
                containerView.visibility = View.VISIBLE

                cbSelect.isChecked = selected
                cbSelect.setOnCheckedChangeListener {
                        buttonView, isChecked -> listener.invoke(item, isChecked)
                }

                Glide.with(containerView.context)
                    .load(item.icon)
                    .into(ivCategory)
                tvCategory.text = item.title
                tvCount.text = item.articlesCount.toString()
            } else {
                containerView.visibility = View.INVISIBLE
            }
        }

    }

}