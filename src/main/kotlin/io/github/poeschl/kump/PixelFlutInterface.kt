package io.github.poeschl.kump

import java.awt.Color
import java.io.*
import java.net.Socket

class PixelFlutInterface(address: String, port: Int) {

    companion object {
        private const val SIZE_COMMAND = "SIZE"
        private val SIZE_ANSWER_PATTERN = "SIZE (\\d+) (\\d+)".toRegex()
        private const val GET_PX_COMMAND = "PX %d %d"
        private val GET_PX_ANSWER_PATTERN = "PX (\\d+) (\\d+) (\\w+)".toRegex()
        private const val PAINT_PX_COMMAND = "PX %d %d %s"
    }

    private val socket = Socket(address, port)
    private val writer = PrintWriter(BufferedWriter(OutputStreamWriter(socket.getOutputStream())))
    private val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

    /**
     * Returns the size of the playground as a pair with width and height.
     *
     * @return Pair<width, height>
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
     * Retuns the Pixel on the given position.
     *
     * @return The pixel at the point.
     */
    fun getPixel(point: Point): Pixel {
        writer.println(GET_PX_COMMAND.format(point.x, point.y))
        writer.flush()

        val pixelAnswer = reader.readLine() ?: ""
        val matcher = GET_PX_ANSWER_PATTERN.matchEntire(pixelAnswer)

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

    fun getPixelArea(start: Point, end: Point) {
        val bufferSize = 500
        val sb = StringBuilder()
        for (i in 0 until bufferSize) {
            sb.append(String.format(GET_PX_COMMAND, 10, 10 + i))
            sb.append('\n')
        }
        writer.print(sb.toString())
        writer.flush()
    }

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
