package sbg.feedme.arrow.input

import io.reactivex.Observable
import io.reactivex.functions.Consumer
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket

interface Listener {
    fun listen(): Observable<String>
}

class SocketListener : Listener{
    override fun listen(): Observable<String> {
        return Observable.create { emitter ->
            val socket = Socket("localhost", 8282)
            val inFromServer = BufferedReader(InputStreamReader(socket.getInputStream()))
            while (true) {
                emitter.onNext(inFromServer.readLine())
            }
        }
    }

}

fun trimWhitespace(str: String) = str.trim()
fun trimPipes(str: String) = str.trim('|')
fun split(str: String) = "|".toRegex(RegexOption.LITERAL).split(str)

//}

fun main(args: Array<String>) {
    val socketListener = SocketListener()
    socketListener
            .listen()
            .map { trimWhitespace(it) }
            .map { trimPipes(it) }
            .map { split(it) }
            .subscribe {
                println(it)
            }


}

