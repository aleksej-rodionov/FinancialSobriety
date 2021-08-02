package space.rodionov.financialsobriety.ui.categories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.data.FinRepository
import space.rodionov.financialsobriety.data.TransactionType
import space.rodionov.financialsobriety.di.ApplicationScope
import space.rodionov.financialsobriety.ui.CAT_DEL_RESULT_COMPLETE_DELETION
import space.rodionov.financialsobriety.ui.CAT_DEL_RESULT_CONTENT_RELOCATED
import javax.inject.Inject

@HiltViewModel
class DeleteCategoryViewModel @Inject constructor(
    private val repo: FinRepository,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val state: SavedStateHandle
) : ViewModel() {
    var category = state.get<Category>("category")
    var categoryName = state.get<String>("categoryName") ?: category?.catName ?: ""
    var categoryType = state.get<String>("categoryType") ?: category?.catType?.name ?: "Outcome"

    fun getCategoriesByTypeExcept(type: TransactionType, catName: String) =
        repo.getCategoriesByTypeExcept(type, catName)
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    //=========================EVENT CHANNEL=====================================================

    private val deleteCatEventChannel = Channel<DeleteCategoryEvent>()
    val deleteCatEvent = deleteCatEventChannel.receiveAsFlow()

    sealed class DeleteCategoryEvent {
        data class NavigateBackWithDeletionResult(val result: Int) : DeleteCategoryEvent()
    }

    //========================================METHODS===============================================

    fun onAlterCatChosen(alterCategory: Category) = applicationScope.launch {
        category?.let {
            repo.moveContentFromCatToCat(categoryName, alterCategory.catName)
            navigateBackWithResult(CAT_DEL_RESULT_CONTENT_RELOCATED)
        }
    }

    fun deleteTransactionsByCat() = applicationScope.launch {
        category?.let {
            repo.deleteTransactionsByCat(it.catName)
            navigateBackWithResult(CAT_DEL_RESULT_COMPLETE_DELETION)
        }
    }

    private fun navigateBackWithResult(result: Int) = viewModelScope.launch {
        deleteCatEventChannel.send(DeleteCategoryEvent.NavigateBackWithDeletionResult(result))
    }

    fun onUndoDeleteCat() = viewModelScope.launch {
        category?.let {
            repo.insertCategory(it)
        }
    }
}





