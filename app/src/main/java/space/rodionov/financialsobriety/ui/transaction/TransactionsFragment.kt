package space.rodionov.financialsobriety.ui.transaction

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.databinding.FragmentTransactionsBinding
import space.rodionov.financialsobriety.ui.transaction.barchart.BarChartTransactionsFragment
import space.rodionov.financialsobriety.ui.transaction.diagram.DiagramTransactionsFragment
import space.rodionov.financialsobriety.ui.transaction.recycler.RecyclerTransactionsFragment
import timber.log.Timber

@AndroidEntryPoint
class TransactionsFragment : Fragment(R.layout.fragment_transactions) {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerTransactionsFragment: RecyclerTransactionsFragment
    private lateinit var diagramTransactionsFragment: DiagramTransactionsFragment
    private lateinit var barChartTransactionsFragment: BarChartTransactionsFragment

    private val viewModel: TransactionsViewModel by viewModels()

    private lateinit var viewPager: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTransactionsBinding.bind(view)

        val tabTitles = listOf("Список", "Диаграммы", "Графики")

        recyclerTransactionsFragment = RecyclerTransactionsFragment()
        diagramTransactionsFragment = DiagramTransactionsFragment()
        barChartTransactionsFragment = BarChartTransactionsFragment()

        val fragments = listOf(recyclerTransactionsFragment, diagramTransactionsFragment, barChartTransactionsFragment)

        val adapter = ViewPagerAdapter(this, fragments)

        binding.apply {
            this@TransactionsFragment.viewPager = viewPagerView
            this@TransactionsFragment.viewPager.adapter = adapter

            TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
                tab.text = tabTitles[pos]
            }.attach()

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}






