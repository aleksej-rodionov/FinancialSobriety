package space.rodionov.financialsobriety.ui.categories

import androidx.lifecycle.SavedStateHandle
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
import space.rodionov.financialsobriety.data.TransactionType
import space.rodionov.financialsobriety.ui.CAT_DEL_RESULT_COMPLETE_DELETION
import space.rodionov.financialsobriety.ui.CAT_DEL_RESULT_CONTENT_RELOCATED
import javax.inject.Inject

@HiltViewModel
class DeleteCategoryViewModel @Inject constructor(
    private val repo: FinRepository,
    private val state: SavedStateHandle
) : ViewModel() {
    var category = state.get<Category>("category")
    var categoryName = state.get<String>("categoryName") ?: category?.catName ?: ""
    var categoryType = state.get<String>("categoryType") ?: category?.catType?.name ?: "Outcome"

    var alternativeCategory = state.get<Category>("alterCategory")
        set(value) {
            field = value
            state.set("alterCategory", value)
        }

    fun getCategoriesByTypeExcept(type: TransactionType, catName: String) =
        repo.getCategoriesByTypeExcept(type, catName)
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    //=========================EVENT CHANNEL=====================================================

    private val deleteCatEventChannel = Channel<DeleteCategoryEvent>()
    val deleteCatEvent = deleteCatEventChannel.receiveAsFlow()

    sealed class DeleteCategoryEvent {
        data class NavigateToDeleteAllTransByCat(
            val delCat: Category,
            val alterCat: Category?,
            val resultCode: Int
        ) : DeleteCategoryEvent()

//        data class NavigateBackWithDeletionResult(val result: Int) : DeleteCategoryEvent()
    }


    //========================================METHODS===============================================

    fun onAlterCatChosen(alterCategory: Category) = viewModelScope.launch {
        category?.let {
            deleteCatEventChannel.send(
                DeleteCategoryEvent.NavigateToDeleteAllTransByCat(
                    category!!, alterCategory, CAT_DEL_RESULT_CONTENT_RELOCATED
                )
            )
        }
    }

    fun deleteTransactionsByCat() = viewModelScope.launch {
        category?.let {
            deleteCatEventChannel.send(
                DeleteCategoryEvent.NavigateToDeleteAllTransByCat(
                    it, null, CAT_DEL_RESULT_COMPLETE_DELETION
                )
            )
        }
    }

//    fun onConfirmDelResult(result: Int) {
//        when (result) {
//            CONFIRM_DELETE_ALL_TRANS_FROM_CAT -> {
//                category = null
//                onConfirmDeletion(CAT_DEL_RESULT_COMPLETE_DELETION)
//            }
//        }
//    }

//    private fun onConfirmDeletion(result: Int) = viewModelScope.launch {
//        deleteCatEventChannel.send(DeleteCategoryEvent.NavigateBackWithDeletionResult(result))
//    }

    fun onUndoDeleteCat() = viewModelScope.launch {
        category?.let {
            repo.deleteCategory(it)
            repo.insertCategory(it)
        }
    }
}





