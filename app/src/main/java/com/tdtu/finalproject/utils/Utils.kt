package com.tdtu.finalproject.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.tdtu.finalproject.R

class Utils {
    companion object{
        fun showSnackBar(view: View, message: String){
            val snackBar : Snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
            snackBar.setAction(R.string.close) {
                snackBar.dismiss()
            }

            snackBar.show()
        }

        fun showDialog(gravity: Int, message: String, mContext: Context){
            val dialog = Dialog(mContext)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.message_dialog)

            val window = dialog.window ?: return

            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val windowAttribute: WindowManager.LayoutParams = window.attributes
            windowAttribute.gravity = gravity
            window.attributes = windowAttribute
            dialog.setCancelable(true)

            val mainButton = dialog.findViewById<Button>(R.id.alertMainButton)
            val alertMessage = dialog.findViewById<TextView>(R.id.alertBody)

            alertMessage.text = message

            mainButton.setOnClickListener{
                dialog.dismiss()
            }

            dialog.show()
        }


    }
}