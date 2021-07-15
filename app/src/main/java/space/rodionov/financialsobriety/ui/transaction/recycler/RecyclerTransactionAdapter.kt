package space.rodionov.financialsobriety.ui.transaction.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import space.rodionov.financialsobriety.data.Transaction
import space.rodionov.financialsobriety.databinding.ItemTransactionBinding

class RecyclerTransactionAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<Transaction, RecyclerTransactionAdapter.RecTransViewHolder>(RecTransComparator()) {

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

        fun bind(transaction: Transaction) {
            binding.apply {
                tvCategory.text = transaction.categoryName
                tvComment.text = transaction.comment
                tvSum.text = transaction.sum.toString()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(transaction: Transaction)
    }

    class RecTransComparator : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction) = oldItem == newItem
    }
}