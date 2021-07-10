package space.rodionov.financialsobriety.ui.debt

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import space.rodionov.financialsobriety.data.FinRepository
import javax.inject.Inject

@HiltViewModel
class DebtsViewModel @Inject constructor(
    val repo: FinRepository
) : ViewModel() {


}