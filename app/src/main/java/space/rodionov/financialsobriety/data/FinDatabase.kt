package space.rodionov.financialsobriety.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Spend::class, Category::class], version = 1, exportSchema = false)
abstract class FinDatabase : RoomDatabase() {

    abstract fun finDao() : FinDao

}