package com.algorand.android.modules.notification.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import com.algorand.android.MainActivity
import com.algorand.android.R
import com.algorand.android.core.DaggerBaseFragment
import com.algorand.android.customviews.toolbar.buttoncontainer.model.IconButton
import com.algorand.android.databinding.FragmentNotificationCenterBinding
import com.algorand.android.models.FragmentConfiguration
import com.algorand.android.models.ScreenState
import com.algorand.android.models.ToolbarConfiguration
import com.algorand.android.modules.notification.ui.adapter.NotificationAdapter
import com.algorand.android.modules.notification.ui.model.NotificationListItem
import com.algorand.android.utils.extensions.collectLatestOnLifecycle
import com.algorand.android.utils.scrollToTop
import com.algorand.android.utils.viewbinding.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.time.ZonedDateTime

@AndroidEntryPoint
class NotificationCenterFragment : DaggerBaseFragment(R.layout.fragment_notification_center) {

    private val notificationCenterViewModel: NotificationCenterViewModel by viewModels()

    private val binding by viewBinding(FragmentNotificationCenterBinding::bind)

    private val toolbarConfiguration = ToolbarConfiguration(
        titleResId = R.string.notifications,
        startIconResId = R.drawable.ic_left_arrow,
        startIconClick = ::navBack
    )

    override val fragmentConfiguration = FragmentConfiguration(
        toolbarConfiguration = toolbarConfiguration,
        isBottomBarNeeded = false
    )

    private val emptyState by lazy {
        ScreenState.CustomState(
            icon = R.drawable.ic_notification,
            title = R.string.no_current_notifications,
            description = R.string.your_recent_transactions
        )
    }

    private val notificationAdapter = NotificationAdapter(::onNewItemAddedToTop, ::onNotificationClick)

    private val notificationPaginationCollector: suspend (PagingData<NotificationListItem>) -> Unit = { pagingData ->
        notificationAdapter.submitData(pagingData)
    }

    private val loadStateFlowCollector: suspend (CombinedLoadStates) -> Unit = { combinedLoadStates ->
        val isNotificationListEmpty = notificationAdapter.itemCount == 0
        val isCurrentStateError = combinedLoadStates.refresh is LoadState.Error
        val isLoading = combinedLoadStates.refresh is LoadState.Loading
        binding.swipeRefreshLayout.isRefreshing = isLoading
        when {
            isCurrentStateError -> {
                enableNotificationsErrorState((combinedLoadStates.refresh as LoadState.Error).error)
            }
            isLoading.not() && isNotificationListEmpty -> {
                binding.screenStateView.setupUi(emptyState)
            }
        }
        binding.notificationsRecyclerView.isInvisible =
            isCurrentStateError || isNotificationListEmpty
        binding.screenStateView.isVisible =
            (isCurrentStateError || isNotificationListEmpty) && isLoading.not()
    }

    private val isRefreshNeededObserver = Observer<Boolean> { isRefreshNeeded ->
        if (isRefreshNeeded) {
            refreshList(changeRefreshTime = true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        initObservers()
        initUi()
    }

    private fun initUi() {
        binding.screenStateView.setOnNeutralButtonClickListener(::handleErrorButtonClick)
    }

    private fun setupToolbar() {
        getAppToolbar()?.setEndButton(button = IconButton(R.drawable.ic_filter, onClick = ::onFilterClick))
    }

    private fun setupRecyclerView() {
        binding.notificationsRecyclerView.apply {
            notificationAdapter.lastRefreshedDateTime = notificationCenterViewModel.getLastRefreshedDateTime()
            notificationCenterViewModel.setLastRefreshedDateTime(ZonedDateTime.now())
            adapter = notificationAdapter
        }

        notificationAdapter.registerDataObserver()

        handleLoadState()

        binding.swipeRefreshLayout.setOnRefreshListener { refreshList(changeRefreshTime = true) }
    }

    private fun initObservers() {
        viewLifecycleOwner.collectLatestOnLifecycle(
            notificationCenterViewModel.notificationHistoryPagingFlow,
            notificationPaginationCollector
        )
        notificationCenterViewModel.isRefreshNeededLiveData().observe(viewLifecycleOwner, isRefreshNeededObserver)
    }

    override fun onResume() {
        super.onResume()
        refreshList(changeRefreshTime = false)
    }

    private fun handleLoadState() {
        viewLifecycleOwner.collectLatestOnLifecycle(
            notificationAdapter.loadStateFlow,
            loadStateFlowCollector
        )
    }

    private fun enableNotificationsErrorState(throwable: Throwable) {
        if (throwable is IOException) {
            binding.screenStateView.setupUi(ScreenState.ConnectionError())
        } else {
            binding.screenStateView.setupUi(ScreenState.DefaultError())
        }
    }

    private fun handleErrorButtonClick() {
        refreshList()
    }

    private fun refreshList(changeRefreshTime: Boolean = false) {
        var refreshDateTime: ZonedDateTime? = null
        if (changeRefreshTime) {
            refreshDateTime = ZonedDateTime.now()
            notificationAdapter.lastRefreshedDateTime = refreshDateTime
        }
        notificationCenterViewModel.refreshNotificationData(refreshDateTime)
    }

    private fun onNotificationClick(notificationListItem: NotificationListItem) {
        if (!notificationListItem.isFailed && !notificationListItem.uri.isNullOrBlank()) {
            (activity as? MainActivity)?.handleDeepLink(notificationListItem.uri)
        }
    }

    private fun onNewItemAddedToTop() {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            binding.notificationsRecyclerView.scrollToTop()
            notificationCenterViewModel.updateLastSeenNotification(
                notificationListItem = notificationAdapter.snapshot().firstOrNull()
            )
        }
    }

    override fun onDestroyView() {
        notificationAdapter.unregisterDataObserver()
        super.onDestroyView()
    }

    private fun onFilterClick() {
        nav(
            NotificationCenterFragmentDirections.actionNotificationCenterFragmentToNotificationFilterFragment(
                showDoneButton = false
            )
        )
    }
}
