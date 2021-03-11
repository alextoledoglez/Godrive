package com.example.godrive.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.godrive.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import java.util.*

class SignInService {

    companion object {

        var driveService: Drive? = null
        var signInClient: GoogleSignInClient? = null
        private val TAG = SignInService::class.java.simpleName

        /**
         * Starts a sign-in activity using [.REQUEST_CODE_SIGN_IN].
         */
        fun requestSignIn(context: Context): Intent? {
            Log.d(TAG, "Requesting sign-in")
            val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Scope(DriveScopes.DRIVE_FILE))
                .build()
            signInClient = GoogleSignIn.getClient(context, signInOptions)
            return signInClient?.signInIntent
        }

        /**
         * Handles the `result` of a completed sign-in activity initiated from [ ][.requestSignIn].
         */
        fun handleSignInResult(fragment: Fragment, result: Intent) {
            GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener { googleAccount: GoogleSignInAccount ->
                    Log.d(TAG, "Signed in as " + googleAccount.email)
                    // Use the authenticated account to sign in to the Drive service.
                    val credential = GoogleAccountCredential.usingOAuth2(
                        fragment.context, Collections.singleton(DriveScopes.DRIVE_FILE)
                    )
                    credential.selectedAccount = googleAccount.account
                    driveService = Drive.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        GsonFactory(),
                        credential
                    ).setApplicationName("Drive Test").build()
                    findNavController(fragment).navigate(R.id.DriveFragment)
                }
                .addOnFailureListener { exception: Exception? ->
                    signInClient = null
                    driveService = null
                    Log.e(
                        TAG,
                        "Unable to sign in.",
                        exception
                    )
                }
        }
    }
}