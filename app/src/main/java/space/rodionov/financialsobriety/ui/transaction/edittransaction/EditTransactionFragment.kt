package space.rodionov.financialsobriety.ui.transaction.edittransaction

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
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
import space.rodionov.financialsobriety.data.TransactionType
import space.rodionov.financialsobriety.databinding.FragmentEditCategoryBinding
import space.rodionov.financialsobriety.databinding.FragmentEditTransactionBinding
import space.rodionov.financialsobriety.util.exhaustive
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "EditTransaction TAGS"

@AndroidEntryPoint
class EditTransactionFragment : Fragment(R.layout.fragment_edit_transaction) {
    private val viewModel: EditTransactionViewModel by viewModels()

    //    private lateinit var binding: FragmentEditTransactionBinding
    private var _binding: FragmentEditTransactionBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: viewModel.tType = ${viewModel.tType}")
        _binding = FragmentEditTransactionBinding.bind(view)

        var green = Color.parseColor(resources.getString(0 + R.color.green))
        Log.d(TAG, "onViewCreated: green = rarseColor(${resources.getString(0 + R.color.green)})")

        binding.apply {
            viewModel.tDateFormatted.observe(viewLifecycleOwner) {
                tvDate.setText(it)
            }
            etTransactionSum.setText(viewModel.tSum.toString())
            viewModel.tCategoryName.observe(viewLifecycleOwner) {
                if (it.isNotBlank()) tvCategory.setText(it)
            }
            etTransactionComment.setText(viewModel.tComment)
            viewModel.debtReduced.observe(viewLifecycleOwner) {
                    if (it.isNotBlank()) tvCutDebt.setText("Cut down debt: $it")
            }

            if (viewModel.tType == TransactionType.INCOME.name) {
                etTransactionComment.setTextColor(resources.getColor(R.color.green))
                etTransactionSum.setTextColor(resources.getColor(R.color.green))
                fabSave.backgroundTintList = ColorStateList.valueOf(green)
            }


            //===========LISTENERS=================================

            ivDate.setOnClickListener {
                viewModel.onChooseDateClick()
            }
            etTransactionSum.addTextChangedListener {
                if (!it.toString().isBlank()) viewModel.tSum = it.toString().toFloat()
            }
            layoutChooseCategory.setOnClickListener {
                viewModel.onChooseCategoryClick()
            }
            etTransactionComment.addTextChangedListener {
                viewModel.tComment = it.toString()
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
                        ChooseCategoryDialogFragment().show(
                            childFragmentManager,
                            ChooseCategoryDialogFragment.TAG
                        )
                    }
                    is EditTransactionViewModel.EditTransactionEvent.NavigateToDatePickerDialog -> {
                        DatePickerDialogFragment().show(
                            childFragmentManager,
                            DatePickerDialogFragment.TAG
                        )
                    }
                    is EditTransactionViewModel.EditTransactionEvent.NavigateToChooseDebtScreen -> {
                        ChooseDebtDialogFragment().show(
                            childFragmentManager,
                            ChooseDebtDialogFragment.TAG
                        )
                    }
                }.exhaustive
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}








