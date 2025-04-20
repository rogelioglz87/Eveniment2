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
            mSocket = IO.socket(SOCKET_URL, IO.Options.builder().setAuth(Collections.singletonMap("token", TOKEN_IO)).build())
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