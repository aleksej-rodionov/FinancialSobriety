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
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: FinRepository,
    private val prefManager: PrefManager,
    private val state: SavedStateHandle
) : ViewModel() {
//================================FLOWS===============================

    private var _monthListFlow = MutableStateFlow(createMonthList())
    val monthListFlow = _monthListFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)

    var _monthIndex = MutableStateFlow<Int>(0)
    private val monthIndex: StateFlow<Int> = _monthIndex.asStateFlow()

    private val spendCatsWithTransactionsFlow =
        repo.getCatsWithTransactionsByType(TransactionType.OUTCOME)

    private val incomeCatsWithTransactionsFlow =
        repo.getCatsWithTransactionsByType(TransactionType.INCOME)

    private val monthDataFlow = combine(
        monthIndex,
        spendCatsWithTransactionsFlow,
        incomeCatsWithTransactionsFlow
    ) { monthIndex, spendCats, incomeCats ->
        Triple(monthIndex, spendCats, incomeCats)
    }.flatMapLatest {
        setMonthValues(it.first, it.second, it.third)
    }
    val monthData = monthDataFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val debtsSumFlow = repo.getAllDebts().flatMapLatest { debtList ->
        debtsSumFlow(debtList)
    }
    val debtsSum = debtsSumFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)

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

    fun debtsSumFlow(debts: List<Debt>) : Flow<Float> {
        val sum = debts.map {
            it.debtSum
        }.sum()
        return MutableStateFlow(sum)
    }

    fun setMonthIndex(index: Int) {
        _monthIndex.value = index
    }

    fun setMonthValues(
        monthIndex: Int,
        spendCats: List<CategoryWithTransactions>,
        incomeCats: List<CategoryWithTransactions>
    ): Flow<Pair<Pair<String, String>, Triple<Float, Float, Float>>> {
        val month = _monthListFlow.value[monthIndex]
        Timber.d("logs spend-cwt.value.size = ${spendCats.size}")
        Timber.d("logs income-cwt.value.size = ${incomeCats.size}")

        val monthSpendSum = spendCats.let { sc ->
            month.getTransactionsOfMonth(sc.flatMap {
                it.transactions
            }).map {
                it.sum
            }.sum()
        }

        val monthIncomeSum = incomeCats.let { ic ->
            month.getTransactionsOfMonth(ic.flatMap {
                it.transactions
            }).map {
                it.sum
            }.sum()
        }

        Timber.d("logs monthSpendSum = $monthSpendSum")
        Timber.d("logs monthIncomeSum = $monthIncomeSum")
        val monthBalance = monthIncomeSum - monthSpendSum

        val monthNamesValue = Pair(month.toString(), month.toAbbrString())
        val monthValuesValue = Triple(monthSpendSum, monthIncomeSum, monthBalance)
        return MutableStateFlow(Pair(monthNamesValue, monthValuesValue))
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








