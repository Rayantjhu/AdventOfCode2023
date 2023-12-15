import Day10.part1
import Day10.part2

fun main() {
    val input = readInputLines("Day10")

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

    /*
        For the second part, we do not have to keep track of the steps taken, instead this time we need to know of which
        tiles the loop consists of. This way we can use ray casting
        (see: https://en.wikipedia.org/wiki/Point_in_polygon#Ray_casting_algorithm) for each dot of each row and check
        if it is in the loop, if it is, increment a counter.
     */
    fun part2(input: List<String>): Int {
        val startLineIndex = input.indexOfFirst { it.contains('S') }
        val startTileIndex = input[startLineIndex].indexOfFirst { it == 'S' }

        val visitedTiles = mutableListOf(
            Pair(startLineIndex, startTileIndex),
            findNextTile(input, startLineIndex, startTileIndex, Pair(-1, -1))
        )

        while (visitedTiles.last() != visitedTiles.first()) {
            val currentTile = visitedTiles.last()
            val previousTile = visitedTiles[visitedTiles.lastIndex - 1]
            visitedTiles.add(findNextTile(input, currentTile.first, currentTile.second, previousTile))
        }

        // Find the correct pipe for the starting pipe S
        val correctStartPipe = correctStartPipe(
            listOf(visitedTiles[1], visitedTiles[visitedTiles.lastIndex - 1]),
            visitedTiles.first()
        )

        // Only these are crossing pipes, include the start pipe as a symbol if it is one of the crossing pipes.
        // We do not select '-' as a pipe, for example, because this does not count as a cross, rather we are still on
        // the same line.
        val crossingPipes =
            if (correctStartPipe in listOf('|', 'L', 'J')) listOf('|', 'L', 'J', 'S') else listOf('|', 'L', 'J')

        var count = 0
        input.forEachIndexed { lineIndex, line ->
            var crossings = 0
            line.indices.forEach { tileIndex ->
                if (visitedTiles.contains(Pair(lineIndex, tileIndex))) {
                    // If the current tile is part of the loop, and it is an edge pipe, increment the crossings
                    if (input[lineIndex][tileIndex] in crossingPipes) crossings++
                }
                // If the current tile is not part of the loop and the number of crossings is odd, it means this tile is
                // inside the loop
                else if (crossings % 2 == 1) count++
            }
        }

        return count
    }

    private fun correctStartPipe(
        connectingPipes: List<Pair<Int, Int>>,
        startPipe: Pair<Int, Int>
    ): Char {
        val first = connectingPipes.first()
        val second = connectingPipes.last()

        val firstDirections = getDirections(startPipe, first)
        val secondDirections = getDirections(startPipe, second)

        return firstDirections.intersect(secondDirections.toSet()).first()
    }

    private fun getDirections(start: Pair<Int, Int>, end: Pair<Int, Int>): List<Char> {
        // End is north
        return if (end.first - 1 == start.first) listOf('|', 'J', 'L')
        // End is south
        else if (end.first + 1 == start.first) listOf('|', 'F', '7')
        // End is east
        else if (end.second + 1 == start.second) listOf('-', 'F', 'L')
        // End is west
        else listOf('-', '7', 'J')
    }
}