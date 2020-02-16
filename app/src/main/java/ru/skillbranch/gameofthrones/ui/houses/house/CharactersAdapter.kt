package ru.skillbranch.gameofthrones.ui.houses.house

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_character.*
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.HouseType

class CharactersAdapter(private val listener: (CharacterItem) -> Unit) :
        RecyclerView.Adapter<CharactersAdapter.CharacterVH>() {
    var items: List<CharacterItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterVH {
        val containerView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_character,
            parent,
            false
        )
        return CharacterVH(containerView)
    }

    override fun onBindViewHolder(holder: CharacterVH, position: Int) =
        holder.bind(items[position], listener)

    override fun getItemCount(): Int = items.size

    fun updateItems(data: List<CharacterItem>) {
        val diffCallback = object: DiffUtil.Callback() {
            override fun areItemsTheSame(oldPos: Int, newPos: Int) =
                items[oldPos].id == data[newPos].id

            override fun areContentsTheSame(oldPos: Int, newPos: Int) =
                items[oldPos] == data[newPos]

            override fun getOldListSize(): Int = items.size

            override fun getNewListSize(): Int = data.size

        }

        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        items = data
        diffResult.dispatchUpdatesTo(this)
    }

    class CharacterVH(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {

        fun bind(item: CharacterItem, listener: (CharacterItem) -> Unit) {
            item.name.also {
                tv_name.text = if (it.isBlank()) "Information is unknown" else it
            }

            item.titles
                .plus(item.aliases)
                .filter { it.isNotBlank() }
                .also {
                    tv_aliases.text = if (it.isEmpty()) "Information is unknown"
                    else it.joinToString("  ●  ")
                }

            val houseType = HouseType.fromString(item.house)
            iv_avatar.setImageResource(houseType.icon)

            itemView.setOnClickListener { listener(item) }
        }

    }

}

