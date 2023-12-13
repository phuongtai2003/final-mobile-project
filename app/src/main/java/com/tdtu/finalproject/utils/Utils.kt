package com.tdtu.finalproject.utils

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.tdtu.finalproject.R
import com.tdtu.finalproject.model.quizzes.Quiz
import com.tdtu.finalproject.model.user.User
import com.tdtu.finalproject.model.vocabulary.Vocabulary

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

        fun showCreateFolderDialog(gravity: Int, mContext: Context, onDialogConfirmListener: OnDialogConfirmListener){
            val dialog = Dialog(mContext)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.create_folder_dialog)

            val window = dialog.window ?: return

            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val windowAttribute: WindowManager.LayoutParams = window.attributes
            windowAttribute.gravity = gravity
            window.attributes = windowAttribute
            dialog.setCancelable(true)

            val createBtn = dialog.findViewById<Button>(R.id.createFolderBtn)
            val cancelBtn = dialog.findViewById<Button>(R.id.createFolderCancelBtn)
            val folderNameEnglishEdt = dialog.findViewById<TextView>(R.id.folderNameEdt)
            val folderNameVietnameseEdt = dialog.findViewById<TextView>(R.id.folderNameVietnameseEdt)
            cancelBtn.setOnClickListener{
                dialog.dismiss()
            }

            createBtn.setOnClickListener{
                val folderNameEnglish = folderNameEnglishEdt.text.toString()
                val folderNameVietnamese = folderNameVietnameseEdt.text.toString()
                if(folderNameEnglish.isEmpty() || folderNameVietnamese.isEmpty()){
                    Toast.makeText(mContext, mContext.getString(R.string.please_enter_folder_name), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                onDialogConfirmListener.onCreateFolderDialogConfirm(folderNameEnglish, folderNameVietnamese)
                dialog.dismiss()
            }
            dialog.show()
        }

        fun showEditFolderDialog(folderNameEnglish: String, folderNameVietnamese: String,gravity: Int, mContext: Context, onDialogConfirmListener: OnDialogConfirmListener){
            val dialog = Dialog(mContext)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.create_folder_dialog)

            val window = dialog.window ?: return

            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val windowAttribute: WindowManager.LayoutParams = window.attributes
            windowAttribute.gravity = gravity
            window.attributes = windowAttribute
            dialog.setCancelable(true)

            val createBtn = dialog.findViewById<Button>(R.id.createFolderBtn)
            val cancelBtn = dialog.findViewById<Button>(R.id.createFolderCancelBtn)
            val folderNameEnglishEdt = dialog.findViewById<TextView>(R.id.folderNameEdt)
            val folderNameVietnameseEdt = dialog.findViewById<TextView>(R.id.folderNameVietnameseEdt)

            folderNameEnglishEdt.text = folderNameEnglish
            folderNameVietnameseEdt.text = folderNameVietnamese
            cancelBtn.setOnClickListener{
                dialog.dismiss()
            }

            createBtn.setOnClickListener{
                val folderNameEnglish = folderNameEnglishEdt.text.toString()
                val folderNameVietnamese = folderNameVietnameseEdt.text.toString()
                if(folderNameEnglish.isEmpty() || folderNameVietnamese.isEmpty()){
                    Toast.makeText(mContext, mContext.getString(R.string.please_enter_folder_name), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                onDialogConfirmListener.onCreateFolderDialogConfirm(folderNameEnglish, folderNameVietnamese)
                dialog.dismiss()
            }
            dialog.show()
        }
        fun showResetPasswordDialog(gravity: Int, mContext: Context, resetPasswordConfirmListener: ResetPasswordConfirmListener){

            val dialog = Dialog(mContext)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.forget_password_dialog)

            val window = dialog.window ?: return

            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val windowAttribute: WindowManager.LayoutParams = window.attributes
            windowAttribute.gravity = gravity
            window.attributes = windowAttribute
            dialog.setCancelable(true)

            val acceptBtn = dialog.findViewById<MaterialButton>(R.id.acceptBtn)
            val cancelBtn = dialog.findViewById<MaterialButton>(R.id.cancelBtn)
            val emailEdt = dialog.findViewById<EditText>(R.id.emailField)

            cancelBtn.setOnClickListener{
                dialog.dismiss()
            }

            acceptBtn.setOnClickListener{
                val email = emailEdt.text.toString()
                if(email.isEmpty()){
                    Toast.makeText(mContext, mContext.getString(R.string.please_fill), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                resetPasswordConfirmListener.onConfirm(email)
                dialog.dismiss()
            }
            dialog.show()

        }

        fun showChangePasswordDialog(gravity: Int, mContext: Context, resetPasswordConfirmListener: ResetPasswordConfirmListener){
            val dialog = Dialog(mContext)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.change_password_dialog)

            val window = dialog.window ?: return

            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val windowAttribute: WindowManager.LayoutParams = window.attributes
            windowAttribute.gravity = gravity
            window.attributes = windowAttribute
            dialog.setCancelable(true)

            val acceptBtn = dialog.findViewById<MaterialButton>(R.id.acceptBtn)
            val cancelBtn = dialog.findViewById<MaterialButton>(R.id.cancelBtn)
            val passwordEdt = dialog.findViewById<EditText>(R.id.oldPasswordEdt)
            val newPasswordEdt = dialog.findViewById<EditText>(R.id.newPasswordEdt)
            val confirmedPasswordEdt = dialog.findViewById<EditText>(R.id.confirmedPasswordEdt)

            cancelBtn.setOnClickListener{
                dialog.dismiss()
            }

            acceptBtn.setOnClickListener{
                val password = passwordEdt.text.toString()
                val newPassword = newPasswordEdt.text.toString()
                val confirmedPassword = confirmedPasswordEdt.text.toString()
                if(password.isEmpty() || newPassword.isEmpty() || confirmedPassword.isEmpty()){
                    Toast.makeText(mContext, mContext.getString(R.string.please_fill), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                else if(newPassword != confirmedPassword){
                    Toast.makeText(mContext, mContext.getString(R.string.password_not_match), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                resetPasswordConfirmListener.changePassword(password, newPassword)
                dialog.dismiss()
            }
            dialog.show()
        }

        fun showDeleteFolderConfirmDialog(gravity: Int, message: String, mContext: Context, onDialogConfirmListener: OnDialogConfirmListener){
            val dialog = Dialog(mContext)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.option_dialog)

            val window = dialog.window ?: return

            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val windowAttribute: WindowManager.LayoutParams = window.attributes
            windowAttribute.gravity = gravity
            window.attributes = windowAttribute
            dialog.setCancelable(true)

            val alertBody = dialog.findViewById<TextView>(R.id.alertBody)
            val cancelBtn = dialog.findViewById<Button>(R.id.dialogCancelBtn)
            val acceptBtn = dialog.findViewById<TextView>(R.id.dialogOkBtn)

            alertBody.text = message

            cancelBtn.setOnClickListener{
                dialog.dismiss()
            }
            acceptBtn.setOnClickListener {
                onDialogConfirmListener.onDeleteFolderDialogConfirm()
                dialog.dismiss()
            }
            dialog.show()
        }

        fun generateQuizzes(vocabularies: List<Vocabulary>, isShuffled: Boolean) : List<Quiz> {
            val quizzes = mutableListOf<Quiz>()
            val shuffledVocabularies = if(isShuffled) vocabularies.shuffled() else vocabularies
            for(vocabulary in shuffledVocabularies){
                val incorrectVocabularies : List<Vocabulary> = vocabularies - vocabulary
                val quiz = Quiz(vocabulary, ArrayList(incorrectVocabularies.shuffled().take(3)))
                quizzes.add(quiz)
            }
            return quizzes
        }

        fun propCorrectAnswer(mContext: Context){
            val dialog = Dialog(mContext)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.success_answer_dialog)
            val window = dialog.window ?: return
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val windowAttribute: WindowManager.LayoutParams = window.attributes
            windowAttribute.gravity = Gravity.CENTER
            window.attributes = windowAttribute
            dialog.setCancelable(false)

            val closeBtn = dialog.findViewById<ImageButton>(R.id.closeBtn)

            closeBtn.setOnClickListener{
                dialog.dismiss()
            }

            dialog.show()
        }

        fun propWrongAnswer(mContext: Context, correctAnswer: String, wrongAnswer: String){
            val dialog = Dialog(mContext)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.wrong_answer_dialog)
            val window = dialog.window ?: return
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val windowAttribute: WindowManager.LayoutParams = window.attributes
            windowAttribute.gravity = Gravity.CENTER
            window.attributes = windowAttribute
            dialog.setCancelable(false)

            val correctAnswerTxt = dialog.findViewById<TextView>(R.id.correctAnswerTxt)
            val wrongAnswerTxt = dialog.findViewById<TextView>(R.id.chosenWrongAnswerTxt)
            val closeBtn = dialog.findViewById<ImageButton>(R.id.closeBtn)

            correctAnswerTxt.text = correctAnswer
            wrongAnswerTxt.text = wrongAnswer
            closeBtn.setOnClickListener{
                dialog.dismiss()
            }
            dialog.show()
        }

        fun getUserFromSharedPreferences(mContext: Context,sharedPreferences: SharedPreferences): User{
            return Gson().fromJson(sharedPreferences.getString(mContext.getString(R.string.user_data_key), null), User::class.java)
        }
    }
}