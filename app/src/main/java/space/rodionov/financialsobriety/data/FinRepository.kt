package space.rodionov.financialsobriety.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FinRepository @Inject constructor(
    private val finDb: FinDatabase
) {
    private val finDao = finDb.finDao()

    //===========================TRANSACTIONS==============================

    fun getAllTransactions(): Flow<List<Transaction>> = finDao.getAllTransactions()
    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> = finDao.getTransactionsByType(type.name)
    suspend fun deleteTransactionsByCat(catName: String) = finDao.deleteTransactionsByCat(catName)

    //============================CATEGORIES WITH TRANSACTIONS==============

    fun getAllCategoriesWithTransactions(): Flow<List<CategoryWithTransactions>> = finDao.getAllCategoriesWithTransactions()
fun getCatsWithTransactionsByType(type: TransactionType) : Flow<List<CategoryWithTransactions>> =
    finDao.getCatsWithTransactionsByType(type.name)

    //=========================CATEGORIES===================================

    fun getAllCategories(): Flow<List<Category>> = finDao.getAllCategories()
    fun getCategoriesByType(type: TransactionType): Flow<List<Category>> = finDao.getCategoriesByType(type.name)
    fun getCategoriesByTypeExcept(type: TransactionType, catName: String): Flow<List<Category>> = finDao.getCategoriesByTypeExcept(type.name, catName)
    suspend fun moveContentFromCatToCat(catNameOld: String, catNameNew: String) = finDao.moveTransactionsFromCatToCat(catNameOld, catNameNew)
    suspend fun getCategoriesByTypeSus(type: TransactionType): List<Category> = finDao.getCategoriesByTypeSus(type.name)

    //=========================DEBTS===================================

    fun getAllDebts(): Flow<List<Debt>> = finDao.getAllDebts()


    //========================STANDARD FUNS===============================

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