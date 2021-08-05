package space.rodionov.financialsobriety.ui.transaction.recycler

import android.content.Context
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.*
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.CategoryWithTransactions
import space.rodionov.financialsobriety.data.Month
import space.rodionov.financialsobriety.data.Transaction
import space.rodionov.financialsobriety.databinding.ItemRecMonthBinding
import space.rodionov.financialsobriety.ui.shared.MonthComparator
import java.util.*

class RecTransParentAdapter(
    private val context: Context,
    private val catsWithTransactions: StateFlow<List<CategoryWithTransactions>?>,
    private val scope: CoroutineScope,
    private val onTransactionClick: (Transaction) -> Unit,
    private val onDeleteTransaction: (Transaction) -> Unit
) : ListAdapter<Month, RecTransParentAdapter.RecParentViewHolder>(MonthComparator()) {

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
                        val catsWithTransactions = it ?: return@collect
                        val transactions = mutableListOf<Transaction>()
                        for (cwt in catsWithTransactions) {
                            transactions.addAll(cwt.transactions)
                        }
                        val monthTransactions = month.getTransactionsOfMonth(transactions)
                        if (monthTransactions.isEmpty()) {
                            tvEmpty.visibility = View.VISIBLE
                        } else {
                            val sortedTransactions = monthTransactions.sortedBy {
                                it.timestamp
                            }.asReversed()
                            recTransChildAdapter.submitList(sortedTransactions)
                        }
                    }
                }

                ItemTouchHelper(object :
                    ItemTouchHelper.SimpleCallback(
                        0, /*ItemTouchHelper.LEFT or */
                        ItemTouchHelper.RIGHT
                    ) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val transaction =
                            recTransChildAdapter.currentList[viewHolder.adapterPosition]
                        onDeleteTransaction(transaction)
                    }

                    override fun onChildDraw(
                        c: Canvas,
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        dX: Float,
                        dY: Float,
                        actionState: Int,
                        isCurrentlyActive: Boolean
                    ) {

                        RecyclerViewSwipeDecorator.Builder(
                            c,
                            recyclerView,
                            viewHolder,
                            dX,
                            dY,
                            actionState,
                            isCurrentlyActive
                        )
                            .addBackgroundColor(ContextCompat.getColor(context, R.color.red))
                            .addActionIcon(R.drawable.ic_delete)
                            .create()
                            .decorate();

                        super.onChildDraw(
                            c,
                            recyclerView,
                            viewHolder,
                            dX,
                            dY,
                            actionState,
                            isCurrentlyActive
                        )
                    }
                }).attachToRecyclerView(childRecyclerView)
            }
        }
    }
}