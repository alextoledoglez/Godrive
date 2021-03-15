package com.example.godrive.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.godrive.R
import com.example.godrive.adapters.RecordsListAdapter
import com.example.godrive.data.AppDatabase
import com.example.godrive.data.dao.PersonDao
import com.example.godrive.data.models.Person
import com.example.godrive.services.DriveService
import com.example.godrive.services.SignInService
import com.example.godrive.utils.DataBaseUtils
import com.example.godrive.utils.ToastUtils
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class DriveFragment : Fragment() {

    private val TAG = DriveFragment::class.java.simpleName

    private var driveFileId: String? = null
    private var driveService: DriveService? = null
    private var sharedPreferences: SharedPreferences? = null

    private var appDatabase: AppDatabase? = null
    private var personDao: PersonDao? = null
    private var persons: ArrayList<Person> = ArrayList()

    private var recyclerView: RecyclerView? = null
    private var recyclerAdapter: RecyclerView.Adapter<*>? = null
    private var recyclerLayoutManager: RecyclerView.LayoutManager? = null

    private val unableToUploadFileToDrive = "Unable to upload file to drive."
    private val unableToDownloadFileFromDrive = "Unable to download file from drive."
    private val sharedPreferencesDriveFileId = "SHARED_PREFERENCES_DRIVE_FILE_ID"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drive, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // The DriveServiceHelper encapsulates all REST API and SAF functionality.
        // Its instantiation is required before handling any onClick actions.
        driveService = DriveService(SignInService.driveService)
        sharedPreferences = requireContext().defaultSharedPreferences

        loadData()

        recyclerAdapter = RecordsListAdapter(appDatabase, persons)
        recyclerLayoutManager = LinearLayoutManager(requireContext())
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_records).apply {
            adapter = recyclerAdapter
            layoutManager = recyclerLayoutManager
        }

        view.findViewById<ImageButton>(R.id.add_btn).setOnClickListener {
            setData()
        }

        view.findViewById<ImageButton>(R.id.backup_btn).setOnClickListener {
            uploadToDrive()
        }

        view.findViewById<ImageButton>(R.id.restore_btn).setOnClickListener {
            downloadFromDrive()
        }
    }

    private fun loadData() {
        appDatabase = AppDatabase.getInstance(requireContext())
        personDao = appDatabase?.personDao()
        doAsync {
            personDao?.selectAll()?.let { persons.addAll(it) }
        }
    }

    private fun setData() {
        val listToBeSaved: ArrayList<Person> = ArrayList()

        val personOne = Person()
        personOne.name = "First person name"
        val personTwo = Person()
        personTwo.name = "Second person name"
        val personThree = Person()
        personThree.name = "Third person name"
        val personFour = Person()
        personFour.name = "Four person name"

        listToBeSaved.add(personOne)
        listToBeSaved.add(personTwo)
        listToBeSaved.add(personThree)
        listToBeSaved.add(personFour)

        doAsync {
            personDao?.clearTable()
            persons.clear()
            uiThread {
                recyclerAdapter?.notifyDataSetChanged()
            }
            val result = personDao?.insert(listToBeSaved)
            uiThread {
                if (result?.isNotEmpty() == true && result.all { it > 0 }) {
                    persons.addAll(listToBeSaved)
                    recyclerAdapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun uploadToDrive() {
        DataBaseUtils.exportDBFrom(requireContext())?.let { file ->
            driveService?.uploadToDrive(file)?.addOnSuccessListener {
                driveFileId = it
                sharedPreferences?.edit()?.putString(sharedPreferencesDriveFileId, driveFileId)
                    ?.apply()
            }?.addOnFailureListener {
                Log.e(TAG, unableToUploadFileToDrive, it)
                ToastUtils.showLongText(requireContext(), unableToUploadFileToDrive)
            }
        }
    }

    private fun downloadFromDrive() {
        driveFileId = sharedPreferences?.getString(sharedPreferencesDriveFileId, null)
        val context = requireContext()
        context.getExternalFilesDir(DataBaseUtils.FILE_DIRECTORY_TYPE)
            ?.let { directoryFile ->
                driveService?.downloadFromDrive(driveFileId, directoryFile)
                    ?.addOnSuccessListener {
                        appDatabase?.let {
                            if (it.isOpen) {
                                it.close()
                            }
                        }
                        DataBaseUtils.importDBFrom(context)
                        loadData()
                    }?.addOnFailureListener {
                        Log.e(TAG, unableToDownloadFileFromDrive, it)
                        ToastUtils.showLongText(requireContext(), unableToDownloadFileFromDrive)
                    }
            }
    }
}