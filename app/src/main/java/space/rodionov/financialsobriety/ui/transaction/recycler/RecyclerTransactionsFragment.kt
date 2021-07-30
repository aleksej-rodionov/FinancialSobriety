package space.rodionov.financialsobriety.ui.transaction.recycler

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.Transaction
import space.rodionov.financialsobriety.data.TransactionType
import space.rodionov.financialsobriety.databinding.FragmentTransactionsRecyclerBinding
import space.rodionov.financialsobriety.ui.transaction.TransactionsFragmentDirections
import space.rodionov.financialsobriety.ui.transaction.TransactionsViewModel
import space.rodionov.financialsobriety.util.exhaustive
import timber.log.Timber
import java.util.*

private const val TAG = "RecTransFr LOGS"

@AndroidEntryPoint
class RecyclerTransactionsFragment : Fragment(R.layout.fragment_transactions_recycler) {
    private lateinit var binding: FragmentTransactionsRecyclerBinding
    private val viewModel: TransactionsViewModel by viewModels({ requireParentFragment() })

    lateinit var parentAdapter: RecTransParentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTransactionsRecyclerBinding.bind(view)
        Timber.d("LOGS Recycler vonViewCreated genType = ${viewModel.typeName.value}")

        parentAdapter = RecTransParentAdapter(
            requireContext(),
            viewModel.transactionsByType,
            viewLifecycleOwner.lifecycleScope,
            onTransactionClick = {
                onItemClick(it)
                Timber.d("logs ontransclick called")
                Log.d(TAG, "logs onTransClick: called ($it)")
            }
        )

        binding.apply {
            recyclerView.apply {
                adapter = parentAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.monthListFlow.collect {
                    val months = it ?: return@collect
                    parentAdapter.submitList(months)
                }
            }

//            ItemTouchHelper(object :
//                ItemTouchHelper.SimpleCallback(
//                    0, /*ItemTouchHelper.LEFT or */
//                    ItemTouchHelper.RIGHT
//                ) {
//                override fun onMove(
//                    recyclerView: RecyclerView,
//                    viewHolder: RecyclerView.ViewHolder,
//                    target: RecyclerView.ViewHolder
//                ): Boolean {
//                    return false
//                }
//
//                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                    val transaction = transAdapter.currentList[viewHolder.adapterPosition]
//                    viewModel.onDeleteTransaction(transaction)
//                }
//            }).attachToRecyclerView(recyclerView)

            btnAdd.setOnClickListener {
                viewModel.addTransaction()
            }
        }

        setFragmentResultListener("add_edit_request") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.recTransEvent.collect { event ->
                when (event) {
                    is TransactionsViewModel.RecTransEvent.NavigateToAddTransactionScreen -> {
                        val action =
                            TransactionsFragmentDirections.actionTransactionsFragmentToEditTransactionFragment(
                                null,
                                "New spending",
                                TransactionType.OUTCOME.name
                            )
                        findNavController().navigate(action)
                    }
                    is TransactionsViewModel.RecTransEvent.NavigateToEditTransactionScreen -> {
                        val action =
                            TransactionsFragmentDirections.actionTransactionsFragmentToEditTransactionFragment(
                                event.transaction,
                                "Edit ${event.transaction.type.name.toLowerCase(Locale.ROOT)}",
                                event.transaction.type.name
                            )
                        findNavController().navigate(action)
                    }
                    is TransactionsViewModel.RecTransEvent.ShowUndoDeleteTransactionMessage -> {
                        Snackbar.make(
                            requireView(),
                            "Transaction deleted for good",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("UNDO") { viewModel.undoDeleteClick(event.transaction) }
                            .show()
                    }
                    is TransactionsViewModel.RecTransEvent.ShowEditTransConfirmMsg -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                }.exhaustive
            }
        }
    }

    private fun onItemClick(transaction: Transaction) {
        viewModel.onTransactionSelected(transaction)
    }
}