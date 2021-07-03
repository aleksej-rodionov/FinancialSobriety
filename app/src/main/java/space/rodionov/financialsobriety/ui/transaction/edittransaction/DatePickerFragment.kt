package space.rodionov.financialsobriety.ui.transaction.edittransaction

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.util.exhaustive
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DatePickerFragment : DialogFragment() {

    private val viewModel: DatePickerViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        Timber.d("LOGS onCreateDialog vizvan")

        val dateString = viewModel.dateFormatted
        val dateFormatted = SimpleDateFormat("dd.MM.yy", Locale.getDefault()).parse(dateString)
        val sdf = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = dateFormatted
        val todayYear = calendar.get(Calendar.YEAR)
        val todayMonth = calendar.get(Calendar.MONTH)
        val todayDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val newCalendar = Calendar.getInstance()
                newCalendar.set(Calendar.YEAR, year);
                newCalendar.set(Calendar.MONTH, month);
                newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                val newDateString = sdf.format(newCalendar.timeInMillis)
//                viewModel.dateFormatted = newDateString // это ненадо
                viewModel.onDateChosen(newDateString)  // это послать в навигейтБэкк уид резалт
//                this.dismiss()
            }, todayYear, todayMonth, todayDay
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("LOGS onViewCreated vizvan")

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.datePickerEvent.collect {
                when (it) {
                    is DatePickerViewModel.DatePickerEvent.NavigateBackWithResult -> {
                        setFragmentResult("date_request", bundleOf("date_result" to it.dateFormatted))
                    }
                }.exhaustive
            }
        }
    }
}






