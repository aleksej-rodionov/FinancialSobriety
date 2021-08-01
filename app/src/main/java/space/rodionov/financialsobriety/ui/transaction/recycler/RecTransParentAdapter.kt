package space.rodionov.financialsobriety.ui.transaction.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.CategoryWithTransactions
import space.rodionov.financialsobriety.data.Month
import space.rodionov.financialsobriety.data.Transaction
import space.rodionov.financialsobriety.databinding.ItemRecMonthBinding
import java.util.*

class RecTransParentAdapter(
    private val context: Context,
    private val catsWithTransactions: StateFlow<List<CategoryWithTransactions>?>,
    private val scope: CoroutineScope,
    private val onTransactionClick: (Transaction) -> Unit
) : ListAdapter<Month, RecTransParentAdapter.RecParentViewHolder>(RecMonthComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecParentViewHolder {
        val binding = ItemRecMonthBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecParentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecParentViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class RecParentViewHolder(private val binding: ItemRecMonthBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(month: Month) {
            binding.apply {
                tvDateDivider.text = month.toAbbrString().capitalize(Locale.ROOT)
                val recTransChildAdapter = RecTransChildAdapter(context, onTransactionClick)
                childRecyclerView.layoutManager = LinearLayoutManager(context)
                childRecyclerView.adapter = recTransChildAdapter
                scope.launch {
                    catsWithTransactions.collect {
                        val catsWithTransactions =  it ?: return@collect
                        val transactions = mutableListOf<Transaction>()
                        for (cwt in catsWithTransactions) {
                            transactions.addAll(cwt.transactions)
                        }
                        recTransChildAdapter.submitList(month.getTransactionsOfMonth(transactions))
                    }
                }
            }
        }
    }

    class RecMonthComparator : DiffUtil.ItemCallback<Month>() {
        override fun areItemsTheSame(oldItem: Month, newItem: Month) =
            oldItem.mmSlashYear == newItem.mmSlashYear

        override fun areContentsTheSame(oldItem: Month, newItem: Month) = oldItem == newItem
    }
}