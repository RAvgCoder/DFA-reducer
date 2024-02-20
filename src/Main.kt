import java.io.File
import java.io.FileNotFoundException
import java.util.*

val states = mutableListOf(
    State(1, mutableListOf()), // Final
    State(0, mutableListOf())  // Non-Final
)
val nodes = mutableListOf<Node>()

val table = mutableListOf<List<Pair<Int, Int>>>()

val stateQueue: Queue<State> = LinkedList(listOf(states[1], states[0]))


fun main() {
    createNodes()

    addIterationStateToTheTable()


    while (stateQueue.isNotEmpty()) {
        evaluateClass(stateQueue.remove())
        addIterationStateToTheTable()
    }

    println("Num of classes are: ${states.size}")
    table.forEachIndexed { index, pairs ->
        println("-------- Iteration $index ---------")
        pairs.forEach(::println)
        println()
    }


    nodes.onEach { println(it.state.name) }

    writeTableToFile()
}

fun addIterationStateToTheTable() {
    table.add(nodes.map { findClass(it.nodeA()).name to findClass(it.nodeB()).name })
}

fun evaluateClass(currState: State) {
    val nodesGroups = currState.nodes.map { node: Node ->
        node to Pair(findClass(node.nodeA()).name, findClass(node.nodeB()).name)
    }.groupBy { it.second }
        .toList()
        .apply {
            println("----------- Current Class {$currState} ------------")
            this.onEach { listPair ->
                listPair.second.onEach { println("Key: ${it.second}     \tValue: ${it.first}") }
            }
        }

    if (nodesGroups.size > 1) {
        // Flush current nodes in state
        currState.nodes.clear()

        // Add back the first subdivided class
        currState.nodes.addAll(nodesGroups.first().second.map { it.first }.toMutableList())

        val new = mutableListOf<State>()
        // All other subdivisions should be made into their own classes
        for (ng in nodesGroups.drop(1)) {
            val newClass = newClasses(ng.second.map { it.first })
            stateQueue.add(newClass)
            states.add(newClass)
            new.add(newClass)
        }

        stateQueue.add(currState)
        println("Divided into ${new.map { it.name() }.toMutableList().apply { this.add(currState.name()) }}\n\n")
        return
    }
    println("not divided\n\n")
}

fun newClasses(nodesForNewClass: List<Node>): State {
    val newClass = State(states.size, mutableListOf())
    newClass.nodes.addAll(nodesForNewClass.onEach { it.state = newClass }.toMutableList())
    return newClass
}

fun findClass(node: Node): State {
    for (clas in states) {
        if (clas.nodes.contains(node)) return clas
    }
    throw IllegalStateException("Cannot find class for $node")
}

fun createNodes() {
    // Specify the file path
    val filePath = "dfa_nodes.txt"

    // Create a File instance
    val file = File(filePath)

    // Check if the file exists
    if (!file.exists()) {
        throw FileNotFoundException("File not found: $filePath")
    }

    var rawNodes: List<Pair<Int, Int>> = emptyList();
    try {
        // Open the file for reading using a BufferedReader
        val bufferedReader = file.bufferedReader().apply {
            // Read the file line by line
            this.useLines { lines ->
                rawNodes = lines.map { line ->
                    val num = line.split(":").map { it.trim().toInt() }
                    num[0] to num[1]
                }.toList()
            }
            // Close the BufferedReader
        }.close()

    } catch (e: Exception) {
        println("Error reading the file: ${e.message}")
    }

    rawNodes.indices.forEach { i -> nodes.add(Node(i, mutableListOf(), mutableListOf(), states[0])) }

    rawNodes.forEachIndexed { index, pair ->
        nodes[index].a.add(nodes[pair.first])
        nodes[index].b.add(nodes[pair.second])
        if (index < 2) nodes[index].state = states[1]
    }

    nodes.forEach {
        it.state.nodes.add(it)
    }
}

fun writeTableToFile() {
    val fileName = "Table.txt" // Specify the file name

    // Create a File instance
    val outputFile = File(fileName)

    // Write content to the file
    try {
        // Open the file for writing using a BufferedWriter
        val bufferedWriter = outputFile.bufferedWriter()

        // Write content to the file
        bufferedWriter.write("Num of classes are: ${states.size}\n")
        table.forEachIndexed { index, pairs ->
            bufferedWriter.write("-------- Iteration $index ---------\n")
            pairs.forEach { bufferedWriter.write("$it\n") }
            bufferedWriter.write("\n")
        }

        // Close the BufferedWriter
        bufferedWriter.close()

        println("Data has been written to $fileName.")
    } catch (e: Exception) {
        println("Error writing to the file: ${e.message}")
    }
}

class Node(val name: Int, val a: MutableList<Node>, val b: MutableList<Node>, var state: State) {

    fun name(): String {
        return "S$name"
    }

    fun nodeA() = a.first()
    fun nodeB() = b.first()

    override fun equals(other: Any?): Boolean {
        return if (other is Node) other.name == this.name
        else false
    }


    override fun hashCode(): Int {
        return name
    }

//    override fun toString(): String {
//        return "Node_Name: S$name Connectors: { a:${nodeA().name()}, b:${nodeB().name()} } Class: { $class_ }"
//    }

    override fun toString(): String {
        return "Node_Name: S$name (${nodeA().name()}, ${nodeB().name()})"
    }
}

class State(val name: Int, val nodes: MutableList<Node>) {


    fun name(): String {
        return "C$name"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is State) other.name == this.name
        else false
    }

    override fun hashCode(): Int {
        return name
    }

    override fun toString(): String {
        return "Class_Name: C$name Nodes:${nodes.map { it.name() }}"
    }

//    override fun toString(): String {
//        return "Class_Name: C$name"
//    }
}