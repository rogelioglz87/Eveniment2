package ita.tech.eveniment.socket

import io.socket.client.IO
import io.socket.client.Socket
import ita.tech.eveniment.util.Constants.Companion.SOCKET_URL
import ita.tech.eveniment.util.Constants.Companion.TOKEN_IO
import java.net.URISyntaxException
import java.util.Collections

object SocketHandler {

    lateinit var mSocket: Socket

    @Synchronized
    fun setSocket() {
        try {
            val opciones = IO.Options.builder()
                .setAuth(Collections.singletonMap("token", TOKEN_IO))
                // Esta opcion indica que el tipo de conexion sea por websocket
                // .setTransports(arrayOf(io.socket.engineio.client.transports.WebSocket.NAME))
                .build()
            mSocket = IO.socket(SOCKET_URL, opciones)
        } catch (e: URISyntaxException) {
            println("Error Socket: ${e.message}")
        }
    }

    @Synchronized
    fun getSocket(): Socket {
        return mSocket
    }

    @Synchronized
    fun establishConnection() {
        mSocket.connect()
    }

    @Synchronized
    fun closeConnection() {
        mSocket.disconnect()
    }

}