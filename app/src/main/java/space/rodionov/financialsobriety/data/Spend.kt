package space.rodionov.financialsobriety.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Entity(tableName = "spend_table")
@Parcelize
data class Spend(
    var sum: Float,
    var category: Category?,
    var timeStamp: Long = 0L,
    var comment: String?,
//    val date: String? = DateFormat.getTimeInstance().format(timeStamp),
    @PrimaryKey(autoGenerate = true) val id: Int
) : Parcelable {
    val dateFormatted: String
        get() = DateFormat.getTimeInstance().format(timeStamp)
}