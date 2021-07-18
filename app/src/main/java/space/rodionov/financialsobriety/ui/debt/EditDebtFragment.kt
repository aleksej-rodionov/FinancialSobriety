package space.rodionov.financialsobriety.ui.debt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.databinding.FragmentEditCategoryBinding
import space.rodionov.financialsobriety.databinding.FragmentEditDebtBinding
import space.rodionov.financialsobriety.util.exhaustive

@AndroidEntryPoint
class EditDebtFragment : BottomSheetDialogFragment() {
    private val viewModel: EditDebtViewModel by viewModels()
    private var _binding: FragmentEditDebtBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditDebtBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvTitle.text = viewModel.title
            etDebtName.setText(viewModel.debtName)
            etDebtSum.setText(viewModel.debtSum.toString())

            //===========================LISTENERS=================================

            etDebtName.addTextChangedListener { viewModel.debtName = it.toString() }
            etDebtSum.addTextChangedListener {
                if (it.toString().isNotBlank()) viewModel.debtSum = it.toString().toFloat()
            }
            btnSave.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.editDebtEvent.collect { event ->
                when (event) {
                    is EditDebtViewModel.EditDebtEvent.NavigateBackWithDebtResult -> {
                        binding.etDebtName.clearFocus()
                        setFragmentResult(
                            "add_edit_debt_request",
                            bundleOf("add_edit_debt_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is EditDebtViewModel.EditDebtEvent.ShowInvalidInputMsg -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }
    }

    override fun onStart() {
        super.onStart()
        //this forces the sheet to appear at max height even on landscape
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}