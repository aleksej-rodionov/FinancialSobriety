package space.rodionov.financialsobriety.ui.transaction.edittransaction

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.databinding.FragmentEditTransactionBinding
import space.rodionov.financialsobriety.util.exhaustive
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EditTransactionFragment : Fragment(R.layout.fragment_edit_transaction) {
    private val viewModel: EditTransactionViewModel by viewModels()
    private lateinit var binding: FragmentEditTransactionBinding
    private val sdf = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentEditTransactionBinding.bind(view)
        Timber.d("LOGS MainFragment viewModel.spendDateFormatted = ${viewModel.spendDateFormatted}")

        binding.apply {
            viewModel.spendDateFormatted.observe(viewLifecycleOwner) {
                tvDate.setText(it)
            }
            etTransactionSum.setText(viewModel.spendSum.toString())
            viewModel.spendCategoryName.observe(viewLifecycleOwner) {
                tvCategory.setText(it)
            }
            etTransactionComment.setText(viewModel.spendComment)
            viewModel.debtReduced.observe(viewLifecycleOwner) {
                tvCutDebt.text = if (!it.toString().isBlank()) "Cut down debt: $it" else "Is it debt repayment?"
            }


            //===========LISTENERS=================================

            ivDate.setOnClickListener {
                viewModel.onChooseDateClick()
            }
            etTransactionSum.addTextChangedListener {
                if (!it.toString().isBlank()) viewModel.spendSum = it.toString().toFloat() else 0f
            }
            layoutChooseCategory.setOnClickListener {
                viewModel.onChooseCategoryClick()
            }
            etTransactionComment.addTextChangedListener {
                viewModel.spendComment = it.toString()
            }
            layoutChooseDebt.setOnClickListener {
                viewModel.onChooseDebtClick()
            }




            fabSave.setOnClickListener {
                viewModel.onSaveClick()
            }
        }



        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.editTransactionEvent.collect {
                when (it) {
                    is EditTransactionViewModel.EditTransactionEvent.NavigateBackWithResult -> {
                        binding.root.focusedChild?.clearFocus()
                        setFragmentResult( // так мы посылаем fragmentResult. (1-Я ПОЛОВИНА FRAGMENT RESULT-A)
                            "add_edit_request",
                            bundleOf("add_edit_result" to it.result)
                        )
                        findNavController().popBackStack() // чтобы удалить сразу этот фрагмент из бэкстека.
                    }
                    is EditTransactionViewModel.EditTransactionEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), it.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is EditTransactionViewModel.EditTransactionEvent.NavigateToChooseCategoryScreen -> {
                        ChooseCategoryDialogFragment().show(childFragmentManager, ChooseCategoryDialogFragment.TAG)
                    }
                    is EditTransactionViewModel.EditTransactionEvent.NavigateToDatePickerDialog -> {
                        DatePickerDialogFragment().show(childFragmentManager, DatePickerDialogFragment.TAG)
                    }
                    is EditTransactionViewModel.EditTransactionEvent.NavigateToChooseDebtScreen -> {
                        ChooseDebtDialogFragment().show(childFragmentManager, ChooseDebtDialogFragment.TAG)
                    }
                }.exhaustive
            }
        }
    }

//    fun onDateResult(newDate: String) {
//        viewModel.onDateResult(newDate)
//    }
}








