package io.github.poeschl.kump

import java.util.*

class PixelMatrix(val width: Int, val height: Int) {

    private val dataArray = initDataArray()

    fun insertAll(pixels: List<Pixel>) {
        pixels.forEach { pixel -> insert(pixel) };
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
