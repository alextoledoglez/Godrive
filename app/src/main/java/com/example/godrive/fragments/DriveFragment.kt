package com.example.godrive.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.godrive.R
import com.example.godrive.services.DriveService
import com.example.godrive.services.SignInService

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class DriveFragment : Fragment() {

    private val REQUEST_CODE_OPEN_DOCUMENT = 2
    private val TAG = DriveFragment::class.java.simpleName

    private var openFileId: String? = null
    private var fileTitleEditText: EditText? = null
    private var docContentEditText: EditText? = null
    private var driveService: DriveService? = null

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

        // Store the EditText boxes to be updated when files are opened/created/modified.
        // Store the EditText boxes to be updated when files are opened/created/modified.
        fileTitleEditText = view.findViewById(R.id.file_title_edittext)
        docContentEditText = view.findViewById(R.id.doc_content_edittext)

        // Set the onClick listeners for the button bar.
        // The result of the SAF Intent is handled in onActivityResult.
        view.findViewById<View>(R.id.open_btn).setOnClickListener {
            startActivityForResult(
                openFilePicker(),
                REQUEST_CODE_OPEN_DOCUMENT
            )
        }
        view.findViewById<View>(R.id.create_btn).setOnClickListener { createFile() }
        view.findViewById<View>(R.id.save_btn).setOnClickListener { saveFile() }
        view.findViewById<View>(R.id.query_btn).setOnClickListener { query() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        when (requestCode) {
            REQUEST_CODE_OPEN_DOCUMENT -> if (resultCode == AppCompatActivity.RESULT_OK) {
                resultData?.data?.let {
                    openFileFromFilePicker(requireContext(), it)
                }
            }
        }
    }

    /**
     * Updates the UI to read-only mode.
     */
    private fun setReadOnlyMode() {
        fileTitleEditText?.isEnabled = false
        docContentEditText?.isEnabled = false
        openFileId = null
    }

    /**
     * Updates the UI to read/write mode on the document identified by `fileId`.
     */
    private fun setReadWriteMode(fileId: String?) {
        fileTitleEditText?.isEnabled = true
        docContentEditText?.isEnabled = true
        openFileId = fileId
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
                fileTitleEditText?.setText(name)
                docContentEditText?.setText(content)
                // Files opened through SAF cannot be modified.
                setReadOnlyMode()
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
    private fun openFilePicker(): Intent? {
        Log.d(TAG, "Opening file picker.")
        return driveService?.createFilePickerIntent()
    }

    /**
     * Creates a new file via the Drive REST API.
     */
    private fun createFile() {
        Log.d(TAG, "Creating a file.")
        driveService?.createFile()?.addOnSuccessListener { fileId -> readFile(fileId) }
            ?.addOnFailureListener { exception ->
                Log.e(
                    TAG,
                    "Couldn't create file.",
                    exception
                )
            }
    }

    /**
     * Retrieves the title and content of a file identified by `fileId` and populates the UI.
     */
    private fun readFile(fileId: String?) {
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
    }

    /**
     * Saves the currently opened file created via [.createFile] if one exists.
     */
    private fun saveFile() {
        Log.d(TAG, "Saving $openFileId")
        val fileName: String = fileTitleEditText?.text.toString()
        val fileContent: String = docContentEditText?.text.toString()
        driveService?.saveFile(openFileId, fileName, fileContent)
            ?.addOnFailureListener { exception ->
                Log.e(
                    TAG,
                    "Unable to save file via REST.",
                    exception
                )
            }
    }

    /**
     * Queries the Drive REST API for files visible to this app and lists them in the content view.
     */
    private fun query() {
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
    }
}