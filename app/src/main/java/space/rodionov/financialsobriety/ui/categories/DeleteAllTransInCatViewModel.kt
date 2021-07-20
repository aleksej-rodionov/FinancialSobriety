package space.rodionov.financialsobriety.ui.categories

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.data.FinRepository
import space.rodionov.financialsobriety.di.ApplicationScope
import space.rodionov.financialsobriety.ui.CAT_DEL_RESULT_COMPLETE_DELETION
import space.rodionov.financialsobriety.ui.CAT_DEL_RESULT_CONTENT_RELOCATED
import space.rodionov.financialsobriety.ui.CONFIRM_DELETE_ALL_TRANS_FROM_CAT
import javax.inject.Inject

@HiltViewModel
class DeleteAllTransInCatViewModel @Inject constructor(
    private val repo: FinRepository,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val state: SavedStateHandle
) : ViewModel() {
    private val delCategory = state.get<Category>("delCategory")
    val delCatName = state.get<String>("delCatName") ?: delCategory?.catName ?: ""
    val result = state.get<Int>("result") ?: CAT_DEL_RESULT_COMPLETE_DELETION
    private val alterCategory = state.get<Category>("alterCategory")
    val alterCatName = state.get<String>("alterCatName") ?: alterCategory?.catName ?: ""




    fun onConfirmClick() = applicationScope.launch {
        when (result) {
            CAT_DEL_RESULT_COMPLETE_DELETION -> {
                if (delCatName.isNotBlank()) repo.deleteTransactionsByCat(delCatName)
                delCategory?.let { repo.deleteCategory(delCategory) }
            }
            CAT_DEL_RESULT_CONTENT_RELOCATED -> {
                repo.moveContentFromCatToCat(delCatName, alterCatName)
                delCategory?.let { repo.deleteCategory(delCategory) }
            }
        }
    }
}