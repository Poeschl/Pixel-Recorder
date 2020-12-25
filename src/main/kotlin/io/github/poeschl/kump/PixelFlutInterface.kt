package io.github.poeschl.kump

import mu.KotlinLogging
import java.awt.Color
import java.io.PrintWriter
import java.net.Socket
import java.util.stream.IntStream
import kotlin.system.measureTimeMillis

class PixelFlutInterface(address: String, port: Int, private val pixelBufferSize: Int = 1000) {

    companion object {
        private val LOGGER = KotlinLogging.logger {}
        private const val SIZE_COMMAND = "SIZE"
        private val SIZE_ANSWER_PATTERN = "SIZE (\\d+) (\\d+)".toRegex()
        private const val GET_PX_COMMAND = "PX %d %d"
        private val GET_PX_ANSWER_PATTERN = "PX (\\d+) (\\d+) (\\w+)".toRegex()
        private const val PAINT_PX_COMMAND = "PX %d %d %s"
    }

    private val socket = Socket(address, port)
    private val writer = PrintWriter(socket.getOutputStream().bufferedWriter())
    private val reader = socket.getInputStream().bufferedReader()

    /**
     * Returns the size of the playground as a pair with width and height.
     *
     * @return Pair<width, height> The size of the screen.
     */
    fun getPlaygroundSize(): Pair<Int, Int> {
        writer.println(SIZE_COMMAND)
        writer.flush()

        val sizeAnswer = reader.readLine() ?: ""
        val matchResult = SIZE_ANSWER_PATTERN.matchEntire(sizeAnswer)

        return if (matchResult != null) {
            val groups = matchResult.groupValues
            Pair(Integer.parseInt(groups[1]), Integer.parseInt(groups[2]))
        } else {
            Pair(0, 0)
        }
    }

    /**
     * Returns the Pixel on the given position.
     *
     * @return The pixel at the point.
     */
    fun getPixel(point: Point): Pixel {
        writer.println(GET_PX_COMMAND.format(point.x, point.y))
        writer.flush()

        val pixelAnswer = reader.readLine() ?: ""
        return convertToPixel(pixelAnswer)
    }

    /**
     * Returns the Pixel inside the given rectangle.
     *
     * @return The pixels inside the area.
     */
    fun getPixelArea(start: Point, end: Point): Set<Pixel> {

        val requests = mutableListOf<Point>()
        IntStream.rangeClosed(start.x, end.x).forEach { x ->
            IntStream.rangeClosed(start.y, end.y).forEach { y ->
                requests.add(Point(x, y))
            }
        }
        val chunks = requests.chunked(pixelBufferSize)

        LOGGER.debug { "Request ($start -> $end) in ${chunks.size} parts" }

        var totalNetwork = 0L
        var totalInput = 0L
        val results = mutableSetOf<Pixel>()

        chunks.forEach { chunk ->
            val sb = StringBuilder()
            var responses: List<String>

            val networkTime = measureTimeMillis {
                chunk.forEach { point ->
                    sb.append(String.format(GET_PX_COMMAND, point.x, point.y))
                    sb.append('\n')
                }
                writer.print(sb.toString())
                writer.flush()

                responses = chunk.indices.map { reader.readLine() }.toList()
            }

            val inputTime = measureTimeMillis {
                responses.forEach { string ->
                    results.add(convertToPixel(string))
                }
            }

            totalNetwork += networkTime
            totalInput += inputTime
        }

        LOGGER.debug {
            "Get pixel area ($start -> $end | ${requests.size} Pixels):" +
                    " Network: $totalNetwork ms, Inputparsing: $totalInput ms"
        }

        return results
    }

    /**
     * Set one pixel at the given position.
     *
     * @param pixel The pixel to draw on screen.
     */
    fun drawPixel(pixel: Pixel) {
        writer.println(PAINT_PX_COMMAND.format(pixel.point.x, pixel.point.y, convertColorToHex(pixel.color)))
        writer.flush()
    }

    /**
     * Draw an collection of pixels.
     *
     * @param pixels A collection of pixels to draw.
     */
    fun drawPixels(pixels: Set<Pixel>) {

        val chunks = pixels.chunked(pixelBufferSize)
        LOGGER.debug { "Sending ${pixels.size} pixels in ${chunks.size} parts" }

        var totalNetwork = 0L

        chunks.forEach { chunk ->
            val sb = StringBuilder()

            val networkTime = measureTimeMillis {
                chunk.forEach { pixel ->
                    sb.append(String.format(PAINT_PX_COMMAND, pixel.point.x, pixel.point.y, convertColorToHex(pixel.color)))
                    sb.append('\n')
                }
                writer.print(sb.toString())
                writer.flush()
            }

            totalNetwork += networkTime
        }

        LOGGER.debug {
            "Pushed ${pixels.size} out in $totalNetwork ms"
        }
    }

    /**
     * Close all open connections.
     */
    fun close() {
        writer.close()
        reader.close()
        socket.close()
    }

    private fun convertColorToHex(color: Color): String {
        return String.format("%02X%02X%02X", color.red, color.green, color.blue)
    }

    private fun convertHexToColor(hex: String): Color {
        val red = hex.substring(0..1)
        val green = hex.substring(2..3)
        val blue = hex.substring(4..5)

        return Color(Integer.parseInt(red, 16), Integer.parseInt(green, 16), Integer.parseInt(blue, 16))
    }

    private fun convertToPixel(answer: String): Pixel {
        val matcher = GET_PX_ANSWER_PATTERN.matchEntire(answer)

        return if (matcher != null) {
            val groups = matcher.groupValues
            return Pixel(
                Point(
                    Integer.parseInt(groups[1]),
                    Integer.parseInt(groups[2])
                ),
                convertHexToColor(groups[3])
            )
        } else {
            Pixel(Point(0, 0), Color.BLACK)
        }
    }
}
