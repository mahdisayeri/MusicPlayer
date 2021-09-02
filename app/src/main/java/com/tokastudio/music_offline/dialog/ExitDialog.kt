package com.tokastudio.music_offline.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.tokastudio.music_offline.R

class ExitDialog : DialogFragment() {

   // private val TAG = "ExitDialog"

    var listener: ExitDialogListener? = null

    interface ExitDialogListener {
        fun onExitBtnClick(dialog: ExitDialog?)
        fun onCancelBtnClick(dialog: ExitDialog?)
        fun onRatingBrnClick(dialog: ExitDialog?)
    }


    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val layoutInflater: LayoutInflater = requireActivity().layoutInflater
        val view: View = layoutInflater.inflate(R.layout.dialog_exit, null)

        val exitBtn: Button = view.findViewById(R.id.exit)
        val cancelBtn: Button = view.findViewById(R.id.cancel)
        val ratingBtn: Button= view.findViewById(R.id.rating)

        YoYo.with(Techniques.Pulse)
                .duration(1000)
                .repeat(100)
                .pivotX(160f)
                .playOn(ratingBtn)
        builder.setView(view)
        exitBtn.setOnClickListener { listener!!.onExitBtnClick(this@ExitDialog) }
        cancelBtn.setOnClickListener { listener!!.onCancelBtnClick(this@ExitDialog) }
        ratingBtn.setOnClickListener { listener!!.onRatingBrnClick(this@ExitDialog) }
        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        listener = try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            context as ExitDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw IllegalStateException(" must implement NoticeDialogListener")
        }
    }
}