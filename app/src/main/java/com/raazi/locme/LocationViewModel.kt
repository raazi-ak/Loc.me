package com.raazi.locme

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LocationViewModel: ViewModel(){
    private val _location = mutableStateOf<LocationData?>(null)
    val location: State<LocationData?> = _location

    fun setLocation(newLocationData: LocationData){
        _location.value = newLocationData
    }
}