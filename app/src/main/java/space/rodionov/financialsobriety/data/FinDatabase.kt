package space.rodionov.financialsobriety.data

import androidx.room.Database

@Database(entities = [Spend::class, Category::class], version = 1, exportSchema = false)
abstract class FinDatabase {

    abstract fun finDao() : FinDao

}