package com.marcos.fittrack.data.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object PasosRepositorio {
    private val _pasosHoy = MutableLiveData(0)
    val pasosHoy: LiveData<Int> = _pasosHoy

    fun actualizar(pasos: Int) {
        _pasosHoy.postValue(pasos)
    }
}