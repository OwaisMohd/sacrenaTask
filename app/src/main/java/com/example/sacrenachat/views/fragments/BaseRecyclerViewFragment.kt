package com.example.sacrenachat.views.fragments

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.example.sacrenachat.R

abstract class BaseRecyclerViewFragment<ViewBindingType : ViewBinding>
    : BaseFragment<ViewBindingType>() {

    companion object {
        private const val TAG = "BaseRecyclerViewFragmen"
    }

    // Variables
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null
    private var groupNoData: Group? = null
    private var ivNoData: ImageView? = null
    private var tvNoDataHeading: TextView? = null
    private var tvNoDataSubHeading: TextView? = null


    override fun init() {
        setupBaseViews()
        observeRecyclerViewProperties()
    }

    private fun setupBaseViews() {
        // Assign Base Views
        swipeRefreshLayout = requireView().findViewById(R.id.swipeRefreshLayout)
        recyclerView = requireView().findViewById(R.id.recyclerView)
        groupNoData = requireView().findViewById(R.id.groupNoData)
        ivNoData = requireView().findViewById(R.id.ivNoData)
        tvNoDataHeading = requireView().findViewById(R.id.tvNoDataHeading)
        tvNoDataSubHeading = requireView().findViewById(R.id.tvNoDataSubHeading)

        // Set SwipeRefreshLayout
        swipeRefreshLayout?.let { swipeRefreshLayout ->
            // Set Default Color Scheme
            swipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent, R.color.colorAccent,
                R.color.colorAccent, R.color.colorAccent
            )
            // Set Refresh Mechanism
            swipeRefreshLayout.setOnRefreshListener {
                onPullDownToRefresh()
            }
        }

        setData()

        // Set Recycler View
        recyclerView?.let { recyclerView ->
            // Set Linear Layout Manager as default if none present else assign the given Manager.
            recyclerView.layoutManager = if (null == layoutManager) {
                androidx.recyclerview.widget.LinearLayoutManager(requireContext())
            } else {
                (layoutManager)
            }

            // Set Recycler View Divider
            if (isShowRecyclerViewDivider) {
                // Use Native Divider By Default.
                recyclerView.addItemDecoration(
                    androidx.recyclerview.widget.DividerItemDecoration(
                        requireContext(),
                        androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
                    )
                )

                // Uncomment the following for custom divider.
                /*val dividerItemDecoration = DividerItemDecoration(
                        ContextCompat.getDrawable(requireContext(),
                                R.drawable.drawable_top_bottom_color_divider_stroke))
                recyclerView.addItemDecoration(dividerItemDecoration)*/
            }
            // Assign Adapter
            recyclerView.adapter = recyclerViewAdapter
        }
    }

    private fun observeRecyclerViewProperties() {
        // Observe SwipeRefresh Layout
        viewModel?.isShowSwipeRefreshLayout()?.observe(viewLifecycleOwner, { showRefreshLayout ->
            if (showRefreshLayout) {
                showSwipeRefreshLoader()
            } else {
                hideSwipeRefreshLoader()
            }
        })

        // Observe Retrofit Errors
        viewModel?.getMessage()?.observe(viewLifecycleOwner, { pojoMessage ->
            showNoDataText(resIdHeading = pojoMessage?.resId, messageHeading = pojoMessage?.message)
        })
    }

    /**
     *  @description call this method to when no data is found for recycler view to show some message
     *  @param imageRes {Int?} Drawable ID to be displayed
     *  @param {messageHeading, messageSubHeading} {String?} String message to be shown (if null method will use resId to show text)
     *  @param {resIdHeading,resIdSubHeading} {Int?} resource id is string (will be used if message value is null)
     */
    fun showNoDataText(
        @DrawableRes imageRes: Int? = null,
        @StringRes resIdHeading: Int? = null, messageHeading: String? = null,
        @StringRes resIdSubHeading: Int? = null, messageSubHeading: String? = null,
    ) {
        if (null == resIdHeading && null == messageHeading
            && null == resIdSubHeading && null == messageSubHeading
        ) {
            hideNoDataText()
        } else {
            if (0 < recyclerViewAdapter?.itemCount!!) {
                val heading = if (resIdHeading != null) {
                    getString(resIdHeading)
                } else messageHeading ?: ""

                val subHeading = if (resIdSubHeading != null) {
                    getString(resIdSubHeading)
                } else messageSubHeading ?: ""

                //Show Toast of heading and subheading in case recycler view has items present.
                if (heading.isNotEmpty() && subHeading.isNotEmpty()) {
                    showMessage(message = "$heading\n$subHeading")
                } else if (heading.isNotEmpty()) {
                    showMessage(message = heading)
                } else if (subHeading.isNotEmpty()) {
                    showMessage(message = subHeading)
                }
            } else {
                groupNoData?.visibility = View.VISIBLE
                if (imageRes != null) {
                    ivNoData?.setImageResource(imageRes)
                } else {
                    ivNoData?.setImageBitmap(null)
                }
                if (messageHeading != null || resIdHeading != null) {
                    tvNoDataHeading?.text = messageHeading ?: getString(resIdHeading!!)
                } else {
                    tvNoDataHeading?.text = ""
                }
                if (messageSubHeading != null || resIdSubHeading != null) {
                    tvNoDataSubHeading?.text = messageSubHeading ?: getString(resIdSubHeading!!)
                } else {
                    tvNoDataSubHeading?.text = ""
                }
            }
        }
    }

    /**
     *  @description call this method to hide NoDataText
     */
    fun hideNoDataText() {
        groupNoData?.visibility = View.GONE
    }

    /**
     *  @description call this method to show progress dialog
     */
    fun showSwipeRefreshLoader() {
        swipeRefreshLayout?.let { swipeRefreshLayout ->
            swipeRefreshLayout.post {
                swipeRefreshLayout.isRefreshing = true
            }
        }
    }

    /**
     *  @description call this method to hide progress dialog
     */
    fun hideSwipeRefreshLoader() {
        swipeRefreshLayout?.let { swipeRefreshLayout ->
            swipeRefreshLayout.post {
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    abstract fun setData()

    abstract val recyclerViewAdapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>?

    abstract val layoutManager: androidx.recyclerview.widget.RecyclerView.LayoutManager?

    abstract val isShowRecyclerViewDivider: Boolean

    abstract fun onPullDownToRefresh()
}