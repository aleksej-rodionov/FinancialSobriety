package space.rodionov.financialsobriety.ui.transaction

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.core.view.allViews
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.Category
import space.rodionov.financialsobriety.databinding.FragmentTransactionsBinding
import space.rodionov.financialsobriety.ui.transaction.barchart.BarChartTransactionsFragment
import space.rodionov.financialsobriety.ui.transaction.diagram.DiagramTransactionsFragment
import space.rodionov.financialsobriety.ui.transaction.recycler.RecyclerTransactionsFragment
import timber.log.Timber

@AndroidEntryPoint
class TransactionsFragment : Fragment(R.layout.fragment_transactions), CompoundButton.OnCheckedChangeListener {

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

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.catsByType.collect {
                    val categories = it ?: return@collect
                    for (c in categories) {
                        if (!chipGroup.children.map {
                                (it as Chip).text.toString()
                            }.contains(c.catName)) {
                            val chip = Chip(requireContext())
                            chip.text = c.catName
                            chip.isChecked = c.catShown
                            chipGroup.addView(chip)
                        }
                        chipGroup.children.forEach {
                            (it as Chip).setOnCheckedChangeListener(this@TransactionsFragment)
                        }
                    }
                }
            }
        }
    }

    override fun onCheckedChanged(compoundButton: CompoundButton, isChecked: Boolean) {
        viewModel.onCatShownCheckedChanged(compoundButton.text.toString(), isChecked)
        Timber.d("logs oncatshownchanged called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}






