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
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
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
class DatePickerDialogFragment : DialogFragment() {


    private val viewModel: DatePickerViewModel by viewModels()

//    private var _binding: FragmentDatePickerBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var customView: View

    val sdf = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("LOGS onCreate vizvan")
        dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        showsDialog = true
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        Timber.d("LOGS onActivityCreated vizvan")
////        dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//    }
//
//    override fun onResume() {
//        super.onResume()
//        Timber.d("LOGS onResume vizvan")
////        dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        Timber.d("LOGS onCreateDialog vizvan")

//        _binding = FragmentDatePickerBinding.inflate(LayoutInflater.from(context))
//        customView = binding.root
//
//        Timber.d("LOGS customView = $customView")

//        return AlertDialog.Builder(requireContext())
//            .setTitle(R.string.choose_date)
//            .setView(customView)
//            .setNegativeButton(
//                requireContext().resources.getString(R.string.cancel_action),
//                null
//            )
//            .create()

        val dateString = viewModel.dateFormatted
        val dateFormatted = SimpleDateFormat("dd.MM.yy", Locale.getDefault()).parse(dateString)
        val calendar = Calendar.getInstance()
        calendar.time = dateFormatted
        val todayYear = calendar.get(Calendar.YEAR)
        val todayMonth = calendar.get(Calendar.MONTH)
        val todayDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker =  DatePickerDialog(
            requireContext(),
//            R.style.AppTheme_Dialog_MyDialogTheme,
            { view, year, month, dayOfMonth ->
                val newCalendar = Calendar.getInstance()
                newCalendar.set(Calendar.YEAR, year);
                newCalendar.set(Calendar.MONTH, month);
                newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                val newDateString = sdf.format(newCalendar.timeInMillis)
//                viewModel.dateFormatted = newDateString // это ненадо
                viewModel.onDateChosen(newDateString)  // это послать в навигейтБэкк уид резалт
//                Snackbar.make(requireView(), "Date NOT selected but just picked", Snackbar.LENGTH_SHORT)
//                this.dismiss()
            }, todayYear, todayMonth, todayDay
        )

//        /*val layoutParams = */datePicker.window?.attributes?.width =
//        layoutParams?.width
        return datePicker
    }



//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)
//        Timber.d("LOGS onCreateView vizvan")
//        customView = binding.root
//        return customView
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("LOGS onViewCreated vizvan")
//        val datePicker = binding.datePicker
//
//        val dateString = viewModel.dateFormatted
//        val dateFormatted = SimpleDateFormat("dd.MM.yy", Locale.getDefault()).parse(dateString)
//        val calendar = Calendar.getInstance()
//        calendar.time = dateFormatted
//        val todayYear = calendar.get(Calendar.YEAR)
//        val todayMonth = calendar.get(Calendar.MONTH)
//        val todayDay = calendar.get(Calendar.DAY_OF_MONTH)
//
//        datePicker.init(todayYear, todayMonth, todayDay, { _, year, month, dayOfMonth ->
//                val newCalendar = Calendar.getInstance()
//                newCalendar.set(Calendar.YEAR, year);
//                newCalendar.set(Calendar.MONTH, month);
//                newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                val newDateString = sdf.format(newCalendar.timeInMillis)
////                viewModel.dateFormatted = newDateString // это ненадо
//                viewModel.onDateChosen(newDateString)  // это послать в навигейтБэкк уид резалт
////                this.dismiss()
//            })

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.datePickerEvent.collect {
                when (it) {
                    is DatePickerViewModel.DatePickerEvent.NavigateBackWithResult -> {
                        setFragmentResult("date_request", bundleOf("date_result" to it.dateFormatted))
                        Snackbar.make(requireView(), "Date selected", Snackbar.LENGTH_SHORT)
                    }
                }.exhaustive
            }
        }
    }

//    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
//        val newCalendar = Calendar.getInstance()
//        newCalendar.set(Calendar.YEAR, year);
//        newCalendar.set(Calendar.MONTH, month);
//        newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//        val newDateString = sdf.format(newCalendar.timeInMillis)
//        viewModel.onDateChosen(newDateString)
//    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
}






