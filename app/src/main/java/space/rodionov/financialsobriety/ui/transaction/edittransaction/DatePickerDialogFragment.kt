package space.rodionov.financialsobriety.ui.transaction.edittransaction

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.util.exhaustive
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DatePickerDialogFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    companion object {
        const val TAG = "DatePickerDialog"
    }

    private val viewModel: EditTransactionViewModel by viewModels({ requireParentFragment() })
    val sdf = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Timber.d("LOGS onCreateDialog vizvan")

        val dateFormatted = viewModel.spendDateFormatted.value ?: sdf.format(System.currentTimeMillis()) // to viewModel
        val dateParsed = SimpleDateFormat("dd.MM.yy", Locale.getDefault()).parse(dateFormatted) // to viewModel
        val calendar = Calendar.getInstance() // to viewModel
        calendar.time = dateParsed
        val todayYear = calendar.get(Calendar.YEAR)
        val todayMonth = calendar.get(Calendar.MONTH)
        val todayDay = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireContext(), this, todayYear, todayMonth, todayDay)
    }

    override fun onDateSet(dp: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val newCalendar = Calendar.getInstance()
        newCalendar.set(Calendar.YEAR, year);
        newCalendar.set(Calendar.MONTH, month);
        newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        val newDateFormatted = sdf.format(newCalendar.timeInMillis)
        viewModel.onDateResult(newDateFormatted)
    }
}






