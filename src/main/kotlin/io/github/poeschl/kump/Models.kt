package io.github.poeschl.kump

import java.awt.Color
import java.util.concurrent.ConcurrentHashMap

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

    private val data = ConcurrentHashMap<Point, Color>(height * width)

    fun insertAll(pixels: Collection<Pixel>) {
        pixels.forEach { pixel -> insert(pixel) }
    }

    fun insert(pixel: Pixel) {
        val coord = pixel.point
        data[coord] = pixel.color
    }

    fun remove(point: Point) {
        data.remove(point)
    }

    fun processData(action: (Pixel) -> Unit) {
        data
            .map { Pixel(it.key, it.value) }
            .forEach { action(it) }
    }

    fun processData(action: (Point, Color) -> Unit) {
        data.forEach { action(it.key, it.value) } ;
    }

    override fun toString(): String {
        return data.toString()
    }
}
