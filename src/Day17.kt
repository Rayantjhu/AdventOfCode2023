import Day17.part1
import Day17.part2
import java.util.*

fun main() {
    val input = readInputLines("Day17")
    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

private object Day17 {
    val OPPOSITE_DIRECTIONS = mapOf(
        'L' to 'R',
        'R' to 'L',
        'U' to 'D',
        'D' to 'U'
    )

    /*
        One way to solve this problem is by using a so-called greedy algorithm. For each step, we check which next step
        is the best, this can be either left, right or straight. The lowest heat loss will be the step taken. The
        problem with the greedy approach is that sometimes you need to take a step that has a higher heat loss than
        others, to find a path that results in a lower heat loss.

        Dijkstra's algorithm is a way to fix the greedy way.
     */
    fun part1(input: List<String>): Int {
        val map = input.map { line -> line.map { it - '0' } }
        return findBestPath(map)
    }

    fun findBestPath(map: List<List<Int>>, isUltraCrucible: Boolean = false): Int {
        // The quadruple contains:
        //  Row,
        //  Column,
        //  Direction we are headed as a char, and
        //  Number of steps we have taken in the same direction
        val visited = mutableSetOf<Quadruple<Int, Int, Char, Int>>()

        // The quintuple also contains the heat loss as the first element, as opposed to the quadruple.
        // We set a custom comparator for the first element which is the heat loss. The lowest heat loss will always be
        // on the top of the queue.
        val queue = PriorityQueue<Quintuple<Int, Int, Int, Char, Int>> { t1, t2 -> t1.first - t2.first }
        // Starting node is added without any movement (empty direction)
        queue.add(Quintuple(0, 0, 0, ' ', 0))

        val mapLength = map.lastIndex
        val mapWidth = map.first().lastIndex
        while (queue.isNotEmpty()) {
            val (heatLoss, row, col, direction, steps) = queue.poll()

            // Stop if we have reached the destination and return its heat loss. This will always be the most optimal,
            // since we are using a priority queue. If it is an ultra crucible (part 2), we also check whether its steps
            // are at least 4 or higher. If it is false (part 1), we do not check for this.
            if (row == mapLength && col == mapWidth && (!isUltraCrucible || steps >= 4))
                return heatLoss

            // If the city has already been visited, we do not have to look at it anymore. The heat loss is not included
            // because, if we were to end up in a loop, the heat loss would always increase, meaning we would most
            // likely end up in an infinite loop.
            if (Quadruple(row, col, direction, steps) in visited) continue

            visited.add(Quadruple(row, col, direction, steps))

            // If it is an ultra crucible (part 2), then the steps should be lower than 10, otherwise lower than 3
            if ((isUltraCrucible && steps < 10 || !isUltraCrucible && steps < 3) && direction != ' ') {
                getNextIndices(mapLength, mapWidth, row, col, direction)?.let { (nextRow, nextCol) ->
                    queue.add(Quintuple(heatLoss + map[nextRow][nextCol], nextRow, nextCol, direction, steps + 1))
                }
            }

            // If it's an ultra crucible (part 2), then it the steps should be greater than 4 or an empty direction
            if (!isUltraCrucible || steps >= 4 || direction == ' ') {
                // Try each different direction
                for (nextDirection in listOf('L', 'R', 'U', 'D')) {
                    // We have already checked if going straight ahead is possible, and we can only go in 90-degree corners
                    if (nextDirection == direction || OPPOSITE_DIRECTIONS[direction] == nextDirection) continue

                    getNextIndices(mapLength, mapWidth, row, col, nextDirection)?.let { (nextRow, nextCol) ->
                        queue.add(Quintuple(heatLoss + map[nextRow][nextCol], nextRow, nextCol, nextDirection, 1))
                    }
                }
            }
        }

        return 0
    }

    /** Gets the next indices for the given location, if and only if the new indices are within range, null otherwise. */
    fun getNextIndices(mapLength: Int, mapWidth: Int, row: Int, col: Int, direction: Char): Pair<Int, Int>? {
        return when (direction) {
            'L' -> if (col - 1 in 0..mapWidth) row to col - 1 else null
            'R' -> if (col + 1 in 0..mapWidth) row to col + 1 else null
            'U' -> if (row - 1 in 0..mapLength) row - 1 to col else null
            'D' -> if (row + 1 in 0..mapLength) row + 1 to col else null
            else -> throw IllegalArgumentException("Unknown direction: $direction")
        }
    }

    fun part2(input: List<String>): Int {
        val map = input.map { line -> line.map { it - '0' } }
        return findBestPath(map, isUltraCrucible = true)
    }
}