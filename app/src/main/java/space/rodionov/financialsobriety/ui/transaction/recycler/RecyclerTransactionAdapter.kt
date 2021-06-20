package space.rodionov.financialsobriety.ui.transaction.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import space.rodionov.financialsobriety.data.Spend
import space.rodionov.financialsobriety.databinding.FragmentTransactionsRecyclerBinding
import space.rodionov.financialsobriety.databinding.ItemTransactionBinding

class RecyclerTransactionAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<Spend, RecyclerTransactionAdapter.RecTransViewHolder>(RecTransComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecTransViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecTransViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecTransViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class RecTransViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val spend = getItem(position)
                        listener.onItemClick(spend)
                    }
                }
            }
        }

        fun bind(spend: Spend) {
            binding.apply {
                tvCategory.text = spend.categoryName
                tvComment.text = spend.comment
                tvSum.text = spend.sum.toString()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(spend: Spend)
    }

    class RecTransComparator : DiffUtil.ItemCallback<Spend>() {
        override fun areItemsTheSame(oldItem: Spend, newItem: Spend) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Spend, newItem: Spend) = oldItem == newItem
    }
}