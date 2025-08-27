package ita.tech.eveniment.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object EmiteNotificacionCalendario {

    private val _notificacion = MutableSharedFlow<Unit>()
    val notificacion = _notificacion.asSharedFlow()

    suspend fun enviarNotificacion(){
        _notificacion.emit(Unit)
    }
}