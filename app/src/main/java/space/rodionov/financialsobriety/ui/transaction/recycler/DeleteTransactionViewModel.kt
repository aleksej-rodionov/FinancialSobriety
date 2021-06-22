package space.rodionov.financialsobriety.ui.transaction.recycler

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.FinRepository
import space.rodionov.financialsobriety.data.Spend
import space.rodionov.financialsobriety.di.ApplicationScope
import javax.inject.Inject

@HiltViewModel
class DeleteTransactionViewModel @Inject constructor(
    private val repo: FinRepository,
    @ApplicationScope private val applicationScope: CoroutineScope,
    private val state: SavedStateHandle
) : ViewModel() {

    val spend = state.get<Spend>("spend")

    fun onConfirmClick(spend: Spend) = applicationScope.launch {
        repo.deleteSpend(spend)
    }

}