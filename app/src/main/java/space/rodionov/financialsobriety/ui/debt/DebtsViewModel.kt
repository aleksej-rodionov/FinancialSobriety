package space.rodionov.financialsobriety.ui.debt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.data.Debt
import space.rodionov.financialsobriety.data.FinRepository
import space.rodionov.financialsobriety.ui.ADD_CATEGORY_RESULT_OK
import space.rodionov.financialsobriety.ui.ADD_DEBT_RESULT_OK
import space.rodionov.financialsobriety.ui.EDIT_DEBT_RESULT_OK
import space.rodionov.financialsobriety.ui.categories.CategoriesViewModel
import javax.inject.Inject

@HiltViewModel
class DebtsViewModel @Inject constructor(
    val repo: FinRepository
) : ViewModel() {
    val debts = repo.getAllDebts().stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val debtsSumFlow = repo.getAllDebts().flatMapLatest { debtList ->
        debtsSumFlow(debtList)
    }
    val debtsSum = debtsSumFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)

    //=================EVENT CHANNEL===========================================
    private val debtsEventChannel = Channel<DebtsEvent>()
    val debtsEvent = debtsEventChannel.receiveAsFlow()
    sealed class DebtsEvent {
        data class NavigateToEditDebtScreen(val debt: Debt) : DebtsEvent()
        object NavigateToAddDebtScreen : DebtsEvent()
        data class ShowDebtSavedConfirmMessage(val msg: String) : DebtsEvent()
        data class ShowUndoDeleteDebtMessage(val debt: Debt) : DebtsEvent()
    }

    //======================FUNS===============================================

    fun onDebtClick(debt: Debt) = viewModelScope.launch {
        debtsEventChannel.send(DebtsEvent.NavigateToEditDebtScreen(debt))
    }

    fun onNewDebtClick() = viewModelScope.launch {
        debtsEventChannel.send(DebtsEvent.NavigateToAddDebtScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_DEBT_RESULT_OK -> showDebtSavedConfirmSnackbar("Debt saved")
            EDIT_DEBT_RESULT_OK -> showDebtSavedConfirmSnackbar("Debt updated")
        }
    }

    private fun showDebtSavedConfirmSnackbar(msg: String) = viewModelScope.launch {
        DebtsEvent.ShowDebtSavedConfirmMessage(msg)
    }

    fun onDeleteDebt(debt: Debt) = viewModelScope.launch {
        repo.deleteDebt(debt)
        debtsEventChannel.send(DebtsEvent.ShowUndoDeleteDebtMessage(debt))
    }

    fun onUndoDeleteDebt(debt: Debt) = viewModelScope.launch {
        repo.insertDebt(debt)
    }

    fun debtsSumFlow(debts: List<Debt>) : Flow<Float> {
        val sum = debts.map {
            it.debtSum
        }.sum()
        return MutableStateFlow(sum)
    }
}