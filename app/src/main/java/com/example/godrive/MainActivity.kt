package com.example.godrive

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.godrive.data.AppDatabase
import com.example.godrive.services.SignInService


class MainActivity : AppCompatActivity() {

    companion object {
        var appDatabase: AppDatabase? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        appDatabase = AppDatabase.getInstance(applicationContext)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_sign_out -> {
                SignInService.signInClient?.signOut()?.addOnSuccessListener {
                    supportFragmentManager.primaryNavigationFragment?.let {
                        findNavController(it.id).navigate(R.id.SignInFragment)
                    }
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}