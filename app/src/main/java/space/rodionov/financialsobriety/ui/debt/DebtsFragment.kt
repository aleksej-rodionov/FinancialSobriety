package space.rodionov.financialsobriety.ui.debt

import android.os.Bundle
import android.view.View
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
import space.rodionov.financialsobriety.data.Debt
import space.rodionov.financialsobriety.databinding.FragmentDebtsBinding
import space.rodionov.financialsobriety.util.exhaustive

@AndroidEntryPoint
class DebtsFragment : Fragment(R.layout.fragment_debts), DebtsAdapter.OnDebtClickListener {
    private val viewModel: DebtsViewModel by viewModels()
    private var _binding: FragmentDebtsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDebtsBinding.bind(view)
        val debtAdapter = DebtsAdapter(this)
        binding.apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = debtAdapter
                setHasFixedSize(true)
            }

            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, /*ItemTouchHelper.LEFT or */ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val debt = debtAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onDeleteDebt(debt)
                }
            }).attachToRecyclerView(recyclerView)

            btnAdd.setOnClickListener {
                viewModel.onNewDebtClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.debts.collect {
                val debts = it ?: return@collect
                debtAdapter.submitList(debts)
            }
        }

        setFragmentResultListener("add_edit_debt_request") { _, bundle ->
            val result = bundle.getInt("add_edit_debt_result")
            viewModel.onAddEditResult(result)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.debtsEvent.collect { event ->
                when (event) {
                    is DebtsViewModel.DebtsEvent.NavigateToAddDebtScreen -> {
                        val action = DebtsFragmentDirections.actionDebtsFragmentToEditDebtFragment("New debt", null)
                        findNavController().navigate(action)
                    }
                    is DebtsViewModel.DebtsEvent.NavigateToEditDebtScreen -> {
                        val action = DebtsFragmentDirections.actionDebtsFragmentToEditDebtFragment("Edit debt", event.debt)
                        findNavController().navigate(action)
                    }
                    is DebtsViewModel.DebtsEvent.ShowDebtSavedConfirmMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    is DebtsViewModel.DebtsEvent.ShowUndoDeleteDebtMessage -> {
                        Snackbar.make(requireView(), requireContext().resources.getString(R.string.debt_deleted), Snackbar.LENGTH_LONG)
                            .setAction(requireContext().resources.getString(R.string.undo)) {
                                viewModel.onUndoDeleteDebt(event.debt)
                            }.show()
                    }
                }.exhaustive
            }
        }
    }

    override fun onDebtClick(debt: Debt) {
        viewModel.onDebtClick(debt)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




