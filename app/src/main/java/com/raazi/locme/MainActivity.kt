package com.raazi.locme

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.raazi.locme.ui.theme.LocmeTheme
import android.Manifest
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: LocationViewModel = viewModel()
            LocmeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun MyApp(viewModel: LocationViewModel){
    val context = LocalContext.current
    val locationUtils = LocationUtils(context)
    LocationDisplay(locationUtils = locationUtils,
        viewModel = viewModel
        ,context = context)
}


@Composable
fun LocationDisplay(locationUtils: LocationUtils,
                    viewModel: LocationViewModel,
                    context: Context){

    val location = viewModel.location.value
    val address = location?.let {
        locationUtils.reverseGeocodeLocation(location)
    }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {permissions ->
           if(permissions[Manifest.permission.ACCESS_FINE_LOCATION]==true
               &&
               permissions[Manifest.permission.ACCESS_COARSE_LOCATION]==true){
                locationUtils.requestLocationUpdates(viewModel)
           }
            else {
               val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                   context as MainActivity,
                   Manifest.permission.ACCESS_COARSE_LOCATION
               ) || ActivityCompat.shouldShowRequestPermissionRationale(
                   context as MainActivity,
                   Manifest.permission.ACCESS_FINE_LOCATION
               )
               if (!rationaleRequired){
                   Toast.makeText(context, "Location access denied.", Toast.LENGTH_SHORT).show()
               }
           }
        })
    Column(modifier= Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center ){
        if(location !=null){
            Text("Address: ${location.latitude} ${location.longitude} \n " +
                    "$address")
        }
        else {
            Text("Location Not Available")
        }
        Button(onClick = {
            if(locationUtils.hasLocationPermission(context)){
                locationUtils.requestLocationUpdates(viewModel)
            }
            else{
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }){
                Text("Request Location")
        }
    }
}


