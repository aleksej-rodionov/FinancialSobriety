package space.rodionov.financialsobriety.ui.transaction.edittransaction

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.*
import space.rodionov.financialsobriety.ui.ADD_TRANSACTION_RESULT_OK
import space.rodionov.financialsobriety.ui.EDIT_TRANSACTION_RESULT_OK
import java.util.*
import javax.inject.Inject

private const val TAG = "EditTransViewModl TAGS"

@HiltViewModel
class EditTransactionViewModel @Inject constructor(
    private val repo: FinRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private val editTransactionEventChannel = Channel<EditTransactionEvent>()
    val editTransactionEvent = editTransactionEventChannel.receiveAsFlow()

    //    fun getCategoriesByType(type: TransactionType) = repo.getCategoriesByType(type).asLiveData()
    fun getCategoriesByType(type: TransactionType) = repo.getCategoriesByType(type)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val debts = repo.getAllDebts().stateIn(viewModelScope, SharingStarted.Lazily, null)


    //==========SAVED STATE HANDLE===========================================
    val tType = state.get<String>("type") ?: "Outcome"

    val transaction = state.get<Transaction>("transaction")

    var tDateFormatted = state.getLiveData(
        "spendDateFormatted",
        transaction?.dateFormatted ?: sdf.format(System.currentTimeMillis())
    )

    var tSum = state.get<Float>("spendSum") ?: transaction?.sum ?: 0f
        set(value) {
            field = value
            state.set("spendSum", value)
        }

    var tCategoryName = state.getLiveData("spendCategoryName", transaction?.catName ?: "")

    var tComment = state.get<String?>("spendComment") ?: transaction?.comment ?: ""
        set(value) {
            field = value
            state.set("spendComment", value)
        }

    var debtReduced = state.getLiveData("debtReduced", "")


    //=======================================================================

    fun onSaveClick() {
        if (tSum.equals(0f) || tSum.toString().isBlank() || tCategoryName.value.isNullOrBlank()) {
            showInvalidInputMessage("Enter sum and category") // EVENT caller
            return
        }
        val calendar = Calendar.getInstance()
        calendar.time = sdf.parse(tDateFormatted.value)

        if (transaction != null) {
            val updatedSpend = transaction.copy(
                sum = tSum,
                catName = tCategoryName.value,
                timestamp = calendar.timeInMillis / 1000,
                comment = tComment,
                type = enumValueOf(tType)
            )
            updateSpend(updatedSpend) // EVENT caller
        } else {
            val newSpend = Transaction(
                tSum,
                tCategoryName.value,
                calendar.timeInMillis / 1000,
                tComment,
                enumValueOf(tType)
            )
            insertSpend(newSpend) // EVENT caller
        }
    }


    // ==============================================

    fun onChooseDateClick() = viewModelScope.launch {
        editTransactionEventChannel.send(EditTransactionEvent.NavigateToDatePickerDialog) // EVENT
    }

    fun onDateResult(resultDate: String?) = viewModelScope.launch {
        tDateFormatted.value = resultDate
    }

    fun onChooseCategoryClick() = viewModelScope.launch {
        editTransactionEventChannel.send(EditTransactionEvent.NavigateToChooseCategoryScreen/*(categories.value!!)*/) // EVENT
    }

    fun onCategoryResult(resultCategory: Category?) = viewModelScope.launch {
        tCategoryName.value = resultCategory?.catName ?: "Choose category"
    }

    fun onChooseDebtClick() = viewModelScope.launch {
        editTransactionEventChannel.send(EditTransactionEvent.NavigateToChooseDebtScreen) // EVENT
    }

    fun onDebtResult(resultDebt: Debt) {
        debtReduced.value = resultDebt.debtName
    }

    fun onClearDebt() {
        debtReduced.value = ""
    }

    private fun updateSpend(transaction: Transaction) = viewModelScope.launch {
        repo.updateSpend(transaction)
        editTransactionEventChannel.send(
            EditTransactionEvent.NavigateBackWithResult(
                EDIT_TRANSACTION_RESULT_OK
            )
        ) // EVENT
    }

    private fun insertSpend(transaction: Transaction) = viewModelScope.launch {
        repo.insertSpend(transaction)
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
        object NavigateToDatePickerDialog : EditTransactionEvent()
        object NavigateToChooseCategoryScreen : EditTransactionEvent()
        object NavigateToChooseDebtScreen : EditTransactionEvent()
        data class ShowInvalidInputMessage(val msg: String) : EditTransactionEvent()
        data class NavigateBackWithResult(val result: Int) : EditTransactionEvent()
    }

}









