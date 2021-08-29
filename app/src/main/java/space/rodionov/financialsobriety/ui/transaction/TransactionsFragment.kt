package space.rodionov.financialsobriety.ui.transaction

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import space.rodionov.financialsobriety.R
import space.rodionov.financialsobriety.data.getColors
import space.rodionov.financialsobriety.databinding.FragmentTransactionsBinding
import space.rodionov.financialsobriety.ui.transaction.barchart.BarChartsFragment
import space.rodionov.financialsobriety.ui.transaction.diagram.DiagramsFragment
import space.rodionov.financialsobriety.ui.transaction.recycler.RecyclerTransactionsFragment
import space.rodionov.financialsobriety.util.exhaustive
import timber.log.Timber

@AndroidEntryPoint
class TransactionsFragment : Fragment(R.layout.fragment_transactions),
    CompoundButton.OnCheckedChangeListener {

    private var _binding: FragmentTransactionsBinding? = null

    private val binding get() = _binding!!
    private lateinit var recyclerTransactionsFragment: RecyclerTransactionsFragment
    private lateinit var diagramsFragment: DiagramsFragment
    private lateinit var barChartsFragment: BarChartsFragment

    private val viewModel: TransactionsViewModel by viewModels()

    private lateinit var viewPager: ViewPager2

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTransactionsBinding.bind(view)

        val listName = resources.getString(R.string.list)
        val diagramName = resources.getString(R.string.diagram)
        val barChartName = resources.getString(R.string.bar_chart)
        val tabTitles = listOf(listName, diagramName, barChartName)

        recyclerTransactionsFragment = RecyclerTransactionsFragment()
        diagramsFragment = DiagramsFragment()
        barChartsFragment = BarChartsFragment()

        val fragments = listOf(recyclerTransactionsFragment, diagramsFragment, barChartsFragment)

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
                    layoutBottomSheet.chipGroup.removeAllViews()
                    for (c in categories) {
                        val chip = Chip(requireContext())
                        chip.text = c.catName
//                        chip.setChipBackgroundColorResource(c.catColor)
                        chip.isChecked = c.catShown
                        layoutBottomSheet.chipGroup.addView(chip)
                        layoutBottomSheet.chipGroup.children.forEach {
                            (it as Chip).setOnCheckedChangeListener(this@TransactionsFragment)
                        }
                    }
                }
            }

            bottomSheetBehavior = BottomSheetBehavior.from(layoutBottomSheet.root)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            bottomSheetBehavior.setHideable(false)

            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                        frameLayout.visibility = View.VISIBLE
                        frameLayout.setOnClickListener {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    } else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                        frameLayout.visibility = View.GONE
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    frameLayout.setVisibility(View.VISIBLE);
                    frameLayout.setAlpha(slideOffset);
                }
            })

            layoutBottomSheet.bottomPooler.setOnClickListener {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                } else if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.transEvent.collect { event ->
                    when (event) {
                        is TransactionsViewModel.TransEvent.ShowInvalidCatNumberMsg -> {
                            Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                        }
                    }.exhaustive
                }
            }
        }
    }

    override fun onCheckedChanged(compoundButton: CompoundButton, isChecked: Boolean) {
        if (isChecked) {
            viewModel.onCatShownCheckedChanged(compoundButton.text.toString(), isChecked)
        } else {
            if (binding.layoutBottomSheet.chipGroup.checkedChipIds.size < 1) {
                compoundButton.isChecked = true
                viewModel.showInvalidAmountOfCatsMsg()
            } else {
                viewModel.onCatShownCheckedChanged(compoundButton.text.toString(), isChecked)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}






