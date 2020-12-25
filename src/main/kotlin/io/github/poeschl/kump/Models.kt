package io.github.poeschl.kump

import java.awt.Color
import java.util.*

data class Point(val x: Int, val y: Int) {

    fun plus(point: Point): Point {
        return Point(this.x + point.x, this.y + point.y)
    }
}

data class Pixel(val point: Point, val color: Color) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pixel

        if (point != other.point) return false

        return true
    }

    override fun hashCode(): Int {
        return point.hashCode()
    }
}

data class Area(val origin: Point, val endCorner: Point) {

    val width = endCorner.x - origin.x
    val height = endCorner.y - origin.y
}

class PixelMatrix(val width: Int, val height: Int) {

    private val dataArray = initDataArray()

    fun insertAll(pixels: Collection<Pixel>) {
        pixels.forEach { pixel -> insert(pixel) }
    }

    fun insert(pixel: Pixel) {
        val coord = pixel.point
        dataArray[coord.y][coord.x] = pixel
    }

    fun remove(point: Point) {
        dataArray[point.y][point.x] = null
    }

    fun processData(action: (Pixel) -> Unit) {
        dataArray
            .flatMap { it -> it.toList() }
            .filterNotNull()
            .forEach { action(it) }
    }

    override fun toString(): String {
        return Arrays.deepToString(dataArray)
    }

    private fun initDataArray(): Array<Array<Pixel?>> {
        return Array<Array<Pixel?>>(height + 1) { Array(width + 1) { null } }
    }
}
