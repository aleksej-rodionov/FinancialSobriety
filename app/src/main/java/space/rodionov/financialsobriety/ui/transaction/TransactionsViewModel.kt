package space.rodionov.financialsobriety.ui.transaction

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.*
import space.rodionov.financialsobriety.ui.ADD_TRANSACTION_RESULT_OK
import space.rodionov.financialsobriety.ui.EDIT_TRANSACTION_RESULT_OK
import space.rodionov.financialsobriety.ui.shared.createMonthList
import space.rodionov.financialsobriety.ui.shared.createYearList
import space.rodionov.financialsobriety.util.filePickerIntent
import space.rodionov.financialsobriety.util.generateFile
import space.rodionov.financialsobriety.util.goToFileIntent
import timber.log.Timber
import java.io.File
import java.io.InputStream
import javax.inject.Inject

private const val TAG = "ViewModel LOGS"

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repo: FinRepository,
    private val state: SavedStateHandle,
    private val prefManager: PrefManager
) : ViewModel() {

    private val csvFileName = "transactions.csv"
    private val oriList = mutableListOf<Transaction>()

//==================================SHARED FLOWS====================================================

    private val typeNameFlow = prefManager.typeNameFlow // All three
    val typeName = typeNameFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private var _monthListFlow = MutableStateFlow<List<Month>>(createMonthList())
    val monthListFlow =
        _monthListFlow.stateIn(viewModelScope, SharingStarted.Lazily, null) // Rec, Dia

    private val catsWithTransactionsByTypeFlow = typeNameFlow.flatMapLatest {
        repo.getCatsWithTransactionsByType(TransactionType.valueOf(it))
    }
    val catsWithTransactionsByType = catsWithTransactionsByTypeFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, null) // Rec, Dia


    private val _allTransactionsFlow = MutableStateFlow<List<Transaction>>(oriList)


//==================================BARCHART FLOWS========================================

    private var _yearListFlow = MutableStateFlow(createYearList())
    val yearListFlow = _yearListFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)


//===============================PARENT FLOWS===============================================

    private val catsByTypeFlow = typeNameFlow.flatMapLatest {
        repo.getCategoriesByType(TransactionType.valueOf(it))
    }
    val catsByType = catsByTypeFlow.stateIn(viewModelScope, SharingStarted.Lazily, null)

//==============================PARENT EVENT CHANNEL==============================================

    private val transEventChannel = Channel<TransEvent>()
    val transEvent = transEventChannel.receiveAsFlow()

    sealed class TransEvent {
        data class TransactionsSnackbar(val msg: String) : TransEvent()
        data class GoToFileActivity(val intent: Intent) : TransEvent()
        data class PickFileActivity(val intent: Intent) : TransEvent()
    }

//===============================PARENT FRAGMENT FUNCTIONS===============================================

    fun exportDataToCSVFile(context: Context) {
        val csvFile = generateFile(context, csvFileName)
        if (csvFile != null) {
            exportTransactionsToCSVFile(csvFile)
            Log.d(TAG, "CSV file generated")
            val intent = goToFileIntent(context, csvFile)
            viewModelScope.launch {
                transEventChannel.send(TransEvent.GoToFileActivity(intent))
            }
        } else {
            viewModelScope.launch {
                transEventChannel.send(TransEvent.TransactionsSnackbar("CSV file not generated"))
            }
        }
    }

    private fun exportTransactionsToCSVFile(csvFile: File) {
        csvWriter().open(csvFile, append = false) {
            // Header
            writeRow(
                listOf(
                    "[sum]",
                    "[category]",
                    "[timestamp]",
                    "[comment]",
                    "[type]",
                    "[local db id]",
                    "[remote db author id]"
                )
            )
            // Body
            viewModelScope.launch {
                _allTransactionsFlow.collectLatest { transactions ->
                    transactions.forEach { t ->
                        writeRow(t.sum, t.catName, t.timestamp, t.comment, t.type, t.id, t.authorId)
                    }
                }
            }
        }
    }

    fun importDataFromCSVFile(context: Context) = viewModelScope.launch {
        val intent = filePickerIntent(context)
        Log.d(TAG, "viewModel: Intent created and sent to Channel")
        transEventChannel.send(TransEvent.PickFileActivity(intent))
    }

    fun parseInputStream(inputStream: InputStream) = viewModelScope.launch {
        Timber.d("LOGS: parseInputStream() CALLED ")
        val rows: List<List<String>> = csvReader {
            skipMissMatchedRow = true
        }.readAll(inputStream)
        rows.forEachIndexed { index, row ->
            if (index > 0) {
                val transaction = Transaction(
                    "${row[0]}f".toFloat(),
                    row[1],
                    row[2].toLong(),
                    row[3],
                    TransactionType.valueOf(row[4]),
                    row[5].toInt(),
                    row[6]
                )
                insertTransaction(transaction)

//todo
            }
        }
    }

    private fun insertTransaction(transaction: Transaction) = viewModelScope.launch {
        repo.insertSpend(transaction)
    }

    private fun insertCategory(category: Category) = viewModelScope.launch {
        repo.insertCategory(category)
    }

    fun subscribeToTransactionsFlow() = viewModelScope.launch {
        repo.getAllTransactions().collectLatest {
            updateTransactionsFlow(it)
        }
    }

    private fun updateTransactionsFlow(transactions: List<Transaction>) {
        Log.d(TAG, "upd $transactions")
        _allTransactionsFlow.value = transactions
    }

    fun onCatShownCheckedChanged(name: String, shown: Boolean) = viewModelScope.launch {
        repo.updateCategory(repo.getCatByName(name).copy(catShown = shown))
    }

    fun showInvalidAmountOfCatsMsg() = viewModelScope.launch {
        transEventChannel.send(TransEvent.TransactionsSnackbar("You cannot observe less than 1 category"))
    }

    fun onShowOutcome() = viewModelScope.launch {
        prefManager.updateTypeName(TransactionType.OUTCOME.name)
    }

    fun onShowIncome() = viewModelScope.launch {
        prefManager.updateTypeName(TransactionType.INCOME.name)
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
}