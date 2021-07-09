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

//    companion object {
//        const val TAG = "DatePickerDialog"
//        fun newInstance(dateFormatted: String): DatePickerDialogFragment {
//            val df = DatePickerDialogFragment()
////            val args = Bundle()
////            args.putString("dateFormatted", dateFormatted)
////            df.arguments = args
//            df.arguments = bundleOf("dateFormatted" to dateFormatted)
//            return df
//        }
//
////        fun dateListener(manager: FragmentManager, lifecycleOwner: LifecycleOwner, listener: (String) -> Unit) {
////            manager.setFragmentResultListener("date_request", lifecycleOwner, FragmentResultListener { _, result ->
////                listener.invoke(result.getString())
////            })
////        }
//    }

        private val viewModel: DatePickerViewModel by viewModels()
//    private val viewModel: EditTransactionViewModel by viewModels()
    val sdf = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("LOGS onCreate vizvan")

//        viewModel.dateFormatted = arguments?.getString("dateFormatted").toString()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Timber.d("LOGS onCreateDialog vizvan")

        val dateString = viewModel.dateFormatted
//        val dateString = viewModel.spendDateFormatted // NEW
        Timber.d("LOGS Current Date String = $dateString")
        val dateFormatted = SimpleDateFormat("dd.MM.yy", Locale.getDefault()).parse(dateString)
        val calendar = Calendar.getInstance()
        calendar.time = dateFormatted
        val todayYear = calendar.get(Calendar.YEAR)
        val todayMonth = calendar.get(Calendar.MONTH)
        val todayDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireContext(),
            this,
            todayYear,
            todayMonth,
            todayDay
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
        Timber.d("LOGS onCreateView vizvan")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("LOGS onViewCreated vizvan")

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.datePickerEvent.collect {
                when (it) {
                    is DatePickerViewModel.DatePickerEvent.NavigateBackWithResult -> {
                        setFragmentResult(
                            "date_request",
                            bundleOf("date_result" to it.dateFormatted)
                        )
                        Timber.d("LOGS date selected: ${it.dateFormatted}")

                    }
                }.exhaustive
            }
        }
    }

    override fun onDateSet(dp: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val newCalendar = Calendar.getInstance()
        newCalendar.set(Calendar.YEAR, year);
        newCalendar.set(Calendar.MONTH, month);
        newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        val newDateString = sdf.format(newCalendar.timeInMillis)
        Timber.d("LOGS New Date String = $newDateString")
        viewModel.dateFormatted = newDateString
        Timber.d("LOGS new viewmodel.dateFormatted = ${viewModel.dateFormatted}")
        viewModel.onDateChosen(newDateString)
//        viewModel.spendDateFormatted = newDateString
//        Timber.d("LOGS new viewmodel.spendDateFormatted = ${viewModel.spendDateFormatted}")
        this.dismiss()
    }
}






