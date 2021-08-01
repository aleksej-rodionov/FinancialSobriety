package space.rodionov.financialsobriety.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.*
import space.rodionov.financialsobriety.ui.ADD_TRANSACTION_RESULT_OK
import space.rodionov.financialsobriety.ui.EDIT_TRANSACTION_RESULT_OK
import space.rodionov.financialsobriety.ui.shared.createMonthList
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: FinRepository,
    private val prefManager: PrefManager,
    private val state: SavedStateHandle
) : ViewModel() {
//================================FLOWS===============================

    private var _monthNames = MutableStateFlow<Pair<String, String>>(Pair(monthFullSdf.format(Calendar.getInstance().time), monthAbbrSdf.format(Calendar.getInstance().time)))
    val monthNames: StateFlow<Pair<String, String>> = _monthNames.asStateFlow()

    private var _monthValues = MutableStateFlow<Triple<Float, Float, Float>>(Triple(0f,0f,0f))
    val monthValues: StateFlow<Triple<Float, Float, Float>> = _monthValues.asStateFlow()

    private var _monthListFlow = MutableStateFlow(createMonthList())
    val monthListFlow = _monthListFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val spendCatsWithTransactionsFlow =
        repo.getCatsWithTransactionsByType(TransactionType.OUTCOME)
    val spendCatsWithTransactions = spendCatsWithTransactionsFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val incomeCatsWithTransactionsFlow =
        repo.getCatsWithTransactionsByType(TransactionType.OUTCOME)
    val incomeCatsWithTransactions = incomeCatsWithTransactionsFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, null)


    //==================EVENT CHANNEL AND SEALED CLASS==================
    private val homeEventChannel = Channel<HomeEvent>()
    val homeEvent = homeEventChannel.receiveAsFlow()

    sealed class HomeEvent {
        object NavigateToSpendsScreen : HomeEvent()
        object NavigateToIncomesScreen : HomeEvent()
        object NavigateToDebtsScreen : HomeEvent()
        object NavigateToAddSpendScreen : HomeEvent()
        object NavigateToAddIncomeScreen : HomeEvent()
        object NavigateToAddDebtScreen : HomeEvent()
        data class ShowTransactionSavedConfirmationMessage(val msg: String) : HomeEvent()
    }

    //========================================METHODS===================================

    fun setMonthValues(
        monthIndex: Int
    ) {
        val month = _monthListFlow.value[monthIndex]
        val spendCats = spendCatsWithTransactions.value
        val incomeCats = incomeCatsWithTransactions.value

        val monthSpendSum = spendCats?.let { sc ->
            month.getTransactionsOfMonth(sc.flatMap {
                it.transactions
            }).map {
                it.sum
            }.sum()
        } ?: 0f

        val monthIncomeSum = incomeCats?.let { ic ->
            month.getTransactionsOfMonth(ic.flatMap {
                it.transactions
            }).map {
                it.sum
            }.sum()
        } ?: 0f

        val monthBalance = monthIncomeSum - monthSpendSum

        _monthNames.value = Pair(month.toString(), month.toAbbrString())
        _monthValues.value = Triple(monthSpendSum, monthIncomeSum, monthBalance)
    }

    fun onSpendsClick(typeName: String) = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToSpendsScreen)
        prefManager.updateTypeName(typeName)
    }

    fun onIncomesClick(typeName: String) = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToIncomesScreen)
        prefManager.updateTypeName(typeName)
    }

    fun onDebtsClick() = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToDebtsScreen)
    }

    fun onAddSpendClick() = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToAddSpendScreen)
    }

    fun onAddIncomeClick() = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToAddIncomeScreen)
    }

    fun onAddDebtClick() = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.NavigateToAddDebtScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_TRANSACTION_RESULT_OK -> showTransactionSavedConfirmationMessage("Transaction added")
            EDIT_TRANSACTION_RESULT_OK -> showTransactionSavedConfirmationMessage("Transaction updated")
        }
    }

    fun showTransactionSavedConfirmationMessage(text: String) = viewModelScope.launch {
        homeEventChannel.send(HomeEvent.ShowTransactionSavedConfirmationMessage(text))
    }
}








