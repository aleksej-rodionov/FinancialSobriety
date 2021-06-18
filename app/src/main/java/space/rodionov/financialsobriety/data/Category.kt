package space.rodionov.financialsobriety.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "category_table")
@Parcelize
class Category(
    val catName: String,
    @PrimaryKey(autoGenerate = true) val catId: Int = 0
) : Parcelable {
}