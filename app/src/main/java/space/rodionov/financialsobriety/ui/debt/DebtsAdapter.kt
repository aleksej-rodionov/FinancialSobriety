package space.rodionov.financialsobriety.ui.debt

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.data.Debt
import space.rodionov.financialsobriety.databinding.ItemCategoryBinding
import space.rodionov.financialsobriety.ui.categories.CategoriesAdapter

class DebtsAdapter(
    private val listener: OnDebtClickListener,
) : ListAdapter<Debt, DebtsAdapter.DebtsViewHolder>(DebtsComparator()){

    inner class DebtsViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val debt = getItem(position)
                        listener.onDebtClick(debt)
                    }
                }
            }
        }

        fun bind(debt: Debt) {
            binding.apply {
                tvCategory.text = debt.debtName
                tvType.text = "Debt"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtsViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DebtsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DebtsViewHolder, position: Int) {
        val curDebt = getItem(position)
        holder.bind(curDebt)
    }

    interface OnDebtClickListener {
        fun onDebtClick(debt: Debt)
    }

    class DebtsComparator : DiffUtil.ItemCallback<Debt>() {
        override fun areItemsTheSame(oldItem: Debt, newItem: Debt) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Debt, newItem: Debt) =
            oldItem.debtName == newItem.debtName
    }
}