package io.github.poeschl.kump

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import kotlinx.coroutines.*
import mu.KotlinLogging
import java.awt.image.BufferedImage
import java.io.File
import java.util.stream.IntStream
import javax.imageio.ImageIO
import kotlin.system.measureTimeMillis

class Application(host: String, port: Int) {

    companion object {
        private val LOGGER = KotlinLogging.logger { }
        private const val X_SPLIT = 20
        private const val Y_SPLIT = 20
    }

    private val flutInterface = PixelFlutInterface(host, port)

    fun start() {
        val size = flutInterface.getPlaygroundSize()
        LOGGER.info { "Dump size: $size" }
        val screenshotTime = measureTimeMillis {
            val imageMatrix = getPixels(size)
            writeSnapshot(imageMatrix, File("snapshot.png"))
        }

        LOGGER.debug { "Took snapshot in $screenshotTime ms" }
        flutInterface.close()
    }

    private fun getPixels(size: Pair<Int, Int>): PixelMatrix {
        val matrix = PixelMatrix(size.first, size.second)

        val xSlices = size.first / X_SPLIT
        val xPartitial = size.first % xSlices
        val ySlices = size.second / X_SPLIT
        val yPartitial = size.second % ySlices

        val areas = mutableListOf<Area>()
        IntStream.rangeClosed(0, X_SPLIT - 1).forEach { xIndex ->
            IntStream.rangeClosed(0, Y_SPLIT - 1).forEach { yIndex ->
                val origin = Point(xSlices * xIndex, ySlices * yIndex)
                val corner = if (xIndex == 0 && yIndex == 0) {
                    Point(origin.x + xSlices + xPartitial - 1, origin.y + ySlices + yPartitial - 1)
                } else {
                    Point(origin.x + xSlices - 1, origin.y + ySlices - 1)
                }
                areas.add(Area(origin, corner))
            }
        }

        runBlocking {
            val deferredData = areas
                .mapIndexed { index, area ->
                    async(newSingleThreadContext("Thread-Area-$index")) {
                        flutInterface.getPixelArea(area.origin, area.endCorner)
                    }
                }

            deferredData.forEach { deferred -> matrix.insertAll(deferred.await()) }
        }
        return matrix
    }

    private fun writeSnapshot(matrix: PixelMatrix, file: File) {
        val bufferedImage = BufferedImage(matrix.width, matrix.height, BufferedImage.TYPE_INT_RGB)
        val canvas = bufferedImage.graphics

        matrix.processData {
            canvas.color = it.color
            canvas.fillRect(it.point.x, it.point.y, 1, 1)
        }

        canvas.dispose()
        ImageIO.write(bufferedImage, file.extension, file)
    }
}

fun main(args: Array<String>) {
    System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG")
    ArgParser(args).parseInto(::Args).run {
        val logger = KotlinLogging.logger {}

        logger.info { "Dumping from $host:$port" }
        Application(host, port).start()
    }
}

class Args(parser: ArgParser) {
    val host by parser.storing("--host", help = "The host of the pixelflut server").default("localhost")
    val port by parser.storing("-p", "--port", help = "The port of the server") { toInt() }.default(1234)
}
