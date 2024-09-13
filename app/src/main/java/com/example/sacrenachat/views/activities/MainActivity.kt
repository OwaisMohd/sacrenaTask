package com.example.sacrenachat.views.activities

import android.content.Intent
import android.view.LayoutInflater
import com.example.sacrenachat.databinding.ActivityMainBinding
import com.example.sacrenachat.views.fragments.MessagesFragment

class MainActivity : BaseAppCompatActivity<ActivityMainBinding>() {

    companion object {
        const val TAG = "MainActivity"
    }

    override val isMakeStatusBarTransparent: Boolean
        get() = false

    override fun setupViewBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(inflater)
    }

    override fun init() {
        // Check if app is restarted from launcher icon then close redundant activity
        if (!isTaskRoot
            && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
            && intent.action != null
            && intent.action == Intent.ACTION_MAIN
        ) {
            finish()
            return
        }

        performTransaction(MessagesFragment(), MessagesFragment.TAG)
    }
}