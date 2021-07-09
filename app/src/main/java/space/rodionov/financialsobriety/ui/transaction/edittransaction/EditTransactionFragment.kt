package space.rodionov.financialsobriety.ui.transaction.edittransaction

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
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

//        dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        binding.apply {
            etTransactionSum.setText(viewModel.spendSum.toString())
            etTransactionComment.setText(viewModel.spendComment)
            tvCategory.text =
                if (viewModel.spendCategoryName.isNotBlank()) viewModel.spendCategoryName else "Category"

//            if (viewModel.spend != null) {
                tvDate.text = viewModel.spendDateFormatted
//            } else {
//                viewModel.spendDateFormatted = sdf.format(System.currentTimeMillis())
//                tvDate.text = viewModel.spendDateFormatted
//            }
            tvCategory.text = viewModel.spendCategoryName

            cvIsDebt.isVisible = viewModel.spend == null

            etTransactionSum.addTextChangedListener {
                if (!it.toString().isBlank()) viewModel.spendSum = it.toString().toFloat() else 0f
            }
            etTransactionComment.addTextChangedListener {
                viewModel.spendComment = it.toString()
            }

            ivDate.setOnClickListener {
                viewModel.onDatePickerShow()
            }

            layoutChooseCategory.setOnClickListener {
                viewModel.onChooseCategoryClick()
            }

            fabSave.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        setFragmentResultListener("cat_name_request") { _, bundle ->
            val resultCatName = bundle.getString("cat_name_result")
            viewModel.onCatNameResult(resultCatName)
        }

        setFragmentResultListener("date_request") { _, bundle ->
            val resultDate = bundle.getString("date_result")
            Timber.d("LOGS Main Fragment: recieved date = $resultDate")
            viewModel.onDateResult(resultDate)
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
                        val action =
                            EditTransactionFragmentDirections.actionGlobalChooseCategoryDialogFragment(
                                it.catName
                            )
                        findNavController().navigate(action)
                    }
                    is EditTransactionViewModel.EditTransactionEvent.NavigateToDatePickerDialog -> {
                        val action = EditTransactionFragmentDirections.actionGlobalDatePickerDialogFragment(it.dateFormatted)
                        findNavController().navigate(action)
//                        val dialog = DatePickerDialogFragment.newInstance(it.dateFormatted)
//                        dialog.show(parentFragmentManager, DatePickerDialogFragment.TAG)
                    }
                }.exhaustive // чтобы не забыть все Эвенты обработать.
            }
        }
    }

//    fun onDateResult(newDate: String) {
//        viewModel.onDateResult(newDate)
//    }
}








