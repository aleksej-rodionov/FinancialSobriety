package space.rodionov.financialsobriety.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FinDao {

//    @Query("SELECT * FROM spend_table WHERE date")
//    fun getSpendsOfDate(date: String) : Flow<List<Spend>>









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















