package com.example.sacrenachat.views.activities

import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.AnimatorRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import com.example.sacrenachat.R

abstract class BaseAppCompatActivity<ViewBindingType : ViewBinding> : AppCompatActivity(),
    LifecycleObserver {

    companion object {
        private const val TAG = "BaseAppCompatActivity"
        private const val DEFAULT_REQUEST_CODE = -345
    }

    // Variables
    private var _binding: ViewBindingType? = null

    // Binding variable to be used for accessing views.
    protected val binding
        get() = requireNotNull(_binding)

    private var requestCode: Int = DEFAULT_REQUEST_CODE       // To keep track of request codes for intents.

    private val launchIntent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            doOnActivityResult(requestCode, result.resultCode, result.data)
            // Reset code.
            this.requestCode = DEFAULT_REQUEST_CODE
        }

    /*
     * Calls the abstract function to return the ViewBinding and set up LifeCycle Observer to get
     * rid of binding once done.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = setupViewBinding(layoutInflater)
        setContentView(requireNotNull(_binding).root)
        lifecycle.addObserver(this)
        setStatusBar()
        init()
    }

    abstract val isMakeStatusBarTransparent: Boolean

    abstract fun init()

    abstract fun setupViewBinding(inflater: LayoutInflater): ViewBindingType

    // Set Status bar according to flag.
    private fun setStatusBar() {
        val window = window
        if (isMakeStatusBarTransparent) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorTransparent)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        } else {
            window.statusBarColor = ContextCompat.getColor(this,
                R.color.colorPrimaryDark)
        }
    }

    /**
     *  @description Helper method to perform Fragment Transactions.
     *
     *  @param frag {Fragment} The Fragment to which we are going.
     *  @param transactionTag {String} the tag for the fragment for which we are going to.
     *  @param isAddFragment {Boolean} to represent whether we are adding or removing fragment.
     */
    fun performTransaction(frag: Fragment, transactionTag: String, isAddFragment: Boolean = true) {
        doFragmentTransaction(supportFragmentManager, R.id.flFragContainer, frag,
            isAddFragment = isAddFragment, tag = transactionTag, enterAnimation = 0,
            popExitAnimation = 0)
    }

    /**
     * Utility function to start an activity for result using the new result contract APIs.
     */
    protected fun openActivityForResult(intent: Intent?, requestCode: Int) {
        this.requestCode = requestCode      // Assign to global request code.
        launchIntent.launch(intent!!)
    }

    /**
     * Utility function to receive an activity result.
     */
    protected open fun doOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }

    // Clears the binding and removes the observer when the activity is destroyed.
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun clearViewBinding() {
        _binding = null
        lifecycle.removeObserver(this)
    }

    /*
     * Safe call method, just in case, if anything is messed up and lifecycle Event does not gets
     * called.
     */
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}

/**
 *  @description Generic layout inflater
 */
fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

/**
 *  @description Generic function do fragment transaction
 *  @param fragManager {FragmentManager}
 *  @param containerViewId {Int} id of view for populating fragment
 *  @param fragment {Fragment} fragment to be populated
 *  @param tag {String} add tag for managing fragments in fragmentBackStack
 *  @param enterAnimation {Int} Enter animation for fragment
 *  @param exitAnimation {Int} Enter animation for fragment
 *  @param popEnterAnimation {Int} Enter animation for fragment
 *  @param popExitAnimation {Int} Enter animation for fragment
 *  @param isAddFragment {Boolean} Boolean to switch between add or replace fragment
 *  @param isAddToBackStack {Boolean} Add fragment to fragmentBackStack or not
 *  @param allowStateLoss {Boolean} make true if fragment has any pending transactions with delays
 */
fun AppCompatActivity.doFragmentTransaction(
    fragManager: androidx.fragment.app.FragmentManager = supportFragmentManager,
    @IdRes containerViewId: Int,
    fragment: Fragment,
    tag: String = "",
    @AnimatorRes enterAnimation: Int = R.animator.slide_right_in,
    @AnimatorRes exitAnimation: Int = 0,
    @AnimatorRes popEnterAnimation: Int = 0,
    @AnimatorRes popExitAnimation: Int = R.animator.slide_right_out,
    isAddFragment: Boolean = true,
    isAddToBackStack: Boolean = true,
    allowStateLoss: Boolean = false,
) {

    // turn of animations if power saver is on
    val powerManager = getSystemService(POWER_SERVICE) as PowerManager
    val isPowerSaverEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && powerManager.isPowerSaveMode

    val fragmentTransaction = fragManager.beginTransaction()
    if (!isPowerSaverEnabled)
        fragmentTransaction.setCustomAnimations(enterAnimation, exitAnimation, popEnterAnimation, popExitAnimation)

    if (isAddFragment) {
        fragmentTransaction.add(containerViewId, fragment, tag)
    } else {
        fragmentTransaction.replace(containerViewId, fragment, tag)
    }

    if (isAddToBackStack) {
        fragmentTransaction.addToBackStack(null)
    }

    if (allowStateLoss) {
        fragmentTransaction.commitAllowingStateLoss()
    } else {
        fragmentTransaction.commit()
    }
}