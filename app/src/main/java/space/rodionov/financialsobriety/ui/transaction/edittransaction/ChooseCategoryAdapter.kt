package space.rodionov.financialsobriety.ui.transaction.edittransaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.databinding.ItemDialogRecyclerBinding

class ChooseCategoryAdapter(
    private val listener: OnItemClickListener,
    private val curCatName: String?
) : ListAdapter<Category, ChooseCategoryAdapter.ChooseCatViewHolder>(ChooseCatComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseCatViewHolder {
        val binding = ItemDialogRecyclerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChooseCatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChooseCatViewHolder, position: Int) {
        val curItem = getItem(position)
        holder.bind(curItem)
    }

    inner class ChooseCatViewHolder(private val binding: ItemDialogRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val category = getItem(position)
                        listener.onItemClick(category)
                    }
                }
                radioButton.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val category = getItem(position)
                        listener.onItemClick(category)
                    }
                }
            }
        }

        fun bind(category: Category) {
            binding.apply {
                tvCatname.text = category.catName
                radioButton.isChecked = category.catName == curCatName
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(category: Category)
    }

    class ChooseCatComparator : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Category, newItem: Category) =
            oldItem.catName == newItem.catName
    }
}







