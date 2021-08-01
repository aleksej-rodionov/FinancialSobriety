package space.rodionov.financialsobriety.ui.transaction

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
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repo: FinRepository,
    private val state: SavedStateHandle,
    private val prefManager: PrefManager
) : ViewModel() {

    val typeNameFlow = prefManager.typeNameFlow

//====================================RECYCLER FLOWS=======================================

    private var _monthListFlow = MutableStateFlow<List<Month>>(createMonthList())
    val monthListFlow = _monthListFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val transactionsByTypeFlow = typeNameFlow.flatMapLatest {
        repo.getTransactionsByType(TransactionType.valueOf(it))
    }
    val transactionsByType = transactionsByTypeFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)

//===============================DIAGRAMS FLOWS===============================================

    private val catsWithTransactionsByTypeFlow = typeNameFlow.flatMapLatest {
        repo.getCatsWithTransactionsByType(TransactionType.valueOf(it))
    }
    val catsWithTransactionsByType = catsWithTransactionsByTypeFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val catsByTypeFlow = typeNameFlow.flatMapLatest {
        repo.getCategoriesByType(TransactionType.valueOf(it))
    }
    val catsByType = catsByTypeFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)

//===============================PARENT FRAGMENT FUNCTIONS===============================================

    fun onCatShownCheckedChanged(name: String, shown: Boolean) = viewModelScope.launch {
        repo.updateCategory(repo.getCatByName(name).copy(catShown = shown))
        Timber.d("logs catshown of cat = ${repo.getCatByName(name).catShown}")
    }


//================================RECYCLER EVENT CHANNEL=========================================

    private val recTransEventChannel = Channel<RecTransEvent>()
    val recTransEvent = recTransEventChannel.receiveAsFlow()

    sealed class RecTransEvent {
        object NavigateToAddTransactionScreen : RecTransEvent()
        data class NavigateToEditTransactionScreen(val transaction: Transaction) : RecTransEvent()
        data class ShowUndoDeleteTransactionMessage(val transaction: Transaction) : RecTransEvent()
        data class ShowEditTransConfirmMsg(val msg: String) : RecTransEvent()
    }


    //===================================RECYCLER METHODS=========================================

    fun onTransactionSelected(transaction: Transaction) = viewModelScope.launch {
        recTransEventChannel.send(RecTransEvent.NavigateToEditTransactionScreen(transaction))
    }

    fun addTransaction() = viewModelScope.launch {
        recTransEventChannel.send(RecTransEvent.NavigateToAddTransactionScreen)
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            EDIT_TRANSACTION_RESULT_OK -> showEditTransConfirmSnackbar("Transaction successfully edited")
            ADD_TRANSACTION_RESULT_OK -> showEditTransConfirmSnackbar("Transaction successfully added")
        }
    }

    private fun showEditTransConfirmSnackbar(msg: String) = viewModelScope.launch {
        recTransEventChannel.send(RecTransEvent.ShowEditTransConfirmMsg(msg))
    }

    fun onDeleteTransaction(transaction: Transaction) = viewModelScope.launch {
        repo.deleteSpend(transaction)
        recTransEventChannel.send(RecTransEvent.ShowUndoDeleteTransactionMessage(transaction))
    }

    fun undoDeleteClick(transaction: Transaction) = viewModelScope.launch {
        repo.insertSpend(transaction)
    }


    //=======================SHARED FUNCTIONS==================================

    private fun createMonthList(): List<Month> {
        val monthList = mutableListOf<Month>()
        val calToday = Calendar.getInstance()
        calToday.timeInMillis = System.currentTimeMillis()
        val curYear = calToday.get(Calendar.YEAR)
        val curMonth = calToday.get(Calendar.MONTH)

        for (y in 1970..curYear) {
            if (y < curYear) {
                for (m in 1..12) {
                    var mString = m.toString()
                    if (mString.length < 2) mString = "0$mString"
                    val month = Month("$mString/$y")
                    monthList.add(month)
                }
            } else {
                for (m in 1..curMonth + 1) {
                    var mString = m.toString()
                    if (mString.length < 2) mString = "0$mString"
                    val month = Month("$mString/$y")
                    monthList.add(month)
                }
            }
        }
        monthList.reverse()
        return monthList
    }
}