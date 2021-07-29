package space.rodionov.financialsobriety.ui.transaction.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.Transaction
import space.rodionov.financialsobriety.data.TransactionType
import space.rodionov.financialsobriety.databinding.ItemTransactionBinding

class RecTransChildAdapter(
    private val context: Context,
    private val onTransactionClick: (Transaction) -> Unit
) : ListAdapter<Transaction, RecTransChildAdapter.RecChildViewHolder>(RecTransComparator()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecChildViewHolder {
        val binding =
            ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecChildViewHolder(
            binding,
            onItemClick = {
                val transaction = getItem(it)
                if (transaction != null) {
                    onTransactionClick(transaction)
                }
            }
        )
    }

    override fun onBindViewHolder(holder: RecChildViewHolder, position: Int) {
        val curTransaction = getItem(position)
        holder.bind(curTransaction)
    }

    inner class RecChildViewHolder(
        private val binding: ItemTransactionBinding,
        private val onItemClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                // ADD LISTENERS
            }
        }

        fun bind(transaction: Transaction) {
            binding.apply {
                tvCategory.text = transaction.catName
                tvComment.text = transaction.comment
                tvSum.text = transaction.sum.toString()
                if (transaction.type == TransactionType.INCOME) {
                    tvSum.setTextColor(context.resources.getColor(R.color.green))
                    tvSum.text = "+ ${tvSum.text}"
                }
            }
        }
    }

    class RecTransComparator : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction) =
            oldItem == newItem
    }
}