package space.rodionov.financialsobriety.ui.transaction.edittransaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.databinding.ItemChooseCategoryBinding

class ChooseCategoryAdapter(
    private val listener: ChooseCategoryAdapter.OnItemClickListener
) : ListAdapter<Category, ChooseCategoryAdapter.ChooseCatViewHolder>(ChooseCatComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseCatViewHolder {
        val binding = ItemChooseCategoryBinding.inflate(
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

    inner class ChooseCatViewHolder(private val binding: ItemChooseCategoryBinding) :
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
            }
        }

        fun bind(category: Category) {
            binding.apply {
                tvCatname.text = category.catName
//                radioButton.isChecked = установить чекд если открыт в модели ЭдитТранскшнФрагмента уже выбранная категория есть
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(category: Category)
    }

    class ChooseCatComparator : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) =
            oldItem.catName == newItem.catName

        override fun areContentsTheSame(oldItem: Category, newItem: Category) =
            oldItem.catName == newItem.catName
//            oldItem == newItem
    }
}







