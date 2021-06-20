package space.rodionov.financialsobriety.ui.transaction.recycler

import androidx.hilt.Assisted
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import space.rodionov.financialsobriety.data.FinRepository
import javax.inject.Inject

@HiltViewModel
class RecyclerTransactionsViewModel @Inject constructor(
    private val repo: FinRepository,
//private val preferencesRepo: PreferencesRepository,
    private val state: SavedStateHandle
) : ViewModel() {


}











