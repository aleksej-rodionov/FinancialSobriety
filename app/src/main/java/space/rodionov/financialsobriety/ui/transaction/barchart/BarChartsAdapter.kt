package space.rodionov.financialsobriety.ui.transaction.barchart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import space.rodionov.financialsobriety.data.CategoryWithTransactions
import space.rodionov.financialsobriety.data.Month
import space.rodionov.financialsobriety.data.Year
import space.rodionov.financialsobriety.databinding.ItemBarChartBinding

class BarChartsAdapter(
    private val allCatsWithTransactionsFlow: StateFlow<List<CategoryWithTransactions>?>,
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
//                barChart.setupBarChart()
                TODO()
            }
        }
    }

    class BarChartComparator : DiffUtil.ItemCallback<Year>() {
        override fun areItemsTheSame(oldItem: Year, newItem: Year) =
            oldItem.yyyy == newItem.yyyy

        override fun areContentsTheSame(oldItem: Year, newItem: Year) = oldItem == newItem
    }

    //====================================FUNCTIONS==========================================


}





