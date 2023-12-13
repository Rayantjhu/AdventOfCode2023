import Day10.part1
import Day10.part2

fun main() {
    val input = readInputLines("Day10")

    val testOne = """
        -L|F7
        7S-7|
        L|7||
        -L-J|
        L|-JF
    """.trimIndent().split('\n')

    val testTwo = """
        7-F7-
        .FJ|7
        SJLL7
        |F--J
        LJ.LJ
    """.trimIndent().split('\n')

    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

object Day10 {
    /*
        We have to find the farthest point from the starting point. This can be done by traversing the whole loop and
        then dividing the steps by 2, this gives the solution.

        Traversing might prove to be challenging. We can tackle this problem by looking for each adjacent tile and
        whether it is traversable or not. A tile is traversable when it connects to the direction we are looking at,
        i.e.: looking at the tile north of the current one, the north tile should connect south to anything
        (|: south and north, 7: south and west, F: south and east).

        There's one problem, we might be stuck in an infinite loop if we don't keep track of where we have previously
        been. To prevent the infinite loop, we keep track of what tile has been visited previously.
     */
    fun part1(input: List<String>): Int {
        // Find the starting indices
        var currentLineIndex = input.indexOfFirst { it.contains('S') }
        var currentTileIndex = input[currentLineIndex].indexOfFirst { it == 'S' }

        var steps = 1
        var previousTile = Pair(-1, -1)
        // Find the next tile
        findNextTile(input, currentLineIndex, currentTileIndex, previousTile).let {
            // Set the previous tile to the tile before the current
            previousTile = Pair(currentLineIndex, currentTileIndex)
            currentLineIndex = it.first
            currentTileIndex = it.second
        }
        // Stop once we're back at the start
        while (input[currentLineIndex][currentTileIndex] != 'S') {
            steps++
            findNextTile(input, currentLineIndex, currentTileIndex, previousTile).let {
                previousTile = Pair(currentLineIndex, currentTileIndex)
                currentLineIndex = it.first
                currentTileIndex = it.second
            }
        }

        return steps / 2
    }

    /** Finds the next tile for the given line and tile indices.  */
    private fun findNextTile(input: List<String>, line: Int, tile: Int, previous: Pair<Int, Int>): Pair<Int, Int> {
        // North tile from the previous line with the same tile index
        val northTile = input.getOrNull(line - 1)?.get(tile)
        // East tile is from the same line but the next tile index
        val eastTile = input[line].getOrNull(tile + 1)
        // South tile is from the next line with the same tile index
        val southTile = input.getOrNull(line + 1)?.get(tile)
        // West tile is from the same line but the previous tile index
        val westTile = input[line].getOrNull(tile - 1)

        // All pipes that connect to their corresponding direction
        val northConnectors = listOf('|', '7', 'F', 'S')
        val eastConnectors = listOf('-', 'J', '7', 'S')
        val southConnectors = listOf('|', 'L', 'J', 'S')
        val westConnectors = listOf('-', 'L', 'F', 'S')

        val north = Pair(line - 1, tile)
        val east = Pair(line, tile + 1)
        val south = Pair(line + 1, tile)
        val west = Pair(line, tile - 1)

        val current = input[line][tile]
        // Return if the corresponding tile is a connector, the current tile allows the connection, and it has not been traversed
        return if (northTile in northConnectors && current in listOf('|', 'L', 'J', 'S') && north != previous) north
        else if (eastTile in eastConnectors && current in listOf('-', 'L', 'F', 'S') && east != previous) east
        else if (southTile in southConnectors && current in listOf('|', '7', 'F', 'S') && south != previous) south
        else if (westTile in westConnectors && current in listOf('-', 'J', '7', 'S') && west != previous) west
        else Pair(0, 0)
    }

    fun part2(input: List<String>): Int {
        return 0
    }
}