package space.rodionov.financialsobriety.ui.transaction

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.databinding.FragmentTransactionsBinding
import space.rodionov.financialsobriety.ui.transaction.calendar.CalendarTransactionsFragment
import space.rodionov.financialsobriety.ui.transaction.diagram.DiagramTransactionsFragment
import space.rodionov.financialsobriety.ui.transaction.recycler.RecyclerTransactionsFragment

@AndroidEntryPoint
class TransactionsFragment : Fragment(R.layout.fragment_transactions) {
//    private val viewModel: TransactionsViewModel by viewModels()

    private lateinit var binding: FragmentTransactionsBinding
    private lateinit var recyclerTransactionsFragment: RecyclerTransactionsFragment
    private lateinit var calendarTransactionsFragment: CalendarTransactionsFragment
    private lateinit var diagramTransactionsFragment: DiagramTransactionsFragment

    private lateinit var viewPager: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTransactionsBinding.bind(view)

        val tabTitles = listOf("Список", "Календарь", "Графики")

        recyclerTransactionsFragment = RecyclerTransactionsFragment()
        calendarTransactionsFragment = CalendarTransactionsFragment()
        diagramTransactionsFragment = DiagramTransactionsFragment()

        val fragments = listOf(recyclerTransactionsFragment, calendarTransactionsFragment, diagramTransactionsFragment)

        val adapter = ViewPagerAdapter(this, fragments)

        binding.apply {
            this@TransactionsFragment.viewPager = viewPagerView
            this@TransactionsFragment.viewPager.adapter = adapter

            TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
                tab.text = tabTitles[pos]
            }.attach()

        }

    }

}







