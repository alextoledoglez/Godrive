package com.example.godrive.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.godrive.R
import com.example.godrive.services.SignInService

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
        view.findViewById<View>(R.id.button_sign_in).setOnClickListener {
            promptSignInDialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        when (requestCode) {
            REQUEST_CODE_SIGN_IN -> if (resultCode == AppCompatActivity.RESULT_OK) {
                resultData?.let {
                    SignInService.handleSignInResult(this, it)
                }
            }
        }
    }

    /**
     * Authenticate the user.
     * This should be done when the user performs an action that requires Drive access.
     * The result of the sign-in Intent is handled in onActivityResult.
     * */
    private fun promptSignInDialog() {
        startActivityForResult(
            SignInService.requestSignIn(requireContext()),
            REQUEST_CODE_SIGN_IN
        )
    }
}