package space.rodionov.financialsobriety.ui.transaction.edittransaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.FinRepository
import space.rodionov.financialsobriety.data.Spend
import space.rodionov.financialsobriety.ui.ADD_TRANSACTION_RESULT_OK
import space.rodionov.financialsobriety.ui.EDIT_TRANSACTION_RESULT_OK
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditTransactionViewModel @Inject constructor(
    private val repo: FinRepository,
    private val state: SavedStateHandle
) : ViewModel() {
    private val sdf = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

    private val editTransactionEventChannel = Channel<EditTransactionEvent>()
    val editTransactionEvent = editTransactionEventChannel.receiveAsFlow()

    val spend = state.get<Spend>("spend")

    var spendDateFormatted = state.getLiveData("spendDateFormatted", spend?.dateFormatted ?: sdf.format(System.currentTimeMillis()))

    var spendSum = state.get<Float>("spendSum") ?: spend?.sum ?: 0f
        set(value) {
            field = value
            state.set("spendSum", value)
        }

    var spendCategoryName = state.getLiveData("spendCategoryName", spend?.categoryName ?: "Other")

    var spendComment = state.get<String?>("spendComment") ?: spend?.comment ?: ""
        set(value) {
            field = value
            state.set("spendComment", value)
        }

    var debtReduced = state.getLiveData("debtReduced", "")

    

    //=======================================================================

    fun onSaveClick() {
        if (spendSum.equals(0f) || spendSum.toString().isBlank() || spendCategoryName.value.isNullOrBlank()) {
            showInvalidInputMessage("Укажите сумму и категорию") // EVENT caller
            return
        }
        val calendar = Calendar.getInstance()
        calendar.time = sdf.parse(spendDateFormatted.value)

        if (spend != null) {
            val updatedSpend = spend.copy(
                sum = spendSum,
                categoryName = spendCategoryName.value,
                timestamp = calendar.timeInMillis / 1000,
                comment = spendComment
            )
            updateSpend(updatedSpend) // EVENT caller
        } else {
            val newSpend = Spend(spendSum, spendCategoryName.value, calendar.timeInMillis / 1000, spendComment)
            insertSpend(newSpend) // EVENT caller
        }
    }



    // ==============================================

    fun onDatePickerShow() = viewModelScope.launch {
        editTransactionEventChannel.send(EditTransactionEvent.NavigateToDatePickerDialog(spendDateFormatted.value ?: sdf.format(System.currentTimeMillis())))
    }

    fun onChooseCategoryClick() = viewModelScope.launch {
        editTransactionEventChannel.send(EditTransactionEvent.NavigateToChooseCategoryScreen(spendCategoryName.value))
    }

    fun onCatNameResult(resultCatName: String?) = viewModelScope.launch {
        spendCategoryName.value = resultCatName ?: ""
        if (resultCatName == null) {
            Timber.d("LOGS resultCatName is NULL")
        } else {
            Timber.d("LOGS resultCanName is $resultCatName")
        }
    }

    fun onDateResult(resultDate: String?) = viewModelScope.launch {
        spendDateFormatted.value = resultDate ?: sdf.format(System.currentTimeMillis())
        if (resultDate == null) {
            Timber.d("LOGS resultDate is NULL")
        } else {
            Timber.d("LOGS resultDate is $resultDate")
        }
    }

    private fun updateSpend(spend: Spend) = viewModelScope.launch {
        repo.updateSpend(spend)
        editTransactionEventChannel.send(
            EditTransactionEvent.NavigateBackWithResult(
                EDIT_TRANSACTION_RESULT_OK
            )
        ) // EVENT
    }

    private fun insertSpend(spend: Spend) = viewModelScope.launch {
        repo.insertSpend(spend)
        editTransactionEventChannel.send(
            EditTransactionEvent.NavigateBackWithResult(
                ADD_TRANSACTION_RESULT_OK
            )
        ) // EVENT
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        editTransactionEventChannel.send(EditTransactionEvent.ShowInvalidInputMessage(text)) // EVENT
    }

    // these events are in Event cause only Fragment can show a snackbar or execute navigation events:
    sealed class EditTransactionEvent {
        data class NavigateToDatePickerDialog(val dateFormatted: String) : EditTransactionEvent()
        data class NavigateToChooseCategoryScreen(val catName: String?) : EditTransactionEvent()
        data class ShowInvalidInputMessage(val msg: String) : EditTransactionEvent()
        data class NavigateBackWithResult(val result: Int) : EditTransactionEvent()
    }

}









