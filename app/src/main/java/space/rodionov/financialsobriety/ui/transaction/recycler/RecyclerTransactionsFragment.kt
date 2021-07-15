package space.rodionov.financialsobriety.ui.transaction.recycler

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.Transaction
import space.rodionov.financialsobriety.data.TransactionType
import space.rodionov.financialsobriety.databinding.FragmentTransactionsRecyclerBinding
import space.rodionov.financialsobriety.ui.transaction.TransactionsFragmentDirections
import space.rodionov.financialsobriety.util.exhaustive

@AndroidEntryPoint
class RecyclerTransactionsFragment : Fragment(R.layout.fragment_transactions_recycler),
    RecyclerTransactionAdapter.OnItemClickListener {

    private lateinit var binding: FragmentTransactionsRecyclerBinding

    private val viewModel: RecyclerTransactionsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTransactionsRecyclerBinding.bind(view)

        val transAdapter = RecyclerTransactionAdapter(this)

        binding.apply {
            recyclerView.apply {
                adapter = transAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.spends.collect {
                    val spends = it ?: return@collect

                    transAdapter.submitList(spends)
                    tvNoTransactions.isVisible = spends.isEmpty()
                    recyclerView.isVisible = spends.isNotEmpty()
                }
            }

            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val transaction = transAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTransSwiped(transaction)
                }
            }).attachToRecyclerView(recyclerView)

            btnAdd.setOnClickListener {
                viewModel.addTransaction()
            }
        }

        setFragmentResultListener("add_edit_result") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.recTransEvent.collect { event ->
                when (event) {
                    is RecyclerTransactionsViewModel.RecTransEvent.NavigateToAddTransactionScreen -> {
                        val action =
                            TransactionsFragmentDirections.actionTransactionsFragmentToEditTransactionFragment(
                                null,
                                "New transaction",
                                TransactionType.OUTCOME.name
                            )
                        findNavController().navigate(action)
                    }
                    is RecyclerTransactionsViewModel.RecTransEvent.NavigateToEditTransactionScreen -> {
                        val action =
                            TransactionsFragmentDirections.actionTransactionsFragmentToEditTransactionFragment(
                                event.transaction,
                                "Edit transaction",
                                event.transaction.type.name
                            )
                        findNavController().navigate(action)
                    }
                    is RecyclerTransactionsViewModel.RecTransEvent.ShowUndoDeleteTransactionMessage -> {
                        Snackbar.make(
                            requireView(),
                            "Transaction deleted for good",
                            Snackbar.LENGTH_LONG
                        )
                            .setAction("UNDO") { viewModel.undoDeleteClick(event.transaction) }
                            .show()
                    }
                    is RecyclerTransactionsViewModel.RecTransEvent.ShowEditTransConfirmMsg -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                }.exhaustive
            }
        }
    }

    override fun onItemClick(transaction: Transaction) {
        viewModel.onTransactionSelected(transaction)
    }
}