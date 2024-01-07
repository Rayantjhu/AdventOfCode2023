import Day21.part1
import Day21.part2
import java.util.*

fun main() {
    val input = readInputLines("Day21").filter { it.isNotBlank() }
    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

private object Day21 {
    fun part1(input: List<String>): Int {
        // Get the starting row and col
        val row = input.indexOfFirst { it.contains('S') }
        val col = input[row].indexOfFirst { it == 'S' }

        // Find all possible positions for the starting position
        var currentPositions = findPossiblePositions(row to col, input)
        // Loop 63 times, we already did the first.
        // For each of the current positions, find the next possible positions and remove any duplicates
        for (x in 1..63) currentPositions = currentPositions.flatMap { findPossiblePositions(it, input) }.distinct()

        // Return the number of garden plots we are on after 64 steps
        return currentPositions.size
    }

    fun findPossiblePositions(pos: Pair<Int, Int>, input: List<String>): List<Pair<Int, Int>> {
        val lastRow = input.lastIndex
        val lastCol = input.first().lastIndex
        val (row, col) = pos

        // Create a list of all possible coordinates
        return listOf(
            row - 1 to col, // North
            row to col + 1, // East
            row + 1 to col, // South
            row to col - 1, // West
        ).filter { (row, col) ->
            // The row and col have to be in range, and it has to point to a garden plot or the starting point.
            row in 0..lastRow && col in 0..lastCol && input[row][col] in "S."
        }
    }

    /*
        Don't ask me how this one works, I tried.
     */
    fun part2(input: List<String>): Long {
        val startRow = input.indexOfFirst { it.contains('S') }
        val startCol = input[startRow].indexOfFirst { it == 'S' }
        val rowLength = input.size
        val colLength = input.first().length

        val maxSteps = 26501365L
        var delta = 0L
        var skip = 0L

        val queue: Queue<Triple<Int, Int, Long>> = LinkedList()
        queue.add(Triple(startRow, startCol, 0))
        val visited = hashSetOf<Pair<Int, Int>>()
        val cycle = colLength * 2

        var lastStep = 0L
        var previousPlots = 0L
        var delta1 = 0L
        var delta2 = 0L
        var plots = 0L

        while (queue.isNotEmpty()) {
            val (row, col, step) = queue.poll()

            val position = row to col
            if (position in visited) continue

            if (step % 2 == 1L) visited.add(position)
            if (step % cycle == 66L && step > lastStep) {
                lastStep = step
                if (plots - previousPlots - delta1 == delta2) {
                    delta = plots - previousPlots + delta2
                    skip = step - 1
                    break
                }
                delta2 = (plots - previousPlots) - delta1
                delta1 = plots - previousPlots
                previousPlots = plots
            }
            plots = visited.size.toLong()
            val directions = setOf(row + 1 to col, row - 1 to col, row to col + 1, row to col - 1)
                .filter { (row, col) -> input[Math.floorMod(row, rowLength)][Math.floorMod(col, colLength)] != '#' }
                .map { Triple(it.first, it.second, step + 1) }
            queue.addAll(directions)
        }

        while (skip < maxSteps) {
            skip += cycle
            plots += delta
            delta += delta2
        }

        return plots
    }

}