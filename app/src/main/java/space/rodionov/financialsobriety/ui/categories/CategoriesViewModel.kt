package space.rodionov.financialsobriety.ui.categories

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.data.FinRepository
import space.rodionov.financialsobriety.ui.ADD_CATEGORY_RESULT_OK
import space.rodionov.financialsobriety.ui.CAT_DEL_RESULT_COMPLETE_DELETION
import space.rodionov.financialsobriety.ui.CAT_DEL_RESULT_CONTENT_RELOCATED
import space.rodionov.financialsobriety.ui.EDIT_CATEGORY_RESULT_OK
import javax.inject.Inject

private const val TAG = "CatViewModel LOGS"

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val repo: FinRepository
) : ViewModel() {
    val categories = repo.getAllCategories().stateIn(viewModelScope, SharingStarted.Lazily, null)


    //=================EVENT CHANNEL================================

    private val categoriesEventChannel = Channel<CategoriesEvent>() // SEALED_EVENT 2,
    val categoriesEvent = categoriesEventChannel.receiveAsFlow()

    sealed class CategoriesEvent {
        object NavigateToAddCatScreen : CategoriesEvent()
        data class NavigateToEditCatScreen(val category: Category) : CategoriesEvent()
        data class ShowCatSavedConfirmMessage(val msg: String) : CategoriesEvent()
        data class ShowUndoDeleteCatMessage(val category: Category) : CategoriesEvent()
        data class NavigateToDelCatDialog(val category: Category) : CategoriesEvent()
    }

    //=========FUNS==============================================

    fun onNewCatClick() = viewModelScope.launch {
        categoriesEventChannel.send(CategoriesEvent.NavigateToAddCatScreen)
    }

    fun onCatItemClick(category: Category) = viewModelScope.launch {
        categoriesEventChannel.send(CategoriesEvent.NavigateToEditCatScreen(category))
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_CATEGORY_RESULT_OK -> showCatSavedConfirmSnackbar("Category saved")
            EDIT_CATEGORY_RESULT_OK -> showCatSavedConfirmSnackbar("Category updated")
        }
    }

    fun onCatDelResult(result: Int) {
        Log.d(TAG, "onCatDelResult: called with result $result")
        when (result) {
            CAT_DEL_RESULT_COMPLETE_DELETION -> showCatSavedConfirmSnackbar("Category deleted with all its content")
            CAT_DEL_RESULT_CONTENT_RELOCATED -> showCatSavedConfirmSnackbar("All the content of deleted category has been successfully relocated to another category")
        }
    }

    private fun showCatSavedConfirmSnackbar(msg: String) = viewModelScope.launch {
        categoriesEventChannel.send(CategoriesEvent.ShowCatSavedConfirmMessage(msg))
        Log.d(TAG, "showCatSavedConfirmSnackbar: called with msg $msg")
    }

    fun onDeleteCat(category: Category) = viewModelScope.launch {
//        repo.deleteCategory(category)
//        categoriesEventChannel.send(CategoriesEvent.ShowUndoDeleteCatMessage(category)) // THIS WILL BE REPLACED BY DEL CAT DIALOG
        categoriesEventChannel.send(CategoriesEvent.NavigateToDelCatDialog(category))
    }

    fun onUndoDeleteCat(category: Category) = viewModelScope.launch {
        repo.insertCategory(category)
    }
}




