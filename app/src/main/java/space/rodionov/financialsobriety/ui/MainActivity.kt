package space.rodionov.financialsobriety.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import space.rodionov.financialsobriety.R
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        bottom_nav.setupWithNavController(navController)

        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean { // че эт
        return navController.navigateUp() || super.onSupportNavigateUp()
    }



}

const val ADD_TRANSACTION_RESULT_OK = Activity.RESULT_FIRST_USER
const val EDIT_TRANSACTION_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val ADD_CATEGORY_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_CATEGORY_RESULT_OK = Activity.RESULT_FIRST_USER + 3
const val ADD_DEBT_RESULT_OK = Activity.RESULT_FIRST_USER + 4
const val EDIT_DEBT_RESULT_OK = Activity.RESULT_FIRST_USER + 5
const val CONFIRM_DELETE_ALL_TRANS_FROM_CAT = Activity.RESULT_FIRST_USER + 6
const val CAT_DEL_RESULT_COMPLETE_DELETION = Activity.RESULT_FIRST_USER + 7
const val CAT_DEL_RESULT_CONTENT_RELOCATED = Activity.RESULT_FIRST_USER + 8



