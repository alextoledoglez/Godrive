package com.example.godrive.utils

import android.content.Context
import android.os.Environment
import androidx.core.app.ActivityCompat.requestPermissions
import com.example.godrive.R
import com.example.godrive.data.AppDatabase
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel


class DataBaseUtils {

    companion object {

        private val DB_NAME = AppDatabase.getDatabaseName()
        private val DB_BACKUP_FORMAT = "%s.bak"

        fun importDBFrom(context: Context) {
            try {
                if (PermissionsUtils.haveStoragePermissionGranted(context)) {
                    val sd: File = Environment.getExternalStorageDirectory()
                    if (sd.canWrite()) {
                        val backupDB: File = context.getDatabasePath(DB_NAME)
                        val backupDBPath: String =
                            java.lang.String.format(DB_BACKUP_FORMAT, DB_NAME)
                        val currentDB = File(sd, backupDBPath)
                        val src: FileChannel = FileInputStream(currentDB).channel
                        val dst: FileChannel = FileOutputStream(backupDB).channel
                        dst.transferFrom(src, 0, src.size())
                        src.close()
                        dst.close()
                        ToastUtils.showLongText(context, R.string.db_successful_imported)
                    } else {
                        ToastUtils.showLongText(context, R.string.db_importing_failure)
                    }
                } else {
                    requestStoragePermissionsFrom(context)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun exportDBFrom(context: Context): File? {
            try {
                if (PermissionsUtils.haveStoragePermissionGranted(context)) {
                    val currentDB: File = context.getDatabasePath(DB_NAME)
                    val src: FileChannel = FileInputStream(currentDB).channel
                    val sd: File = Environment.getExternalStorageDirectory()
                    if (sd.canWrite()) {
                        val backupDBPath: String =
                            java.lang.String.format(DB_BACKUP_FORMAT, DB_NAME)
                        val backupDB = File(sd, backupDBPath)
                        val dst: FileChannel = FileOutputStream(backupDB).channel
                        dst.transferFrom(src, 0, src.size())
                        src.close()
                        dst.close()
                        ToastUtils.showLongText(context, R.string.db_successful_exported)
                        return backupDB
                    } else {
                        ToastUtils.showLongText(context, R.string.db_exporting_failure)
                    }
                } else {
                    requestStoragePermissionsFrom(context)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        private fun requestStoragePermissionsFrom(context: Context) {
            ActivityUtils.parse(context)?.let { activity ->
                requestPermissions(
                    activity,
                    PermissionsUtils.STORAGE_PERMISSIONS,
                    PermissionsUtils.STORAGE_PERMISSION_CODE
                )
            }
        }
    }
}