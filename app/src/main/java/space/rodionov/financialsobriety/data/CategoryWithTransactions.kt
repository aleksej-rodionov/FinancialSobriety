package space.rodionov.financialsobriety.data

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithTransactions(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "catName",
        entityColumn = "catName"
    )
    val transactions: List<Transaction>
)