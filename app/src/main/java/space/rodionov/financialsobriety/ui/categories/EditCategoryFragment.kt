package space.rodionov.financialsobriety.ui.categories

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
import space.rodionov.financialsobriety.data.TransactionType
import space.rodionov.financialsobriety.databinding.FragmentDialogRecyclerBinding
import space.rodionov.financialsobriety.databinding.FragmentEditCategoryBinding
import space.rodionov.financialsobriety.util.exhaustive
import java.util.*

@AndroidEntryPoint
class EditCategoryFragment : BottomSheetDialogFragment() {
    private val viewModel: EditCategoryViewModel by viewModels()
    private var _binding: FragmentEditCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditCategoryBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvTitle.text = viewModel.title
            etCatName.setText(viewModel.catName)
            switchButton.isChecked = viewModel.catType == TransactionType.INCOME
            switchButton.text = viewModel.catType.name.toLowerCase(Locale.getDefault()).capitalize(Locale.getDefault())

            //====================LISTENERS================================================
            etCatName.addTextChangedListener {
                viewModel.catName = it.toString()
            }
            switchButton.setOnCheckedChangeListener { btn, isChecked ->
                if (isChecked) {
                    viewModel.catType = TransactionType.INCOME
                    btn.text = "Income"
                } else {
                    viewModel.catType = TransactionType.OUTCOME
                    btn.text = "Outcome"
                }
            }

            btnSave.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.editCatEvent.collect {
                when (it){
                    is EditCategoryViewModel.EditCatEvent.NavigateBackWithResult -> {
                        binding.etCatName.clearFocus()
                        setFragmentResult("add_edit_request", bundleOf("add_edit_result" to it.result))
                        findNavController().popBackStack()
                    }
                    is EditCategoryViewModel.EditCatEvent.ShowInvalidInputMsg -> {
                        Snackbar.make(requireView(), it.msg, Snackbar.LENGTH_LONG).show()
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