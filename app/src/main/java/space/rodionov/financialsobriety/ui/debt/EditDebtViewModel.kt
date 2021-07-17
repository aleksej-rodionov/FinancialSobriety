package space.rodionov.financialsobriety.ui.debt

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.Debt
import space.rodionov.financialsobriety.data.FinRepository
import space.rodionov.financialsobriety.ui.ADD_DEBT_RESULT_OK
import space.rodionov.financialsobriety.ui.EDIT_DEBT_RESULT_OK
import space.rodionov.financialsobriety.ui.categories.EditCategoryViewModel
import javax.inject.Inject

@HiltViewModel
class EditDebtViewModel @Inject constructor(
    private val repo: FinRepository,
    private val state: SavedStateHandle
) : ViewModel() {
    val title = state.get<String>("title")
    val debt = state.get<Debt>("debt")
    var debtName = state.get<String>("debtName") ?: debt?.debtName ?: ""
        set(value) {
            field = value
            state.set("debtName", value)
        }
    var debtSum = state.get<Float>("debtSum") ?: debt?.debtSum ?: 0f
        set(value) {
            field = value
            state.set("debtSum", value)
        }

    //=============EVENT CHANNEL==========================================
    private val editDebtEventChannel = Channel<EditDebtEvent>()
    val editDebtEvent = editDebtEventChannel.receiveAsFlow()
    sealed class EditDebtEvent {
        data class NavigateBackWithDebtResult(val result: Int) : EditDebtEvent()
        data class ShowInvalidInputMsg(val msg: String) : EditDebtEvent()
    }

    //==============================FUNS====================================
    fun onSaveClick() {
        if (debtName.isBlank()) {
            showInvalidInputSnackbar("Debt name cannot be empty")
            return
        }
        if (debt != null) {
            val updatedDebt = debt.copy(debtName, debtSum)
            updateDebt(updatedDebt)
        } else {
            val newDebt = Debt(debtName, debtSum)
            createDebt(newDebt)
        }
    }

    private fun createDebt(debt: Debt) = viewModelScope.launch {
        repo.insertDebt(debt)
        editDebtEventChannel.send(EditDebtEvent.NavigateBackWithDebtResult(ADD_DEBT_RESULT_OK))
    }

    private fun updateDebt(debt: Debt) = viewModelScope.launch {
        repo.updateDebt(debt)
        editDebtEventChannel.send(EditDebtEvent.NavigateBackWithDebtResult(EDIT_DEBT_RESULT_OK))
    }

    private fun showInvalidInputSnackbar(msg: String) = viewModelScope.launch {
        editDebtEventChannel.send(EditDebtEvent.ShowInvalidInputMsg(msg))
    }
}









