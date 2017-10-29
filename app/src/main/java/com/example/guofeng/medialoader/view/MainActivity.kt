package com.example.guofeng.medialoader.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.MemoryCategory
import com.example.guofeng.medialoader.R
import com.example.guofeng.medialoader.app.GlideApp
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    companion object {
        private val REQUEST_READ_STORAGE = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setGlideHighMemoryCategory()
        if (isRequestPermissionApi23()) {
            requestStoragePermission()
        } else {
            addFragment()
        }
    }

    private fun setGlideHighMemoryCategory() {
        GlideApp.get(this).setMemoryCategory(MemoryCategory.HIGH)
    }

    private fun isRequestPermissionApi23()
            = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_READ_STORAGE)
    }

    @SuppressLint("ResourceType")
    private fun addFragment() {
        var fragment = supportFragmentManager.findFragmentById(R.layout.fragment_gallery)
        if (fragment == null) {
            fragment = GalleryFragment()
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.container, fragment)
                    .commit()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_READ_STORAGE ->
                // if request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addFragment()
                } else {
                    toast("Storage permission is required")
                    requestStoragePermission()
                }
        }
    }
}
