package com.example.godrive.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.godrive.R
import com.example.godrive.services.SignInService
import com.example.godrive.utils.ToastUtils

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class SignInFragment : Fragment() {

    private val REQUEST_CODE_SIGN_IN = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val requireContext: Context = requireContext()
        var emailAccount = "a@a.com"

        val emailEditText = view.findViewById<EditText>(R.id.edit_text_email)
        val btnSignIn = view.findViewById<View>(R.id.button_sign_in)
        val btnSignInPicker = view.findViewById<View>(R.id.button_sign_in_picker)

        emailEditText.doAfterTextChanged { text ->
            text?.let {
                emailAccount = it.toString()
            }
        }

        btnSignIn.setOnClickListener {
            val intent = SignInService.requestSilentSignIn(requireContext, emailAccount)
            startActivityForResult(intent, REQUEST_CODE_SIGN_IN)
        }

        btnSignInPicker.setOnClickListener {
            val intent = SignInService.requestSignInPicker(requireContext)
            startActivityForResult(intent, REQUEST_CODE_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> if (resultCode == AppCompatActivity.RESULT_OK) {
                resultData?.let {
                    SignInService.handleSignInResult(this, it)
                }
            } else {
                ToastUtils.showLongText(requireContext(), R.string.sign_in_canceled)
            }
        }
    }
}