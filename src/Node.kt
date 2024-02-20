// Class representing a node in the DFA, with connections to other nodes via transitions 'a' and 'b'
class Node(val name: Int, val a: MutableList<Node>, val b: MutableList<Node>, var state: State) {

    // Function to generate a string representation of the node
    fun name(): String {
        return "S$name"
    }

    // Function to get the first node connected via transition 'a'
    fun nodeA() = a.first()

    // Function to get the first node connected via transition 'b'
    fun nodeB() = b.first()

    // Override equals method to compare nodes based on their names
    override fun equals(other: Any?): Boolean {
        return if (other is Node) other.name == this.name
        else false
    }

    // Override hashCode method to generate hash code based on node's name
    override fun hashCode(): Int {
        return name
    }

    // Override toString method to generate a string representation of the node
    override fun toString(): String {
        return "Node_Name: S$name (${nodeA().name()}, ${nodeB().name()})"
    }
}