package com.example.godrive.utils

import android.content.Context
import android.os.Environment
import androidx.core.app.ActivityCompat.requestPermissions
import com.example.godrive.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel


class DataBaseUtils {

    private val DB_NAME = "TEST_DB"
    private val DB_BACKUP_FORMAT = "%s.bak"

    fun Context.importDB() {
        try {
            if (PermissionsUtils.haveStoragePermissionGranted(this)) {
                val sd: File = Environment.getExternalStorageDirectory()
                if (sd.canWrite()) {
                    val backupDB: File = this.getDatabasePath(DB_NAME)
                    val backupDBPath: String = java.lang.String.format(DB_BACKUP_FORMAT, DB_NAME)
                    val currentDB = File(sd, backupDBPath)
                    val src: FileChannel = FileInputStream(currentDB).channel
                    val dst: FileChannel = FileOutputStream(backupDB).channel
                    dst.transferFrom(src, 0, src.size())
                    src.close()
                    dst.close()
                    ToastUtils.showLongText(this, R.string.db_successful_imported)
                } else {
                    ToastUtils.showLongText(this, R.string.db_importing_failure)
                }
            } else {
                this.requestStoragePermissions()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun Context.exportDB() {
        try {
            if (PermissionsUtils.haveStoragePermissionGranted(this)) {
                val currentDB: File = this.getDatabasePath(DB_NAME)
                val src: FileChannel = FileInputStream(currentDB).channel
                val sd: File = Environment.getExternalStorageDirectory()
                if (sd.canWrite()) {
                    val backupDBPath: String = java.lang.String.format(DB_BACKUP_FORMAT, DB_NAME)
                    val backupDB = File(sd, backupDBPath)
                    val dst: FileChannel = FileOutputStream(backupDB).channel
                    dst.transferFrom(src, 0, src.size())
                    src.close()
                    dst.close()
                    ToastUtils.showLongText(this, R.string.db_successful_exported)
                } else {
                    ToastUtils.showLongText(this, R.string.db_exporting_failure)
                }
            } else {
                this.requestStoragePermissions()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun Context.requestStoragePermissions() {
        ActivityUtils.parse(this)?.let { activity ->
            requestPermissions(
                activity,
                PermissionsUtils.STORAGE_PERMISSIONS,
                PermissionsUtils.STORAGE_PERMISSION_CODE
            )
        }
    }
}