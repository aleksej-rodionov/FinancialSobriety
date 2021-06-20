package space.rodionov.financialsobriety.ui.transaction

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter (
        val fragmentParent: Fragment,
        val fragments: List<Fragment>
    ) : FragmentStateAdapter(fragmentParent) {

        override fun getItemCount(): Int {
            return fragments.size
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                1 -> fragments[1]
                2 -> fragments[2]
                3 -> fragments[3]
                else -> fragments[0]
            }
        }
    }