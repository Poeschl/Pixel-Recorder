package io.github.poeschl.kump

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

class Application(host: String, port: Int) {

    private val flutInterface = PixelFlutInterface(host, port)

    fun start() {
        val size = flutInterface.getPlaygroundSize()
        println("Dump size: $size")
        val pixels = getPixels(size)
        println(pixels)

        flutInterface.close()
    }

    private fun getPixels(size: Pair<Int, Int>): PixelMatrix {
        val matrix = PixelMatrix(size.first, size.second)

//        IntStream.rangeClosed(0, size.first)
//            .parallel()
//            .forEach { x ->
//                println("Read x $x")
//                IntStream.rangeClosed(0, size.second)
//                    .parallel()
//                    .forEach { y -> matrix.insert(flutInterface.getPixel(Point(x, y))) }
//            }
        flutInterface.getPixelArea(Point(10, 10), Point(20, 10))

        return matrix
    }
}

fun main(args: Array<String>) {
    ArgParser(args).parseInto(::Args).run {
        println("Dumping from $host:$port")
        Application(host, port).start()
    }
}

class Args(parser: ArgParser) {
    val host by parser.storing("--host", help = "The host of the pixelflut server").default("localhost")
    val port by parser.storing("-p", "--port", help = "The port of the server") { toInt() }.default(1234)
}
