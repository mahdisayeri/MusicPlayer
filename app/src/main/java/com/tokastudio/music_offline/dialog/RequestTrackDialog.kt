package com.tokastudio.music_offline.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.DialogFragment
import com.tokastudio.music_offline.R

class RequestTrackDialog : DialogFragment() {

   private lateinit var listener: RequestTrackListener

     interface RequestTrackListener{
         fun onSendBtnClick(dialog: DialogFragment,artistName: String,trackName: String)
     }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
      return activity.let {
          val builder=AlertDialog.Builder(activity)
          val layoutInflater: LayoutInflater = requireActivity().layoutInflater
          val view: View =layoutInflater.inflate(R.layout.dialog_request_track,null)

          val artistName =view.findViewById<AppCompatEditText>(R.id.dialog_request_artist_name)
          val trackName =view.findViewById<AppCompatEditText>(R.id.dialog_request_track_name)
          val sendBtn: Button=view.findViewById(R.id.dialog_request_send_email)
          val cancelBtn: Button=view.findViewById(R.id.dialog_request_cancel)
          builder.setView(view)

          sendBtn.setOnClickListener {
              listener.onSendBtnClick(this@RequestTrackDialog,artistName.text.toString(),trackName.text.toString())
          }
          cancelBtn.setOnClickListener {
              dismiss()
          }

          builder.create()
      } ?: throw IllegalStateException("Activity cannot be null")

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener= context as RequestTrackListener
        } catch (e: ClassCastException){
            throw ClassCastException("$context must implement RequestTrackListener")
        }
    }
}