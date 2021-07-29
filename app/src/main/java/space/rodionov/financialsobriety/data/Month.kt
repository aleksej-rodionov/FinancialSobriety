package space.rodionov.financialsobriety.data

import java.text.SimpleDateFormat
import java.util.*

data class Month(
    val mmSlashYear: String
) {
    fun monthStart(): Long {
        val startCal = Calendar.getInstance()
        startCal.time = monthYearSdf.parse(mmSlashYear)
        return startCal.timeInMillis
    }

    fun monthEnd(): Long {
        val endCal = Calendar.getInstance()
        endCal.time = monthYearSdf.parse(mmSlashYear)
        val endYear = endCal.get(Calendar.YEAR)
        val endMonth = endCal.get(Calendar.MONTH)
        endCal.set(endYear, endMonth, endCal.getActualMaximum(Calendar.DAY_OF_MONTH))
        return endCal.timeInMillis + 86399999L
    }

    fun getTransactionsOfMonth(allTransactions: List<Transaction>) : List<Transaction> {
        val list = mutableListOf<Transaction>()
        for (t in allTransactions) {
            if (t.timestamp >= monthStart() && t.timestamp <= monthEnd()) list.add(t)
        }
        return list
    }

    override fun toString(): String {
        val cal = Calendar.getInstance()
        return monthFullSdf.format(monthYearSdf.parse(mmSlashYear))
    }

    fun toAbbrString(): String {
        return monthAbbrSdf.format(monthYearSdf.parse(mmSlashYear))
    }
}

val monthNames = arrayOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")

val monthYearSdf = SimpleDateFormat("MM/yyyy", Locale.getDefault())
val yearSdf = SimpleDateFormat("yyyy", Locale.getDefault())
val monthNameSdf = SimpleDateFormat("MMMM", Locale.getDefault());
val monthAbbrSdf = SimpleDateFormat("MMM yyyy", Locale.getDefault());
val monthFullSdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault());