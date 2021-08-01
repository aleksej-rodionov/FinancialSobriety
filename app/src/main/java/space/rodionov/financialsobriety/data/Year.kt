package space.rodionov.financialsobriety.data

data class Year(
    val yyyy: String
) {

    fun getMonthsOfYear() : List<Month> {
        val monthList = mutableListOf<Month>()

        for (m in 1..12) {
            var mString = m.toString()
            if (mString.length < 2) mString = "0$mString"
            val month = Month("$mString/$yyyy")
            monthList.add(month)
        }
        return monthList
    }

    override fun toString(): String {
        return yyyy
    }
}