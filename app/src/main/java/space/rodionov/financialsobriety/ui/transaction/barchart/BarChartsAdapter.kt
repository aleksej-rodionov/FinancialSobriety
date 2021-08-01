package space.rodionov.financialsobriety.ui.transaction.barchart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.CategoryWithTransactions
import space.rodionov.financialsobriety.data.Month
import space.rodionov.financialsobriety.data.Transaction
import space.rodionov.financialsobriety.data.Year
import space.rodionov.financialsobriety.databinding.ItemBarChartBinding
import timber.log.Timber
import java.util.ArrayList

class BarChartsAdapter(
    private val catsWithTransactionsFlow: StateFlow<List<CategoryWithTransactions>?>,
    private val scope: CoroutineScope
) : ListAdapter<Year, BarChartsAdapter.BarChartViewHolder>(BarChartComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarChartViewHolder {
        val binding = ItemBarChartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BarChartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BarChartViewHolder, position: Int) {
        val curYear = getItem(position)
        holder.bind(curYear)
    }

    inner class BarChartViewHolder(private val binding: ItemBarChartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(year: Year) {
            binding.apply {
                barChart.setupBarChart()
                scope.launch {
                    catsWithTransactionsFlow.collect {
                        val catsWithTransactions = it ?: return@collect
                        val barEntries = createYearBarEntryList(catsWithTransactions, year)
                        barChart.data?.clearValues()
                        barChart.loadBarChartData(barEntries, catsWithTransactions)
                    }
                }
            }
        }
    }

    class BarChartComparator : DiffUtil.ItemCallback<Year>() {
        override fun areItemsTheSame(oldItem: Year, newItem: Year) =
            oldItem.yyyy == newItem.yyyy

        override fun areContentsTheSame(oldItem: Year, newItem: Year) = oldItem == newItem
    }

//==========================CREATING ENTRIES FUNS===================================

    fun createYearBarEntryList(
        categoriesWithTransactions: List<CategoryWithTransactions>,
        year: Year
    ): List<BarEntry> {
        val monthsOfYear = year.getMonthsOfYear()
        val barEntries = mutableListOf<BarEntry>()

        monthsOfYear.forEachIndexed { index, month ->
            val sumsByCats = mutableListOf<Float>()
            for (cwt in categoriesWithTransactions) {
                val sumByCat = month.getSumOfTransactionsInMonth(cwt.transactions)
                sumsByCats.add(sumByCat)
            }
            barEntries.add(BarEntry((index+1).toFloat(), sumsByCats.toFloatArray()))
        }
        return barEntries
    }

    fun Month.getSumOfTransactionsInMonth(transactions: List<Transaction>) : Float {
        val transactionsOfMonth = this.getTransactionsOfMonth(transactions)
        val sumOfMonth = transactionsOfMonth.map {
            it.sum
        }.sum()
        return sumOfMonth
    }

    //====================================BARCHART FUNCTIONS==========================================

    private fun BarChart.setupBarChart() {

//        barChart.getAxisLeft().setAxisMaximum(100000f);      //Maximum value of Y axis
        this.axisLeft.axisMinimum = 0f;      //Y-axis minimum value
        //The number of Y-axis coordinates The second parameter is generally false true to indicate that the number of labels is mandatory, which may cause problems such as incomplete display of X-axis coordinates.
//        barChart.getAxisLeft().setLabelCount(18,false);
        this.xAxis.axisMaximum = 12f;      //Maximum value of X axis
        this.xAxis.axisMinimum = 0f;      //X-axis minimum value
        //Number of X-axis coordinates The second parameter is generally filled with false true, which means that the number of labels is mandatory, which may cause problems such as incomplete display of X-axis coordinates.
        this.xAxis.setLabelCount(12, false);
        this.description.setEnabled(false);        //A string of English letters in the lower right corner is not displayed
        this.xAxis.position =
            XAxis.XAxisPosition.TOP;      //The position of the X axis is set to down, the default is up
        this.axisRight.isEnabled = false;

//        val l = this.getLegend();
//        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
//        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
//        l.orientation = Legend.LegendOrientation.HORIZONTAL
//        l.setDrawInside(false)
//        l.formSize = 8f
//        l.formToTextSpace = 4f
//        l.xEntrySpace = 6f

//        this.setDrawGridBackground(false)
    }

    private fun BarChart.loadBarChartData(
        entries: List<BarEntry>,
        categories: List<CategoryWithTransactions>
    ) {
//        val colorList = mutableListOf<Int>()
//        for (i in ColorTemplate.VORDIPLOM_COLORS) {
//            colorList.add(i)
//        }
//        for (i in ColorTemplate.LIBERTY_COLORS) {
//            colorList.add(i)
//        }
//        while (colorList.size < categories.size) {
//            colorList.addAll(colorList)
//        }
//        val colors = IntArray(categories.size)
//        Timber.d("logs categories.size = ${categories.size}")
//        System.arraycopy(colorList.toIntArray(), 0, colors, 0, categories.size)
//        Timber.d("logs colors.size = ${colors.size}")

        val catNames = categories.map {
            it.category.catName
        }.toTypedArray()
        Timber.d("logs catNames.size = ${catNames.size}")

        val catColors = categories.map {
            it.category.catColor
        }
        Timber.d("logs catColors.size = ${catColors.size}")

        if (this.getData() != null &&
            this.getData().getDataSetCount() > 0
        ) {
            val barDataSet = this.getData().getDataSetByIndex(0) as BarDataSet
            barDataSet.values = entries
            this.data.notifyDataChanged()
            this.notifyDataSetChanged()
        } else {
            val barDataSet = BarDataSet(entries, "In year")
            barDataSet.setDrawIcons(false)
//            barDataSet.colors = getColors(catNames.size, colorList.toIntArray())?.toList()
            barDataSet.colors = catColors

            barDataSet.stackLabels = catNames

            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(barDataSet)

            this.data = null
            val barData = BarData(dataSets)

            this.data = barData
            barData.barWidth = 0.9f;//The width of the column
        }

        this.setFitBars(true);
        this.invalidate()
    }

    private fun getColors(size: Int, colorList: IntArray): IntArray {

        Timber.d("logs getColors(catNamesSize = $size)")
        // have as many colors as stack-values per entry
        val colors = IntArray(size)
        System.arraycopy(colorList, 0, colors, 0, size)
        return colors
    }
}





