import kotlin.io.path.Path
import kotlin.io.path.readLines

/** Reads lines from the given input txt file. */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/** Helper function to print the solution for the first part of the puzzle */
fun Int.printFirstPart() = println("The solution for the first part is: $this")

/** Helper function to print the solution for the second part of the puzzle */
fun Int.printSecondPart() = println("The solution for second part is: $this")
