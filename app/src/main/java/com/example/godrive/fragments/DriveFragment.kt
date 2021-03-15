package com.example.godrive.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.godrive.MainActivity
import com.example.godrive.R
import com.example.godrive.adapters.RecordsListAdapter
import com.example.godrive.data.dao.PersonDao
import com.example.godrive.data.models.Person
import com.example.godrive.services.DriveService
import com.example.godrive.services.SignInService
import com.example.godrive.utils.DataBaseUtils
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class DriveFragment : Fragment() {

    private val TAG = DriveFragment::class.java.simpleName

    private var driveService: DriveService? = null
    private var dataBaseBackupFile: File? = null

    private var personDao: PersonDao? = null
    private var persons: ArrayList<Person> = ArrayList()

    private var recyclerView: RecyclerView? = null
    private var recyclerAdapter: RecyclerView.Adapter<*>? = null
    private var recyclerLayoutManager: RecyclerView.LayoutManager? = null

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

        personDao = MainActivity.appDatabase?.personDao()
        doAsync {
            personDao?.selectAll()?.let { persons.addAll(it) }
        }

        recyclerAdapter = RecordsListAdapter(persons)
        recyclerLayoutManager = LinearLayoutManager(requireContext())
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_records).apply {
            adapter = recyclerAdapter
            layoutManager = recyclerLayoutManager
        }

        view.findViewById<ImageButton>(R.id.add_btn).setOnClickListener {

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
                val result = personDao?.insert(listToBeSaved)
                uiThread {
                    if (result?.isNotEmpty() == true && result.all { it > 0 }) {
                        persons.addAll(listToBeSaved)
                        recyclerAdapter?.notifyDataSetChanged()
                    }
                }
            }
        }

        view.findViewById<ImageButton>(R.id.backup_btn).setOnClickListener {
            DataBaseUtils.exportDBFrom(requireContext())?.let {
                dataBaseBackupFile = it
                driveService?.createDriveFileFrom(it)
            }
        }

        view.findViewById<ImageButton>(R.id.restore_btn).setOnClickListener {
            dataBaseBackupFile?.let {
                val fileUri: Uri? = it.toURI() as Uri?
                openFileFromFilePicker(requireContext(), fileUri)
            }
        }
    }

    /**
     * Opens a file from its `uri` returned from the Storage Access Framework file picker
     * initiated by [.openFilePicker].
     */
    private fun openFileFromFilePicker(context: Context, uri: Uri?) {
        Log.d(TAG, "Opening " + uri?.path)
        driveService?.openFileUsingStorageAccessFramework(context.contentResolver, uri)
            ?.addOnSuccessListener { nameAndContent ->
                val name: String? = nameAndContent?.first
                val content: String? = nameAndContent?.second
                // Files opened through SAF cannot be modified.
                DataBaseUtils.importDBFrom(requireContext())
            }
            ?.addOnFailureListener { exception ->
                Log.e(
                    TAG,
                    "Unable to open file from picker.",
                    exception
                )
            }
    }

    /**
     * Opens the Storage Access Framework file picker using [.REQUEST_CODE_OPEN_DOCUMENT].
     */
/*    private fun openFilePicker(): Intent? {
        Log.d(TAG, "Opening file picker.")
        return driveService?.createFilePickerIntent()
    }*/

    /**
     * Retrieves the title and content of a file identified by `fileId` and populates the UI.
     */
/*    private fun readFile(fileId: String?) {
        Log.d(TAG, "Reading file $fileId")
        driveService?.readFile(fileId)
            ?.addOnSuccessListener { nameAndContent ->
                val name: String? = nameAndContent?.first
                val content: String? = nameAndContent?.second
                fileTitleEditText?.setText(name)
                docContentEditText?.setText(content)
                setReadWriteMode(fileId)
            }
            ?.addOnFailureListener { exception ->
                Log.e(
                    TAG,
                    "Couldn't read file.",
                    exception
                )
            }
    }*/

    /**
     * Queries the Drive REST API for files visible to this app and lists them in the content view.
     */
/*    private fun query() {
        Log.d(TAG, "Querying for files.")
        driveService?.queryFiles()?.addOnSuccessListener { fileList ->
            val builder = StringBuilder()
            fileList?.files?.forEach { builder.append(it.name).append("\n") }
            val fileNames = builder.toString()
            fileTitleEditText?.setText(R.string.file_list)
            docContentEditText?.setText(fileNames)
            setReadOnlyMode()
        }?.addOnFailureListener { exception ->
            Log.e(
                TAG,
                "Unable to query files.",
                exception
            )
        }
    }*/
}