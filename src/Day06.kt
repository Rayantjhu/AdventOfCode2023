import kotlin.math.ceil

fun main() {
    /*
        For each race we have to determine the amount of ways we can win the race. Multiply these numbers and we have
        the solution.

        There are two approaches:
        -   Brute-forcing by looking at each different possibility and checking whether it would be winning
        -   Knowing math, the middle (median) amount of time held of the total time will always result in the longest
            distance. The distribution is uniform, meaning if we know the first half of results, the remaining half can
            be filled in. Knowing both of these things, if we calculate the best distance, we can go back (or further)
            until the distance is lower than the minimum distance required to win the race. We than multiply the amount
            of observations * 2. The best will be included in the observations if the mean is two numbers, otherwise the
            best will not be included and instead 1 will be added to the result of doubling the observations.

        An example to show what has been previously said:

        Total time: 7, minimum distance: 9

        Time held:          Time moved:         Speed (mm/ms):          Distance
         0                  7                   0                       0
         1                  6                   1                       6
         2                  5                   2                       10
         3                  4                   3                       12          <-- middle
         4                  3                   4                       12          <-- middle
         5                  2                   5                       10
         6                  1                   6                       6
         7                  0                   7                       0

         In the above example the median is 3.5 (7 divided by 2), numbers 3 and 4. Starting from 4 and going further
         we only find a total of 2 observations where the distance is greater than 9 (including the best observation).
         So the amount of winning observations is 2 * 2 = 4.

         Total time: 30, min distance = 200

        Time held:          Time moved:         Speed (mm/ms):          Distance
        0                   30                  0                       0
        1                   29                  1                       29
        2                   28                  2                       56
        3                   27                  3                       81
        4                   26                  4                       104
        5                   25                  5                       125
        6                   24                  6                       144
        7                   23                  7                       161
        8                   22                  8                       176
        9                   21                  9                       189
        10                  20                  10                      200
        11                  19                  11                      209
        12                  18                  12                      216
        13                  17                  13                      221
        14                  16                  14                      224
        15                  15                  15                      225         <-- middle

        In the above example, only half of the observations are shown. Here the median is 15 (30 divided by 2), which is
        one number. We can see the amount of observations where the distance is greater than 200 is 4 times. This time
        we double the amount of observations and add 1 to get the solution: 4 * 2 + 1 = 9.
     */
    fun part1(input: List<String>): Int {
        // Parse the times and distances from the input to a list of integers
        val times = input.first().substringAfter(':').split(' ').filter { it.isNotBlank() }.map { it.toInt() }
        val distances = input.last().substringAfter(':').split(' ').filter { it.isNotBlank() }.map { it.toInt() }

        return times.zip(distances).map { (time, distance) ->
            // Use the ceiling of the division to find the 2nd median number if there are two
            val median = ceil(time / 2.0).toInt()

            // Calculate the amount of winning observations
            var winningObservations = 0
            while (calculateDistance(median - 1 - winningObservations, time) > distance) winningObservations++
            // Double it because we have only found the first half of observations
            winningObservations *= 2

            // If the median is one number, add 1 for the median itself
            if (time % 2 == 0) winningObservations + 1 else winningObservations
        }
            // Multiply each of the results with each other
            .reduce { acc, i -> acc * i }

    }

    /*
        Now there is only one time and one distance. Since we already do not brute-force it in the first part, the code
        can be re-used.
     */
    fun part2(input: List<String>): Int {
        val time = input.first().substringAfter(':').filter { !it.isWhitespace() }.toInt()
        val distance = input.last().substringAfter(':').filter { !it.isWhitespace() }.toLong()

        val median = ceil(time / 2.0).toInt()
        var winningObservations = 0
        while(calculateDistance(median - 1 - winningObservations, time.toLong()) > distance) winningObservations++
        winningObservations *= 2

        return if (time % 2 == 0) winningObservations + 1 else winningObservations
    }

    val input = readInputLines("Day06")
    part1(input).printFirstPart()
    part2(input).printSecondPart()
}

/** Helper function to calculate the distance based on the time held and the total time. */
fun calculateDistance(timeHeld: Int, totalTime: Int): Int {
    return timeHeld * (totalTime - timeHeld)
}

/** Helper function to calculate the distance based on the time held and the total time as long values. */
fun calculateDistance(timeHeld: Int, totalTime: Long): Long {
    return timeHeld * (totalTime - timeHeld)
}