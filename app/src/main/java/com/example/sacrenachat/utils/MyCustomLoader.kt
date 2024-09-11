package com.example.sacrenachat.utils

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.sacrenachat.R
import com.example.sacrenachat.databinding.DialogProgressLoaderBinding

class MyCustomLoader(private val mContext: Context?) {

    private var mDialog: Dialog? = null

    fun showSnackBar(view: View?, contentMsg: String) {
        if (null != mContext && null != view) {
            com.google.android.material.snackbar.Snackbar.make(
                view,
                contentMsg,
                com.google.android.material.snackbar.Snackbar.LENGTH_LONG
            )
                .setAction(
                    mContext.getString(R.string.action_okay)
                ) { }
                .setActionTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary))
                .show()
        }
    }

    fun showToast(contentMsg: String) {
        if (null != mContext) {
            Toast.makeText(mContext, contentMsg, Toast.LENGTH_SHORT).show()
        }
    }

    fun showAlertDialog(
        title: String, message: String, positiveButtonText: String,
        negativeButtonText: String,
        okListener: DialogInterface.OnClickListener
    ) {
        AlertDialog.Builder(mContext!!)
            //                .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText, okListener)
            .setNegativeButton(negativeButtonText, null)
            .create()
            .show()
    }

    fun showProgressDialog() {
        mDialog = Dialog(mContext!!)
        mDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val binding = DialogProgressLoaderBinding.inflate(LayoutInflater.from(mContext))
        mDialog!!.setContentView(binding.root)
        mDialog!!.setCancelable(false)
        mDialog!!.show()
    }

    fun dismissProgressDialog() {
        mDialog?.dismiss()
    }
}