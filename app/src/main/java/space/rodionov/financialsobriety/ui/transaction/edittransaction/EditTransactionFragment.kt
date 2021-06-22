package space.rodionov.financialsobriety.ui.transaction.edittransaction

import android.app.DatePickerDialog
import android.os.Bundle
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

        binding.apply {
            etTransactionSum.setText(viewModel.spendSum.toString())
            etTransactionComment.setText(viewModel.spendComment)

            if (viewModel.spend != null) {
                tvDate.text = viewModel.spend?.dateFormatted
            } else {
                val today = System.currentTimeMillis()
                tvDate.text = sdf.format(today)
            }
            tvCategory.text = viewModel.spendCategoryName

            cvIsDebt.isVisible = viewModel.spend == null

            etTransactionSum.addTextChangedListener {
                 if (!it.toString().isBlank()) viewModel.spendSum = it.toString().toFloat() else 0f
            }
            etTransactionComment.addTextChangedListener {
                viewModel.spendComment = it.toString()
            }

            ivDate.setOnClickListener {
                showDatePicker()
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
                            bundleOf("add_edit_request" to it.result)
                        )
                        findNavController().popBackStack() // чтобы удалить сразу этот фрагмент из бэкстека.
                    }
                    is EditTransactionViewModel.EditTransactionEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), it.msg, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive // чтобы не забыть все Эвенты обработать.
            }
        }
    }

    private fun showDatePicker() {
        val dateString = binding.tvDate.text.toString()
        val dateFormatted = SimpleDateFormat("dd.MM.yy", Locale.getDefault()).parse(dateString)
        val calendar = Calendar.getInstance()
        calendar.time = dateFormatted
        val todayYear = calendar.get(Calendar.YEAR)
        val todayMonth = calendar.get(Calendar.MONTH)
        val todayDay = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(
            requireContext(),
            { view, year, month, dayOfMonth ->
                val newCalendar = Calendar.getInstance()
                newCalendar.set(Calendar.YEAR, year);
                newCalendar.set(Calendar.MONTH, month);
                newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                val newDateString = sdf.format(newCalendar.timeInMillis)
                binding.tvDate.text = newDateString
            }, todayYear, todayMonth, todayDay
        ).show()
    }
}








