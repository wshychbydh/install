package com.eye.cool.install.ui

import android.annotation.TargetApi
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import com.eye.cool.install.R

/**
 * Request permissions.
 * Created cool on 2018/4/16.
 */
@TargetApi(Build.VERSION_CODES.M)
internal class PermissionActivity : DialogActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (sRequestPermissionListener == null) {
      showInstallSettingDialog()
    } else {
      val permissions = intent.getStringArrayExtra(REQUEST_PERMISSIONS)
      requestPermissions(permissions, REQUEST_PERMISSION_CODE)
    }
  }

  private fun showInstallSettingDialog() {
    val message = getString(R.string.permission_install_packages_setting_rationale, getAppName(this))
    AlertDialog.Builder(this)
        .setCancelable(false)
        .setTitle(R.string.permission_title_rationale)
        .setMessage(message)
        .setPositiveButton(R.string.permission_setting) { _, _ ->
          val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName"))
          startActivityForResult(intent, REQUEST_INSTALL_PACKAGES_CODE)
        }
        .setNegativeButton(R.string.permission_no) { _, _ -> sRequestInstallPackageListener?.invoke(false) }
        .show()
  }

  private fun getAppName(context: Context): String {
    val appInfo = context.packageManager.getApplicationInfo(context.packageName, 0) ?: return ""
    return context.packageManager.getApplicationLabel(appInfo) as? String ?: ""
  }

  override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
    return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event)
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    if (requestCode == REQUEST_PERMISSION_CODE) {
      val denied = grantResults.filter { it == PackageManager.PERMISSION_DENIED }
      sRequestPermissionListener?.invoke(denied.isEmpty())
      finish()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == REQUEST_INSTALL_PACKAGES_CODE) {
      sRequestInstallPackageListener?.invoke(resultCode == RESULT_OK)
      finish()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    sRequestPermissionListener = null
    sRequestInstallPackageListener = null
  }

  companion object {

    private const val REQUEST_PERMISSION_CODE = 3001
    private const val REQUEST_INSTALL_PACKAGES_CODE = 4001
    private const val REQUEST_PERMISSIONS = "request_permissions"

    private var sRequestPermissionListener: ((Boolean) -> Unit)? = null
    private var sRequestInstallPackageListener: ((Boolean) -> Unit)? = null

    @TargetApi(Build.VERSION_CODES.O)
    fun requestInstall(context: Context, callback: ((Boolean) -> Unit)? = null) {
      sRequestInstallPackageListener = callback
      val intent = Intent(context, PermissionActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      context.startActivity(intent)
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermission(context: Context, permissions: Array<String>, callback: ((Boolean) -> Unit)? = null) {
      sRequestPermissionListener = callback
      val intent = Intent(context, PermissionActivity::class.java)
      intent.putExtra(REQUEST_PERMISSIONS, permissions)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
      context.startActivity(intent)
    }
  }
}