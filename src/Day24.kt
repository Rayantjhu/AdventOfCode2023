import Day24.part1
import Day24.part2

/*
    I don't understand this very much :(, so I copied the code mainly from here:
    https://github.com/Jadarma/advent-of-code-kotlin-solutions/blob/main/solutions/aockt/y2023/Y2023D24.kt

    I should give credit where credit is due.
 */

fun main() {
    val input = readInputLines("Day24")
    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

private object Day24 {
    fun part1(input: List<String>): Int {
        val hailstones = parseInput(input)

        val range = 200000000000000.0..400000000000000.0
        return hailstones.distinctPairs()
            .mapNotNull { (h1, h2) -> intersect(h1, h2) }
            .count { (x, y) -> x in range && y in range }
    }

    fun parseInput(input: List<String>): List<Hailstone> {
        return input.map { line -> line.split(" @ ", ", ").map { it.replace(" ", "").toLong() } }.map {
            Hailstone(it[0], it[1], it[2], it[3], it[4], it[5])
        }
    }

    data class Hailstone(
        val x: Long,
        val y: Long,
        val z: Long,
        val vx: Long,
        val vy: Long,
        val vz: Long,
        val a: Long = vy,
        val b: Long = -vx,
        val c: Long = vy * x - vx * y,
    )

    private fun intersect(h1: Hailstone, h2: Hailstone): Pair<Double, Double>? {
        val (c1, c2) = listOf(h1, h2).map { it.c.toDouble() }
        val slopeDifference = h1.vy * h2.b - h2.vy * h1.b
        if (slopeDifference == 0L) return null

        val x = (c1 * h2.b - c2 * h1.b) / slopeDifference
        val y = (c2 * h1.a - c1 * h2.a) / slopeDifference

        val intersectsInFuture = listOf(
            (x - h1.x < 0) == (h1.vx < 0),
            (y - h1.y < 0) == (h1.vy < 0),
            (x - h2.x < 0) == (h2.vx < 0),
            (y - h2.y < 0) == (h2.vy < 0),
        ).all { it }

        return (x to y).takeIf { intersectsInFuture }
    }

    private fun rockVelocities(hailstones: List<Hailstone>): Sequence<Triple<Long, Long, Long>> =
        sequence {
            val amplitude = 250
            val velocityRange = -amplitude.toLong()..amplitude.toLong()
            val (invalidXRanges, invalidYRanges, invalidZRanges) = List(3) { mutableSetOf<LongRange>() }

            hailstones.distinctPairs().forEach { (h1, h2) ->
                testImpossible(invalidXRanges, h1.x, h1.vx, h2.x, h2.vx)
                testImpossible(invalidYRanges, h1.y, h1.vy, h2.y, h2.vy)
                testImpossible(invalidZRanges, h1.z, h1.vz, h2.z, h2.vz)
            }

            val possibleX = velocityRange.filter { x -> invalidXRanges.none { x in it } }
            val possibleY = velocityRange.filter { y -> invalidYRanges.none { y in it } }
            val possibleZ = velocityRange.filter { z -> invalidZRanges.none { z in it } }

            possibleX.forEach { vx ->
                possibleY.forEach { vy ->
                    possibleZ.forEach { vz ->
                        yield(Triple(vx, vy, vz))
                    }
                }
            }
        }

    private fun deduceThrowingLocation(
        h1: Hailstone,
        h2: Hailstone,
        velocity: Triple<Long, Long, Long>
    ): Triple<Long, Long, Long>? {
        val h1vx = h1.vx - velocity.first
        val h1vy = h1.vy - velocity.second
        val h2vx = h2.vx - velocity.first
        val h2vy = h2.vy - velocity.second

        val slopeDifference = h1vx * h2vy - h1vy * h2vx
        if (slopeDifference == 0L) return null

        val t = (h2vy * (h2.x - h1.x) - h2vx * (h2.y - h1.y)) / slopeDifference
        if (t < 0) return null

        val x = h1.x + (h1.vx - velocity.first) * t
        val y = h1.y + (h1.vy - velocity.second) * t
        val z = h1.z + (h1.vz - velocity.third) * t
        return Triple(x, y, z)
    }

    private fun willCollide(h1: Hailstone, h2: Hailstone): Boolean {
        val t = when {
            h1.vx != h2.vx -> (h2.x - h1.x).toDouble() / (h1.vx - h2.vx)
            h1.vy != h2.vy -> (h2.y - h1.y).toDouble() / (h1.vy - h2.vy)
            h1.vz != h2.vz -> (h2.z - h1.z).toDouble() / (h1.vz - h2.vz)
            else -> return false
        }
        return if (t < 0) false else posAfterTime(h1, t) == posAfterTime(h2, t)
    }

    fun posAfterTime(h1: Hailstone, time: Double): Triple<Double, Double, Double> = Triple(
        h1.x + h1.vx * time,
        h1.y + h1.vy * time,
        h1.z + h1.vz * time
    )

    fun testImpossible(ranges: MutableSet<LongRange>, p0: Long, v0: Long, p1: Long, v1: Long) {
        if (p0 > p1 && v0 > v1) ranges.add(v1..v0)
        if (p1 > p0 && v1 > v0) ranges.add(v0..v1)
    }

    fun part2(input: List<String>): Long {
        val hailstones = parseInput(input)
        val (h1, h2) = hailstones

        return rockVelocities(hailstones)
            .mapNotNull { velocity ->
                deduceThrowingLocation(h1, h2, velocity)?.let {
                    Hailstone(
                        it.first,
                        it.second,
                        it.third,
                        velocity.first,
                        velocity.second,
                        velocity.third
                    )
                }
            }
            .first { rock -> hailstones.all { willCollide(rock, it) } }
            .let { it.x + it.y + it.z }
    }
}