package com.example.sacrenachat.views.fragments

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import com.example.sacrenachat.R
import com.example.sacrenachat.utils.MyCustomLoader
import com.example.sacrenachat.viewmodels.BaseViewModel
import com.example.sacrenachat.views.activities.MainActivity
import com.example.sacrenachat.views.activities.doFragmentTransaction
import com.example.sacrenachat.views.utils.supportFragmentManager


abstract class BaseFragment<ViewBindingType : ViewBinding> : Fragment(), LifecycleObserver {

    companion object {
        private const val TAG = "BaseFragment"
        private const val DEFAULT_REQUEST_CODE = -345
    }

    // Variables
    private var _binding: ViewBindingType? = null

    // Binding variable to be used for accessing views.
    protected val binding
        get() = requireNotNull(_binding)

    private var requestCode: Int =
        DEFAULT_REQUEST_CODE       // To keep track of request codes for intents.

    private val mMyCustomLoader: MyCustomLoader by lazy {
        MyCustomLoader(requireContext())
    }

    private val launchIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            doOnActivityResult(requestCode, result.resultCode, result.data)
            // Reset code.
            this.requestCode = DEFAULT_REQUEST_CODE
        }

    val activityContext: Context
        get() = requireActivity()

    // Calls the abstract function to return the ViewBinding.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = setupViewBinding(inflater, container)
        return requireNotNull(_binding).root
    }

    // Set up the LifeCycle observer to get rid of binding once done.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycle.addObserver(this)
        // This callback will only be called when MyFragment is at least Started.
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (supportFragmentManager().backStackEntryCount > 0) {
                        supportFragmentManager().popBackStack()
                    } else {
                        requireActivity().finish()
                    }
                }
            })
        init()
        observeBaseProperties()
    }

    abstract fun setupViewBinding(
        inflater: LayoutInflater, container: ViewGroup?,
    ): ViewBindingType

    abstract val viewModel: BaseViewModel?

    abstract fun init()

    /**
     *  @description call this method to show toast or snack bars
     *  @param message {String} String message to be shown (if null method will use resId to show text)
     *  @param resId {Int?} resource id is string (will be used if message value is null)
     *  @param isShowSnackbarMessage {Boolean} A boolean to switch between snackBar and toast
     */
    fun showMessage(
        @StringRes resId: Int? = null, message: String? = null,
        isShowSnackbarMessage: Boolean = false,
    ) {
        if (isShowSnackbarMessage) {
            mMyCustomLoader.showSnackBar(view, message ?: getString(resId!!))
        } else {
            mMyCustomLoader.showToast(message ?: getString(resId!!))
        }
    }

    /**
     *  @description call this method to show progress dialog
     *  @param isShowProgress {Boolean} Boolean to show or hide progress bar
     */
    fun showProgressDialog(isShowProgress: Boolean) {
        if (isShowProgress) {
            mMyCustomLoader.showProgressDialog()
        } else {
            mMyCustomLoader.dismissProgressDialog()
        }
    }

    /**
     *  @description call this method to hide progress dialog
     */
    fun dismissDialogFragment() {
        (requireActivity().supportFragmentManager
            .findFragmentByTag(getString(R.string.dialog)) as androidx.fragment.app.DialogFragment).dismiss()
    }

    /**
     *  @description call this method to start home activity and finish current activity
     */
    protected fun navigateToMainActivity() {
        startActivity(
            Intent(activityContext, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        activity?.finish()
    }

    /**
     *  @description observe live data values
     */
    private fun observeBaseProperties() {
        // Observe session id
        viewModel?.isSessionExpired()?.observe(viewLifecycleOwner) { sessionExpired ->
            AsyncTask.execute {
                //                Firebase.messaging.deleteToken()
            }
            if (sessionExpired) {
                navigateToMainActivity()
            }
        }
        // Observe Errors
        viewModel?.getErrorHandler()?.observe(viewLifecycleOwner) { baseError ->
            showMessage(resId = baseError?.getErrorResource())
        }

        // Observe Loader
        viewModel?.isShowLoader()?.observe(viewLifecycleOwner) { showLoader ->
            showProgressDialog(showLoader)
        }

        // Observe Retrofit Errors
        viewModel?.getMessage()?.observe(viewLifecycleOwner, { pojoMessage ->
            showMessage(pojoMessage?.resId, pojoMessage?.message)
        })
    }

    /**
     * @description Helper method to change Fragments making use of doFragmentTransaction method of Activity.
     * @param frag is the Fragment to which you want to perform transaction to.
     * @param transactionTag is the TAG of the Fragment you're performing transaction to.
     * @param isAddFragment is Boolean to represent if fragment is being added or replaced. True, by default.
     */
    fun performTransaction(frag: Fragment, transactionTag: String, isAddFragment: Boolean = true) {
        val act = activity as AppCompatActivity
        act.doFragmentTransaction(
            act.supportFragmentManager, R.id.flFragContainer, frag,
            isAddFragment = isAddFragment, tag = transactionTag
        )
    }

    /**
     * Utility function to start an activity for result using the new result contract APIs.
     */
    protected fun openActivityForResult(intent: Intent?, requestCode: Int) {
        this.requestCode = requestCode      // Assign to global request code.
        launchIntent.launch(intent)
    }

    /**
     * Utility function to receive an activity result.
     */
    protected open fun doOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }

    // Clears the binding and removes the observer when the Fragment's views get destroyed.
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun clearViewBinding() {
        _binding = null
        viewLifecycleOwner.lifecycle.removeObserver(this)
    }

    /*
     * Safe call method, just in case, if anything is messed up and lifecycle Event does not gets
     * called.
     */
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}