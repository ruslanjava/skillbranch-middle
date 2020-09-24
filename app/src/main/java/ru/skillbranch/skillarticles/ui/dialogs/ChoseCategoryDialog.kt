package ru.skillbranch.skillarticles.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.skillarticles.R

class ChoseCategoryDialog : DialogFragment() {

    private val args: ChoseCategoryDialogArgs by navArgs()

    private lateinit var categoriesList: RecyclerView
    private val adapter = CategoryAdapter()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_categories_list, null)
        categoriesList = view.findViewById(R.id.categories_list)
        categoriesList.adapter = adapter

        adapter.items = args.categories

        val selectedCategories: Array<String>
        if (savedInstanceState == null) {
            selectedCategories = args.selectedCategories
        } else {
            selectedCategories = savedInstanceState.getStringArray(SELECTED_CATEGORIES)!!
        }
        val checked = Array(args.categories.size) {
            selectedCategories.contains(args.categories[it].categoryId)
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
            setFragmentResult(CHOSE_CATEGORY_KEY, bundleOf(SELECTED_CATEGORIES to selectedCategories.toList()))
            dialog.dismiss()
        }

        val resetButton = view.findViewById<Button>(R.id.reset_button)
        resetButton.setOnClickListener {
            setFragmentResult(CHOSE_CATEGORY_KEY, bundleOf(SELECTED_CATEGORIES to emptyList<String>()))
            dialog.dismiss()
        }

        return dialog
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val selectedCategories = adapter.items
            .filterIndexed { index, category -> adapter.selectedCategories[index] }
            .map { category -> category.categoryId }
            .toTypedArray()

        outState.putStringArray(SELECTED_CATEGORIES, selectedCategories)
    }

    companion object {

        const val SELECTED_CATEGORIES = "selected_categories"
        const val CHOSE_CATEGORY_KEY = "CHOSE_CATEGORY_KEY"

    }

}