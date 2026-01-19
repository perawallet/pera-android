/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.android

import android.graphics.Rect
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.forEach
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import com.algorand.android.CoreMainViewModel.ViewEvent.InitializeCoreManagers
import com.algorand.android.CoreMainViewModel.ViewEvent.InitializeHomeNavigation
import com.algorand.android.CoreMainViewModel.ViewEvent.InitializeLoginNavigation
import com.algorand.android.CoreMainViewModel.ViewEvent.InitializeMainActivity
import com.algorand.android.core.BaseActivity
import com.algorand.android.core.BottomNavigationMenuViewModel
import com.algorand.android.core.bottomnav.model.BottomNavMenuItem
import com.algorand.android.customviews.toolbar.CustomToolbar
import com.algorand.android.databinding.ActivityMainBinding
import com.algorand.android.models.Node
import com.algorand.android.models.StatusBarConfiguration
import com.algorand.android.modules.autolockmanager.ui.AutoLockManager
import com.algorand.android.notification.NotificationPermissionManager
import com.algorand.android.notification.PeraNotificationManager
import com.algorand.android.utils.TESTNET_NETWORK_SLUG
import com.algorand.android.utils.coremanager.ParityManager
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.extensions.hide
import com.algorand.android.utils.extensions.show
import com.algorand.android.utils.navigateBackSafe
import com.algorand.android.utils.navigateSafe
import com.algorand.android.utils.setupWithNavController
import com.algorand.android.utils.showDarkStatusBarIcons
import com.algorand.android.utils.showLightStatusBarIcons
import com.algorand.android.utils.viewbinding.viewBinding
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

abstract class CoreMainActivity : BaseActivity() {

    @Inject
    lateinit var peraNotificationManager: PeraNotificationManager

    @Inject
    lateinit var parityManager: ParityManager

    @Inject
    lateinit var autoLockManager: AutoLockManager

    @Inject
    lateinit var notificationPermissionManager: NotificationPermissionManager

    lateinit var navController: NavController

    private val coreMainViewModel: CoreMainViewModel by viewModels()

    private val bottomNavMenuViewModel: BottomNavigationMenuViewModel by viewModels()

    var isBottomBarNavigationVisible: Boolean by Delegates.observable(false) { _, oldValue, newValue ->
        if (newValue != oldValue) {
            binding.bottomNavigationView.isVisible = newValue
            setWindowInsetsForSystemBars()
        }
    }

