package iammert.com.app

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.common.api.ResolvableApiException
import iammert.com.live_location.LocationData
import iammert.com.live_location.LocationLiveData
import iammert.com.live_location.PermissionUtils

class MainActivity : AppCompatActivity() {

    private lateinit var locationLiveData: LocationLiveData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationLiveData = LocationLiveData(this)
        locationLiveData.observe(this,
                Observer {
                    when (it?.status) {
                        LocationData.Status.PERMISSION_REQUIRED -> requestPermissions(it.permissionList)
                        LocationData.Status.ENABLE_SETTINGS -> enableLocationSettings(it.resolvableApiException)
                    }
                })

        locationLiveData.start()
    }

    private fun requestPermissions(permissionList: Array<String?>) {
        ActivityCompat.requestPermissions(this, permissionList, REQUEST_CODE_LOCATION_PERMISSION)
    }

    private fun enableLocationSettings(exception: ResolvableApiException?) {
        exception?.startResolutionForResult(this, REQUEST_CODE_LOCATION_SETTINGS)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (PermissionUtils.checkIfPermissionsGranted(grantResults)) {
            locationLiveData.start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_LOCATION_SETTINGS && resultCode == Activity.RESULT_OK) {
            locationLiveData.start()
        }
    }

    companion object {
        const val REQUEST_CODE_LOCATION_PERMISSION = 12
        const val REQUEST_CODE_LOCATION_SETTINGS = 13
    }
}
