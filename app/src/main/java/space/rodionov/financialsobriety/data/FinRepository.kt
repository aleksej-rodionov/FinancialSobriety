package space.rodionov.financialsobriety.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FinRepository @Inject constructor(
    private val finDb: FinDatabase
) {
    private val finDao = finDb.finDao()

    fun getAllSpends(): Flow<List<Transaction>> = finDao.getAllSpends()
    fun getAllCategories(): Flow<List<Category>> = finDao.getAllCategories()
    fun getAllDebts(): Flow<List<Debt>> = finDao.getAllDebts()





    //=================================================================

    suspend fun insertSpend(transaction: Transaction) = finDao.insertSpend(transaction)
    suspend fun insertCategory(category: Category) = finDao.insertCategory(category)
    suspend fun insertDebt(debt: Debt) = finDao.insertDebt(debt)

    suspend fun deleteSpend(transaction: Transaction) = finDao.deleteSpend(transaction)
    suspend fun deleteCategory(category: Category) = finDao.deleteCategory(category)
    suspend fun deleteDebt(debt: Debt) = finDao.deleteDebt(debt)

    suspend fun updateSpend(transaction: Transaction) = finDao.updateSpend(transaction)
    suspend fun updateCategory(category: Category) = finDao.updateCategory(category)
    suspend fun updateDebt(debt: Debt) = finDao.updateDebt(debt)

}