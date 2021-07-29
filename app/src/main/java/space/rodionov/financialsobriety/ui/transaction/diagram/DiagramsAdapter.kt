package space.rodionov.financialsobriety.ui.transaction.diagram

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import space.rodionov.financialsobriety.data.CategoryWithTransactions
import space.rodionov.financialsobriety.data.Month
import space.rodionov.financialsobriety.databinding.ItemDiagramBinding
import timber.log.Timber

class DiagramsAdapter(
    private val allCatsWithTransactionsFlow: StateFlow<List<CategoryWithTransactions>?>,
    private val scope: CoroutineScope
) : ListAdapter<Month, DiagramsAdapter.DiagramViewHolder>(DiagramsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagramViewHolder {
        val binding = ItemDiagramBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiagramViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiagramViewHolder, position: Int) {
        val curMonth = getItem(position)
        holder.bind(curMonth)
    }

    inner class DiagramViewHolder(private val binding: ItemDiagramBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(month: Month) {
            binding.apply {
                pieChart.setupPieChart()
                scope.launch {
                    allCatsWithTransactionsFlow.collect {
                        val allCatsWithTransactions = it ?: return@collect
                        pieChart.loadPieChartData(createMonthPieEntryList(allCatsWithTransactions, month))
                    }
                }
            }
        }
    }

    class DiagramsComparator : DiffUtil.ItemCallback<Month>() {
        override fun areItemsTheSame(oldItem: Month, newItem: Month) =
            oldItem.mmSlashYear == newItem.mmSlashYear

        override fun areContentsTheSame(oldItem: Month, newItem: Month) = oldItem == newItem
    }

    //=======================UNIQUE FUNS==========================================

    private fun createMonthPieEntryList(
        allCatsWithTransactions: List<CategoryWithTransactions>,
        month: Month
    ): List<PieEntry> {
        val pieEntries = mutableListOf<PieEntry>()
        for (cwt in allCatsWithTransactions) {
            val monthTransactionsByCat = month.getTransactionsOfMonth(cwt.transactions)
            if (!monthTransactionsByCat.isNullOrEmpty()) {
                val monthSumByCat = monthTransactionsByCat.map {
                    it.sum
                }.sum()
                pieEntries.add(PieEntry(monthSumByCat, cwt.category.catName))
            }
        }
        Timber.d("LOGS Entrys: $pieEntries")
        return pieEntries
    }

    //=========================FUNS FOR PIECHART==============================

    private fun PieChart.setupPieChart() {
        this.isDrawHoleEnabled
        this.setUsePercentValues(true)
        this.setEntryLabelTextSize(12f)
        this.setEntryLabelColor(Color.BLACK)
        this.centerText = "Fuck you"
        this.setCenterTextSize(24f)
        !this.description.isEnabled

        val legend = this.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)
        legend.isEnabled
    }

    private fun PieChart.loadPieChartData(entries: List<PieEntry>) {
        val colors = mutableListOf<Int>()
        for (i in ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(i)
        }
        for (i in ColorTemplate.LIBERTY_COLORS) {
            colors.add(i)
        }

        val dataset = PieDataSet(entries, "Expense Category")
        dataset.setColors(colors)

        val data = PieData(dataset)
        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter(this))
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.BLACK)

        this.data = data
        this.invalidate()
    }
}