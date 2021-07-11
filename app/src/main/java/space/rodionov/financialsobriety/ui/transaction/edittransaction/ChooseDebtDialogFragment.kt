package space.rodionov.financialsobriety.ui.transaction.edittransaction

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.Debt
import space.rodionov.financialsobriety.databinding.FragmentDialogRecyclerBinding
import timber.log.Timber

@AndroidEntryPoint
class ChooseDebtDialogFragment : DialogFragment(), ChooseDebtAdapter.OnDebtItemClickListener {
    companion object {
        const val TAG = "ChooseDebtDialog"
    }
    private val viewModel: EditTransactionViewModel by viewModels({ requireParentFragment() })
    private var _binding: FragmentDialogRecyclerBinding? = null
    private val binding get() = _binding!!
    private lateinit var chooseDebtAdapter: ChooseDebtAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        Timber.d("LOGS onCreateDialog vizvan")
        _binding = FragmentDialogRecyclerBinding.inflate(LayoutInflater.from(context))

        return AlertDialog.Builder(requireContext())
            .setTitle(requireContext().resources.getString(R.string.choose_debt))
            .setView(binding.root)
            .setNegativeButton(requireContext().resources.getString(R.string.cancel_action), null)
            .create()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        Timber.d("LOGS onCreateView vizvan")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("LOGS onViewCreated vizvan")

        val debt = viewModel.debtReduced.value
        chooseDebtAdapter = ChooseDebtAdapter(this, debt)

        binding.apply {
            recyclerView.apply {
                viewModel.debts.observe(viewLifecycleOwner) {
                    chooseDebtAdapter.submitList(it)
                    tvNoItems.isVisible = it.isNullOrEmpty()
                }
                adapter = chooseDebtAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }


    }

    override fun onItemClick(debt: Debt) {
        viewModel.onDebtResult(debt)
        this.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




