package space.rodionov.financialsobriety.ui.spendandincome

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import space.rodionov.financialsobriety.data.FinDao
import javax.inject.Inject

@HiltViewModel
class SpendViewModel @Inject constructor(
    private val finDao: FinDao
) : ViewModel() {
}