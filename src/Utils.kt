import kotlin.io.path.Path
import kotlin.io.path.readLines
import kotlin.io.path.readText

/** Reads lines from the given input txt file. */
fun readInputLines(name: String) = Path("src/$name.txt").readLines()

fun readInput(name: String) = Path("src/$name.txt").readText()

/** Helper function to print the solution for the first part of the puzzle */
fun Number.printFirstPart() = println("The solution for the first part is: $this")

/** Helper function to print the solution for the second part of the puzzle */
fun Number.printSecondPart() = println("The solution for second part is: $this")
