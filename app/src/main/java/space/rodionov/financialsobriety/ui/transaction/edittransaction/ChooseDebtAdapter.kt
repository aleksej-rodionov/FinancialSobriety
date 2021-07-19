package space.rodionov.financialsobriety.ui.transaction.edittransaction

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import space.rodionov.financialsobriety.data.Debt
import space.rodionov.financialsobriety.databinding.ItemDialogRecyclerBinding

class ChooseDebtAdapter(
    private val listener: OnDebtItemClickListener,
    private val curDebtName: String?
) : ListAdapter<Debt, ChooseDebtAdapter.ChooseDebtViewHolder>(ChooseDebtComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseDebtViewHolder {
        val binding = ItemDialogRecyclerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChooseDebtViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChooseDebtViewHolder, position: Int) {
        val curItem = getItem(position)
        holder.bind(curItem)
    }

    inner class ChooseDebtViewHolder(private val binding: ItemDialogRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val debt = getItem(position)
                        if (debt.debtName != curDebtName) {
                            listener.onDebtChosen(debt)
                        } else {
                            listener.onDebtCancelled()
                        }
                    }
                }
                radioButton.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val debt = getItem(position)
                        if (debt.debtName != curDebtName) {
                            listener.onDebtChosen(debt)
                        } else {
                            listener.onDebtCancelled()
                        }
                    }
                }
            }
        }

        fun bind(debt: Debt) {
            binding.apply {
                tvCatname.text = debt.debtName
                radioButton.isChecked = debt.debtName == curDebtName
            }
        }
    }

    interface OnDebtItemClickListener {
        fun onDebtChosen(debt: Debt)
        fun onDebtCancelled()
    }

    class ChooseDebtComparator : DiffUtil.ItemCallback<Debt>() {
        override fun areItemsTheSame(oldItem: Debt, newItem: Debt) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: Debt, newItem: Debt) =
            oldItem.debtName == newItem.debtName
    }
}