package space.rodionov.financialsobriety.ui.transaction.recycler

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.Spend
import space.rodionov.financialsobriety.databinding.FragmentTransactionsRecyclerBinding
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

            btnAdd.setOnClickListener {
                viewModel.addTransaction()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.recTransEvent.collect {
                when (it) {
                    is RecyclerTransactionsViewModel.RecTransEvent.NavigateToAddTransactionScreen -> TODO()
                    is RecyclerTransactionsViewModel.RecTransEvent.NavigateToDeleteTransactionScreen -> {
                        val action = RecyclerTransactionsFragmentDirections.actionGlobalDeleteTransactionDialogFragment(it.spend)
                        findNavController().navigate(action)
                    }
                    is RecyclerTransactionsViewModel.RecTransEvent.NavigateToEditTransactionScreen -> TODO()
                    is RecyclerTransactionsViewModel.RecTransEvent.ShowUndoDeleteTransactionMessage -> TODO()
                }.exhaustive
            }
        }
    }

    override fun onItemClick(spend: Spend) {
        viewModel.onTransactionSelected(spend)
    }

    override fun onItemLongClick(spend: Spend) {
        viewModel.onDeleteTransaction(spend)
    }


}