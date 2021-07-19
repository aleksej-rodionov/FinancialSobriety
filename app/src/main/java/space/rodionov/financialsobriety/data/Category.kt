package space.rodionov.financialsobriety.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "category_table")
@Parcelize
data class Category(
    @PrimaryKey(autoGenerate = false)
    val catName: String,
    val catType: TransactionType,
) : Parcelable {

    override fun toString(): String {
        return "$catName ($catType)"
    }
}

enum class TransactionType {
    INCOME, OUTCOME
}