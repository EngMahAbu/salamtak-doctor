package com.salamtak.salamtakdoctor.salamtak_doctor.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.salamtak.R
import com.salamtak.databinding.ActivitySelectLocationBinding
import com.salamtak.domain.entities.resquest.auth.DoctorLocation
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.HelperDialog
import com.salamtak.salamtakdoctor.salamtak_doctor.utils.dialogs.LoadingDialog
import java.io.Serializable
import java.util.*


class SelectLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivitySelectLocationBinding
    private lateinit var mMap: GoogleMap
    var myLocation: LatLng?=null
    private var myLatLng: LatLng? = null
    private lateinit var geocode: Geocoder
    private lateinit var loadingDialog: LoadingDialog
    lateinit var mContext: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_location)

        geocode = Geocoder(applicationContext, Locale("ar"))//.getDefault())

        val mapFragment = //(context!! as AppCompatActivity).supportFragmentManager
            supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mContext = binding.root.context
        loadingDialog = LoadingDialog(mContext)
        loadingDialog.show()

        Handler().postDelayed({
            loadingDialog.dismiss()

        }, 4000)

        binding.tvAdd.setOnClickListener {

            if (myLocation == null) {
                toast(
                    applicationContext,
                    getString(R.string.select_your_location_first)
                )
                return@setOnClickListener
            }

            val addresses: List<Address>? =
                geocode.getFromLocation(myLocation!!.latitude, myLocation!!.longitude, 1)

            if (addresses != null && addresses.isNotEmpty()) {

                val address = addresses[0]//.getAddressLine(0).split("،")

//                for (i in address.indices)
//                    Log.e(TAG, "onCreate: i $i  add. ${address[i]}")

                val location = DoctorLocation.Location(
                    city = address.adminArea,
                    country = address.countryName,
                    details = address.getAddressLine(0),
                    district = if(address.subAdminArea.isNullOrEmpty()) "" else address.subAdminArea,//[address.size - 3],
                    myLocation!!.latitude,
                    myLocation!!.longitude,
                )

                val intent = Intent()
                intent.putExtra(DOCTOR_LOCATION, location as Serializable)

                setResult(RESULT_OK, intent)
                finish()

            }


        }


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap!!
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        checkLocationPermissions()

//        mMap.setOnCameraIdleListener { //get lat-lng at the center by calling
//            myLocation = mMap.cameraPosition.target
//
//            setLocationUI(myLocation)
//
//        }
//
        mMap.setOnMapClickListener {
            myLocation = mMap.cameraPosition.target

//            myLocation = mMap.cameraPosition.target

            mMap.clear()

//            mMap.animateCamera(
//                CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 18f)
//            )


            mMap.addMarker(MarkerOptions().position(it))
            setLocationUI(myLocation)

        }

    }

    private fun checkLocationPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    RequestCode.LOCATION_REQUEST_CODE
                )

                Log.e(TAG, "checkLocationPermissions: 1")
            } else {
                Log.e(TAG, "checkLocationPermissions: 2")
                goToMyLocation()

            }

        } else {
            goToMyLocation()
        }

    }

    private fun setLocationUI(myLocation: LatLng?) {

        try {
            val addresses: List<Address> =
                myLocation?.let { geocode.getFromLocation(it.latitude, myLocation.longitude, 1) }!!
//            geocode.getFromLocation(myLocation.latitude, myLocation.longitude, 1) as List<Address>



            if (addresses != null && addresses.isNotEmpty()) {

                mMap.clear()
                mMap.addMarker(MarkerOptions().position(myLocation))

                Log.e(TAG, "onMapReady6: ${addresses} ")

                binding.tvAddress.text = addresses[0].getAddressLine(0)

            }
        } catch (e: Exception) {

            Log.e(TAG, "setLocationUI: ${e.message} ==> ${e.cause}")

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            goToMyLocation()
        } else {
            toast(this, getString(R.string.location_per))
        }
    }

    @SuppressLint("MissingPermission")
    private fun goToMyLocation() {

        mMap.isMyLocationEnabled = true

        val locationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager


        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }

        val locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {

                if (myLatLng != null) return

                myLatLng = LatLng(
                    location.latitude,
                    location.longitude
                )

                myLocation = myLatLng!!
                setLocationUI(myLocation)


                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                            myLocation!!.latitude,
                            myLocation!!.longitude
                        ), 18f
                    )
                )

            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0,
            0f,
            locationListener
        )
    }

    private fun buildAlertMessageNoGps() {
        val d = HelperDialog(null)
        d.isCancelable = false
        d.show(supportFragmentManager, "")
    }

    companion object {
        private var TAG = this::class.java.name
    }

}