import Day22.part1
import Day22.part2
import java.util.*

fun main() {
    val input = readInputLines("Day22")
    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

private object Day22 {
    fun part1(input: List<String>): Int {
        val bricks = readInput(input).toMutableList()
        val (keySupportsValue, valueSupportsKey) = getSupports(bricks)

        val lambda = { i: Int ->
            if (keySupportsValue[i]?.all { j -> valueSupportsKey[j]?.size?.let { it >= 2 } == true } == true) 1
            else 0
        }
        return bricks.indices.sumOf(lambda)
    }

    fun readInput(input: List<String>): List<Pair<Point3D, Point3D>> {
        return input.map { line ->
            line
                .split('~')
                .map { end -> end.split(',').map { it.toInt() }.let { Point3D(it[0], it[1], it[2]) } }
                .let { (left, right) -> left to right }
        }.sortedBy { it.first.z }
    }

    fun overlaps(a: Pair<Point3D, Point3D>, b: Pair<Point3D, Point3D>): Boolean {
        return maxOf(a.first.x, b.first.x) <= minOf(a.second.x, b.second.x) &&
                maxOf(a.first.y, b.first.y) <= minOf(a.second.y, b.second.y)
    }

    fun getSupports(bricks: List<Pair<Point3D, Point3D>>): Pair<Map<Int, Set<Int>>, Map<Int, Set<Int>>> {
        for ((i, brick) in bricks.withIndex()) {
            var maxZ = 1
            for (check in bricks.slice(0..<i)) if (overlaps(brick, check)) maxZ = maxOf(maxZ, check.second.z + 1)
            brick.second.z -= brick.first.z - maxZ
            brick.first.z = maxZ
        }

        bricks as MutableList
        bricks.sortBy { it.first.z }

        val keySupportsValue = bricks.indices.associateWith { mutableSetOf<Int>() }
        val valueSupportsKey = bricks.indices.associateWith { mutableSetOf<Int>() }

        for ((j, upperBrick) in bricks.withIndex()) {
            for ((i, lowerBrick) in bricks.slice(0..<j).withIndex()) {
                if (overlaps(lowerBrick, upperBrick) && upperBrick.first.z == lowerBrick.second.z + 1) {
                    keySupportsValue[i]?.add(j)
                    valueSupportsKey[j]?.add(i)
                }
            }
        }

        return keySupportsValue to valueSupportsKey
    }

    data class Point3D(var x: Int, var y: Int, var z: Int)


    fun part2(input: List<String>): Int {
        val bricks = readInput(input)
        val (keySupportsValue, valueSupportsKey) = getSupports(bricks)

        var total = 0
        for (i in bricks.indices) {
            val queue: Queue<Int> = LinkedList()
            queue.addAll(keySupportsValue[i]?.filter { j -> valueSupportsKey[j]?.size == 1 } ?: listOf())

            val falling = queue.toMutableSet()
            falling.add(i)

            while (queue.isNotEmpty()) {
                val j = queue.poll()
                for (k in (keySupportsValue[j] ?: continue) - falling) {
                    if (valueSupportsKey[k]?.all { it in falling } == true) {
                        queue.add(k)
                        falling.add(k)
                    }
                }
            }
            total += falling.size - 1
        }

        return total
    }
}