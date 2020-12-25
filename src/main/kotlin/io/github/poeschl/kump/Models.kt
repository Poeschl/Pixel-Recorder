package io.github.poeschl.kump

import java.awt.Color

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