    var statusBarConfiguration: StatusBarConfiguration by Delegates.observable(
        StatusBarConfiguration()
    ) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            handleStatusBarChanges(newValue)
            handleStatusBarIconColorChanges(oldValue, newValue)
        }
    }

    private val viewEventCollector: suspend (CoreMainViewModel.ViewEvent) -> Unit = { event ->
        when (event) {
            is InitializeCoreManagers -> initializeCoreManagers()
            is InitializeHomeNavigation -> startNavigation(R.id.homeNavigation)
            is InitializeLoginNavigation -> startNavigation(R.id.initialRegisterIntroNavigation)
            is InitializeMainActivity -> initializeMainActivity(event.savedInstanceState)
        }
    }

    private val bottomNavMenuEventCollector: suspend (BottomNavigationMenuViewModel.ViewEvent) -> Unit = { event ->
        when (event) {
            is BottomNavigationMenuViewModel.ViewEvent.SetItemEnabled -> {
                binding.bottomNavigationView.menu.forEach { menuItem ->
                    if (menuItem.itemId == event.itemId) menuItem.isEnabled = event.enabled
                }
            }
        }
    }

    protected val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::inflate)

    private var isConnectedToTestNet: Boolean by Delegates.observable(false) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            handleStatusBarChanges(statusBarConfiguration)
            handleNavigationButtonsForChosenNetwork()
        }
    }

    abstract fun onMenuItemClicked(item: MenuItem)

    abstract fun observeAutoLockManager()

    abstract fun initializeMainActivity(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowInsetsForSystemBars()
        setContentView(binding.root)
        initObservers()
        setNavController()
        if (savedInstanceState != null) {
            isBottomBarNavigationVisible = savedInstanceState.getBoolean(IS_BOTTOM_BAR_VISIBLE_KEY)
        }
        initializeActivity(savedInstanceState)
        bottomNavMenuViewModel.initBottomNavState()
    }

    private val bottomNavMenuStateCollector: suspend (BottomNavigationMenuViewModel.ViewState) -> Unit = { state ->
        when (state) {
            is BottomNavigationMenuViewModel.ViewState.Content -> {
                initBottomNavMenu(state.menuItems)
                updateMenuItemForDestination(navController.currentDestination?.id)
            }

            BottomNavigationMenuViewModel.ViewState.Idle -> Unit
        }
    }

    private fun setNavController() {
        val navHostFragment = supportFragmentManager.findFragmentById(binding.navigationHostFragment.id)
        navController = (navHostFragment as NavHostFragment).navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateMenuItemForDestination(destination.id)
        }
    }

    private fun updateMenuItemForDestination(destinationId: Int?) {
        val menuItemId = when (destinationId) {
            R.id.accountsFragment -> R.id.accountsFragment
            R.id.discoverHomeFragment -> R.id.discoverHomeNavigation
            R.id.swapFragment -> R.id.swapV2Navigation
            R.id.stakingFragment -> R.id.stakingFragment
            R.id.xoSwapFragment -> R.id.xoSwapFragment
            R.id.collectiblesFragment -> R.id.collectiblesFragment
            else -> null
        }
        menuItemId?.let { binding.bottomNavigationView.menu.findItem(it)?.isChecked = true }
    }

    private fun initializeActivity(savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                coreMainViewModel.initialize(savedInstanceState)
            }
        }
    }

    private fun initObservers() {
        collectLatestOnLifecycle(
            flow = coreMainViewModel.viewEvent,
            collection = viewEventCollector,
            state = Lifecycle.State.CREATED
        )
        collectLatestOnLifecycle(
            flow = bottomNavMenuViewModel.state,
            collection = bottomNavMenuStateCollector,
            state = Lifecycle.State.CREATED
        )
        collectLatestOnLifecycle(
            flow = bottomNavMenuViewModel.viewEvent,
            collection = bottomNavMenuEventCollector,
            state = Lifecycle.State.CREATED
        )
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        outState.putBoolean(IS_BOTTOM_BAR_VISIBLE_KEY, isBottomBarNavigationVisible)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    fun handleNavigationButtonsForChosenNetwork() {
        bottomNavMenuViewModel.updateBottomNavOnNodeChange()
    }

    fun checkIfConnectedToTestNet(activeNode: Node?) {
        isConnectedToTestNet = activeNode?.networkSlug == TESTNET_NETWORK_SLUG
    }

    fun navBack() {
        navController.navigateBackSafe()
    }

    fun nav(directions: NavDirections, onError: (() -> Unit)? = null) {
        navController.navigateSafe(directions, onError)
    }

    fun nav(directions: NavDirections, extras: FragmentNavigator.Extras) {
        navController.navigateSafe(directions, extras)
    }

    fun setBottomNavigationBarSelectedItem(@IdRes itemRes: Int) {
        binding.bottomNavigationView.selectedItemId = itemRes
    }

    fun getToolbar(): CustomToolbar {
        return binding.toolbar
    }

    fun showProgress() {
        binding.progressBar.root.show()
    }

    fun hideProgress() {
        binding.progressBar.root.hide()
    }

    private fun initializeCoreManagers() {
        with(lifecycle) {
            addObserver(parityManager)
            addObserver(notificationPermissionManager)
        }
        observeAutoLockManager()
    }

    private fun startNavigation(startDestinationFragmentId: Int) {
        with(navController) {
            graph = navInflater.inflate(R.navigation.main_navigation).apply {
                setStartDestination(startDestinationFragmentId)
            }
            binding.bottomNavigationView.setupWithNavController(this, ::onMenuItemClicked)
        }
    }

    private fun handleStatusBarChanges(statusBarConfiguration: StatusBarConfiguration) {
        val intendedStatusBarColor =
            if (statusBarConfiguration.showNodeStatus && isConnectedToTestNet) {
                R.color.testnet_bg
            } else {
                statusBarConfiguration.backgroundColor
            }

        val statusBarColor = ContextCompat.getColor(this, intendedStatusBarColor)
        binding.statusBarBackgroundView.setBackgroundColor(statusBarColor)
    }

    private fun setWindowInsetsForSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val root = binding.rootConstraintLayout

        val initialPadding = Rect(
            root.paddingLeft,
            root.paddingTop,
            root.paddingRight,
            root.paddingBottom
        )

        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val sys = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomPadding = if (isBottomBarNavigationVisible) {
                0
            } else {
                maxOf(sys.bottom, ime.bottom)
            }
            v.updatePadding(
                left = initialPadding.left + sys.left,
                top = initialPadding.top,
                right = initialPadding.right + sys.right,
                bottom = bottomPadding
            )

            binding.statusBarBackgroundView.updateLayoutParams {
                height = sys.top
            }

            insets
        }

        ViewCompat.requestApplyInsets(root)
    }

    private fun handleStatusBarIconColorChanges(
        oldStatusBarConfiguration: StatusBarConfiguration,
        newStatusBarConfiguration: StatusBarConfiguration
    ) {
        if (oldStatusBarConfiguration.showLightStatusBarIcons != newStatusBarConfiguration.showLightStatusBarIcons) {
            if (newStatusBarConfiguration.showLightStatusBarIcons) {
                showLightStatusBarIcons()
            } else {
                showDarkStatusBarIcons()
            }
        }
    }

    private fun initBottomNavMenu(items: List<BottomNavMenuItem>) {
        binding.bottomNavigationView.menu.apply {
            if (isEmpty()) {
                items.forEachIndexed { index, item ->
                    add(0, item.id, index, item.titleResId).apply {
                        setIcon(item.iconResId)
                        isEnabled = item.enabled
                        isCheckable = true
                    }
                }
            }
        }
    }

    companion object {
        private const val IS_BOTTOM_BAR_VISIBLE_KEY = "is_bottom_bar_visible"
    }
}
