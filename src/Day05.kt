fun main() {
    /*
        We have to find the lowest location for our seeds. We can map each seed to its soil number, then map each soil
        number to each fertilizer number and so on. This way we will end up with only location numbers and find the
        lowest number, which will be the solution.
     */
    fun part1(input: List<String>): Long {
        // We start with the seed numbers
        var currentNumbers = input.first().substringAfter("seeds: ").split(' ').map { it.toLong() }.toTypedArray()

        // Find all indices where the mapping starts
        val mappingIndices = input.mapIndexedNotNull { index, line -> if (line.endsWith(':')) index else null }

        for (i in mappingIndices.indices) {
            // Get the starting and ending indices for the lines of numbers of the current mapping
            val startingIndex = mappingIndices[i] + 1
            val endingIndex = mappingIndices.getOrNull(i + 1)?.let { it - 1 } ?: input.lastIndex

            // Get the lines of numbers according to the starting and ending index
            val numberLines = input.subList(startingIndex, endingIndex)

            val newNumbers = arrayOfNulls<Long>(currentNumbers.size)
            numberLines.forEach { line ->
                // Get the starting numbers and the range length for the current line
                val numbers = line.split(' ').map { it.toLong() }
                val destinationStart = numbers[0]
                val sourceStart = numbers[1]
                val rangeLength = numbers[2]

                currentNumbers.forEachIndexed { index, number ->
                    // Add the mapped number if the source is within the range and the number has not been set
                    if (number in sourceStart..<sourceStart + rangeLength && newNumbers[index] == null)
                    // Get the destination number and add the difference of current number and source start
                        newNumbers[index] = destinationStart + (number - sourceStart)
                }
            }

            // Replace all null numbers (numbers that are not mapped) with their source number
            currentNumbers = newNumbers.mapIndexed { index, number -> number ?: currentNumbers[index] }.toTypedArray()
        }

        return currentNumbers.min()
    }

    /*
        Exactly the same as part1, but the only difference is that there are now a lot more seeds to begin with. One
        problem: the ranges are huge, so there will be millions of numbers in one list, you would need a ton of RAM
        to make this work (if it even will work). So the same approach brute-force approach will not work. Of course,
        one could not make a list of all seeds, but just loop through each range of seeds and do to same thing, but this
        still would take a long time to compute.

        Instead of using all possible seeds, ranges can be used. For each starting range, we find new mapped ranges.
     */
    fun part2(input: String): Long {
        val blocks = input.split("\n\n")

        // Get all ranges
        var seedRanges = blocks
            // Take the first line
            .first()
            // Remove the seeds part to only get the string of numbers
            .substringAfter("seeds: ")
            // Split the string into a list of numbers
            .split(' ')
            // Map value to a long
            .map { it.toLong() }
            // Divide the list into groups of the starting number and its range length
            .chunked(2)
            // Map into a list of LongRanges from the starting number until the end of the range (exclusive)
            .map { (start, range) -> start..<start + range }
            // Make it mutable so we can update its contents
            .toMutableList()

        // Get all mapping blocks
        val mappingBlocks = blocks
            // Drop the seeds part
            .drop(1)
            // For each mapping create a triple for each of its lines
            .map { mapping ->
                // Split the mapping into multiple lines and drop the first line and map the lines of numbers
                mapping.split('\n').drop(1).filter { it.isNotBlank() }.map { line ->
                    // Split into multiple numbers, map each to a long and place them into a Triple
                    val numbers = line.split(' ').map { it.toLong() }
                    // The numbers are the destination start, source start and range length respectively
                    Triple(numbers[0], numbers[1], numbers[2])
                }
            }

        mappingBlocks.forEach { mappingBlock ->
            val newRanges = mutableListOf<LongRange>()

            while (seedRanges.size > 0) {
                // Pop the first seed range
                val seedRange = seedRanges.removeFirst()

                // Keep track if an overlap has been found
                var overlapFound = false

                // Find the overlapping ranges for each mapping of the block
                for ((destinationStart, sourceStart, rangeLength) in mappingBlock) {
                    // Start of the overlap is the max of start of the seed or source range
                    val overlapStart = maxOf(seedRange.first, sourceStart)
                    // End of the overlap is the min of the end of the seed or source range
                    val overlapEnd = minOf(seedRange.last, sourceStart + rangeLength - 1)

                    // There is overlap between the seed ranges and the mapping ranges
                    if (overlapStart < overlapEnd) {
                        // Calculate the destination start and end and add it to the new ranges
                        newRanges.add(overlapStart - sourceStart + destinationStart..overlapEnd - sourceStart + destinationStart)

                        // Left part of the seedRange is not looked at, so re-add that part to the seedRanges
                        if (overlapStart > seedRange.first) seedRanges.add(seedRange.first..<overlapStart)
                        // Right part of the seedRange is not looked at, so re-add that part to the seedRanges
                        if (seedRange.last > overlapEnd) seedRanges.add(overlapEnd + 1..seedRange.last)

                        // An overlap has been found, no need to look further for the current range of seeds
                        overlapFound = true
                        break
                    }
                }

                // If no overlap has been found, re-add the seed range is there is no mapping for it
                if (!overlapFound) newRanges.add(seedRange)

            }
            // Set the ranges to the newly mapped ranges
            seedRanges = newRanges
        }

        // Find the lowest location number and return it
        return seedRanges.minOf { it.first }
    }

    val inputLines = readInputLines("Day05")
    part1(inputLines).printFirstPart()

    val input = readInput("Day05")
    part2(input).printSecondPart()
}