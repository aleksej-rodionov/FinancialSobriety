package space.rodionov.financialsobriety.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FinDao {
    //===========================TRANSACTIONS==============================

    @Query("SELECT * FROM spend_table ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM spend_table WHERE type = :type ORDER BY timestamp DESC")
    fun getTransactionsByType(type: String): Flow<List<Transaction>>

    @Query("DELETE FROM spend_table WHERE catName = :catName")
    suspend fun deleteTransactionsByCat(catName: String)

    @Query("UPDATE spend_table SET catName = :catNameNew WHERE catName = :catNameOld")
    suspend fun moveTransactionsFromCatToCat(catNameOld: String, catNameNew: String)



    //============================CATEGORIES WITH TRANSACTIONS==============

    @androidx.room.Transaction
    @Query("SELECT * FROM category_table WHERE catName = :catName")
    fun getCatWithTransactions(catName: String) : Flow<List<CategoryWithTransactions>>

    @androidx.room.Transaction
    @Query("SELECT * FROM category_table")
    fun getAllCategoriesWithTransactions() : Flow<List<CategoryWithTransactions>>

    @androidx.room.Transaction
    @Query("SELECT * FROM category_table WHERE catType = :type AND catShown = 1 ORDER BY catName")
    fun getCatsWithTransactionsByType(type: String): Flow<List<CategoryWithTransactions>>

    @androidx.room.Transaction
    @Query("SELECT * FROM category_table WHERE catType = :type ORDER BY catName")
    fun getAllCatsWithTransactionsByType(type: String): Flow<List<CategoryWithTransactions>>

    //===============================CATEGORIES==========================

    @Query("SELECT * FROM category_table ORDER BY catName")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM category_table WHERE catType = :type ORDER BY catName")
    fun getCategoriesByType(type: String): Flow<List<Category>>

    @Query("SELECT * FROM category_table WHERE catType = :type AND catName != :catName ORDER BY catName")
    fun getCategoriesByTypeExcept(type: String, catName: String): Flow<List<Category>>

    @Query("SELECT COUNT(*) FROM category_table WHERE catType = :type")
    suspend fun getCatNumberByType(type: String): Int

    @Query("UPDATE category_table SET catShown = :catShown WHERE catName = :catName")
    suspend fun changeCatShown(catName: String, catShown: Boolean)

    @Query("SELECT * FROM category_table WHERE catName = :catName")
    suspend fun getCatByName(catName: String) : Category



    //==================================GET DEBTS============================

    @Query("SELECT * FROM debt_table ORDER BY debtId DESC")
    fun getAllDebts(): Flow<List<Debt>>


    //============================STANDARD FUNS=========================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpend(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: Debt)

    @Delete
    suspend fun deleteSpend(transaction: Transaction)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Delete
    suspend fun deleteDebt(debt: Debt)

    @Update
    suspend fun updateSpend(transaction: Transaction)

    @Update
    suspend fun updateCategory(category: Category)

    @Update
    suspend fun updateDebt(debt: Debt)
}















