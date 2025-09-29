package structures


class LinkList<T>() : Iterable<T> {

    inner class Node(
        var value: T,
        var next: Node? = null
    )

    private var head: Node? = null

    fun add(value: T) {
        val n = Node(value)
        if (head == null) {
            head = n
        } else {
            var cur = head
            while (cur?.next != null) {
                cur = cur.next
            }
            cur?.next = n
        }
    }

    fun remove(value: T) {
        var cur = head
        var prev = head
        while (cur?.value != value) {
            prev = cur
            cur = cur?.next
        }
        if (head == cur) {
            head = cur.next
            cur.next = null
        } else {
            prev?.next = cur.next
            cur.next = null
        }
    }

    fun size(): Int {
        if (head == null) return 0
        var i = 1
        var cur = head
        while (cur?.next != null) {
            cur = cur.next
            i++
        }
        return i
    }

    override fun toString(): String {
        val str = StringBuilder()
        var cur = head
        str.append(cur?.value.toString())
        while (cur?.next != null) {
            cur = cur.next
            str.append("->${cur?.value.toString()}")
        }
        return str.toString()
    }

    inner class LLIterator : Iterator<T?> {
        private var nextNodeToReturn: Node? = head

        override fun hasNext(): Boolean {
            return nextNodeToReturn != null
        }

        override fun next(): T? {
            if (!hasNext()) {
                throw NoSuchElementException("Iterator exceeded.")
            }
            val ret: T? = nextNodeToReturn?.value
            nextNodeToReturn = nextNodeToReturn?.next
            return ret
        }

    }

    override fun iterator(): Iterator<T> {
        return LLIterator() as Iterator<T>
    }
}