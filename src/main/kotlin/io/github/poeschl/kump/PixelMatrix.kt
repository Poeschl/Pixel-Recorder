package io.github.poeschl.kump

import java.util.*

class PixelMatrix(private val xSize: Int, private val ySize: Int) {

    private val dataArray = initDataArray()

    fun insert(pixel: Pixel) {
        val coord = pixel.point
        dataArray[coord.y][coord.x] = pixel
    }

    fun remove(point: Point) {
        dataArray[point.y][point.x] = null
    }

    override fun toString(): String {
        return Arrays.deepToString(dataArray)
    }

    private fun initDataArray(): Array<Array<Pixel?>> {
        return Array<Array<Pixel?>>(ySize + 1) { Array(xSize + 1) { null } }
    }
}
