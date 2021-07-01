package space.rodionov.financialsobriety.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FinDao {

    @Query("SELECT * FROM spend_table ORDER BY timestamp DESC")
    fun getAllSpends(): Flow<List<Spend>>

    @Query("SELECT * FROM category_table ORDER BY catId DESC")
    fun getAllCategories(): Flow<List<Category>>





    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpend(spend: Spend)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Delete
    suspend fun deleteSpend(spend: Spend)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Update
    suspend fun updateSpend(spend: Spend)

    @Update
    suspend fun updateCategory(category: Category)
}















