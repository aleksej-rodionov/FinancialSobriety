package space.rodionov.financialsobriety.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FinDao {
    //===========================GET TRANSACTIONS==============================

    @Query("SELECT * FROM spend_table ORDER BY timestamp DESC")
    fun getAllSpends(): Flow<List<Transaction>>

    //===============================GET CATEGORIES==========================

    @Query("SELECT * FROM category_table ORDER BY catId DESC")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM category_table WHERE catType = :type ORDER BY catId DESC")
    fun getCategoriesByType(type: String): Flow<List<Category>>

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















