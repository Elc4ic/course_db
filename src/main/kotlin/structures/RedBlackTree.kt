package structures

import data.LogsFile
import entities.Instance
import entities.ReportField
import structures.RedBlackTree.Node

class RedBlackTree<K : Comparable<K>>() {

    enum class TreeColor {
        RED, BLACK
    }

    inner class Node(
        var key: K? = null,
        val duplicates: LinkList<Int> = LinkList(),
        var color: TreeColor = TreeColor.RED,
        var left: Node? = null,
        var right: Node? = null,
        var parent: Node? = null,
    ) {
        constructor(key: K) : this() {
            this.key = key
            this.left = Node(color = TreeColor.BLACK)
            this.right = Node(color = TreeColor.BLACK)
        }

        operator fun compareTo(other: Node): Int {
            return this.key!!.compareTo(other.key!!)
        }

        override operator fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is RedBlackTree<K>.Node) return false
            return this.key == other.key
        }

        override fun toString(): String {
            return "${this.key} ${this.color} ${this.duplicates}"
        }
    }

    private var root: Node = Node()

    private fun Node?.isNullLeaf() = this != null && key == null
    private fun Node.uncleL(): Node? = this.parent?.parent?.left
    private fun Node.uncleR(): Node? = this.parent?.parent?.right
    private fun Node.grand(): Node? = this.parent?.parent

    private fun Node.bratR(to: Node? = null): Node? {
        if (to != null) this.parent?.right = to
        return this.parent?.right
    }

    private fun Node.bratL(to: Node? = null): Node? {
        if (to != null) this.parent?.left = to
        return this.parent?.left
    }

    private fun cutNode(before: Node, after: Node) {
        when {
            before.parent.isNullLeaf() -> root = after
            before == before.bratL() -> before.parent?.left = after
            else -> before.parent?.right = after
        }
        after.parent = before.parent
    }

    private fun minimum(node: Node): Node {
        var z = node
        while (!z.left.isNullLeaf()) {
            z = z.left!!
        }
        return z
    }

    private fun leftRotate(x: Node) {
        val y = x.right
        x.right = y?.left
        y?.left.let { it?.parent = x }
        y?.parent = x.parent
        when {
            x.parent.isNullLeaf() -> root = y!!
            x == x.bratL() -> x.bratL(y)
            else -> x.bratR(y)
        }
        y?.left = x
        x.parent = y
    }

    private fun rightRotate(x: Node) {
        val y = x.left
        x.left = y?.right
        y?.right.let { it?.parent = x }
        y?.parent = x.parent
        when {
            y?.parent.isNullLeaf() -> root = y!!
            x == x.bratR() -> x.bratR(y)
            else -> x.bratL(y)
        }
        y?.right = x
        x.parent = y
    }

    fun add(key: K, n: Int) {
        val newNode = Node(key)
        var y = root
        var x = root
        while (!x.isNullLeaf()) {
            y = x
            x = if (newNode == x) {
                x.duplicates.add(n)
                return
            } else (if (newNode > x) x.right else x.left)!!
        }
        if (newNode == y) {
            y.duplicates.add(n)
            return
        } else newNode.duplicates.add(n)
        newNode.parent = y
        if (y.isNullLeaf()) root = newNode
        else if (newNode < y) y.left = newNode
        else y.right = newNode
        fixAdd(newNode)
    }

    private fun fixAdd(node: Node) {
        var z = node
        while (z.parent?.color == TreeColor.RED && z != root) {
            if (z.parent == z.uncleL()) {
                val uncle = z.uncleR()
                if (uncle?.color == TreeColor.RED) {
                    uncle.color = TreeColor.BLACK
                    z.parent?.color = TreeColor.BLACK
                    z.grand()?.color = TreeColor.RED
                    z = z.grand()!!
                } else {
                    if (z == z.bratR()) {
                        z = z.parent!!
                        leftRotate(z)
                    }
                    z.parent?.color = TreeColor.BLACK
                    z.grand()?.color = TreeColor.RED
                    rightRotate(z.grand()!!)
                }
            } else {
                val uncle = z.uncleL()
                if (uncle?.color == TreeColor.RED) {
                    uncle.color = TreeColor.BLACK
                    z.parent?.color = TreeColor.BLACK
                    z.grand()?.color = TreeColor.RED
                    z = z.grand()!!
                } else {
                    if (z == z.bratL()) {
                        z = z.parent!!
                        rightRotate(z)
                    }
                    z.parent?.color = TreeColor.BLACK
                    z.grand()?.color = TreeColor.RED
                    leftRotate(z.grand()!!)
                }
            }
        }
        root.color = TreeColor.BLACK
    }

    fun delete(key: K, n: Int) {
        search(key)?.let {
            if (it.duplicates.size() > 1) {
                it.duplicates.remove(n)
                return
            }
            delete(it)
        }
    }

    private fun delete(z: Node) {
        var y = z
        val x: Node?
        var originalColor = y.color
        if (z.left.isNullLeaf()) {
            x = z.right
            cutNode(z, z.right!!)
        } else if (z.right.isNullLeaf()) {
            x = z.left
            cutNode(z, z.left!!)
        } else {
            y = minimum(z.right!!)
            originalColor = y.color;
            x = y.right
            if (y.parent == z) {
                x?.parent = y
            } else {
                cutNode(y, y.right!!)
                y.right = z.right
                y.right?.parent = y
            }
            cutNode(z, y)
            y.left = z.left
            y.left?.parent = y
            y.color = z.color
        }
        if (originalColor == TreeColor.BLACK) {
            fixDelete(x);
        }
    }

    private fun fixDelete(node: Node?) {
        var z = node
        while (z != root && z?.color == TreeColor.BLACK) {
            z = fixByBrat(z)(z == z.bratL())
        }
        z?.color = TreeColor.BLACK
    }

    private fun fixByBrat(z: Node): (Boolean) -> Node? = { isLeft ->
        var brat = if (isLeft) z.bratR() else z.bratL()
        val getBrat = { z: Node? -> if (isLeft) z?.bratR() else z?.bratL() }
        val rotate1 = { z: Node -> if (isLeft) leftRotate(z) else rightRotate(z) }
        val rotate2 = { z: Node -> if (isLeft) rightRotate(z) else leftRotate(z) }
        val getSon1 = { z: Node? -> if (isLeft) z?.right else z?.left }
        val getSon2 = { z: Node? -> if (isLeft) z?.left else z?.right }
        if (brat?.color == TreeColor.RED) {
            brat.color = TreeColor.BLACK
            z.parent?.color = TreeColor.RED
            rotate1(z.parent!!)
            brat = getBrat(z)
        }
        if (getSon1(brat)?.color == TreeColor.BLACK && getSon2(brat)?.color == TreeColor.BLACK) {
            brat?.color = TreeColor.RED
            z.parent
        } else {
            if (getSon1(brat)?.color == TreeColor.BLACK) {
                getSon2(brat)?.color = TreeColor.BLACK
                brat?.color = TreeColor.RED
                rotate2(brat!!)
                brat = getBrat(z)
            }
            brat?.color = z.parent?.color!!
            z.parent?.color = TreeColor.BLACK
            getSon1(brat)?.color = TreeColor.BLACK
            rotate1(z.parent!!)
            root
        }
    }

    fun search(key: K): Node? {
        val newNode = Node(key)
        var y: Node? = null
        var x = root
        while (!x.isNullLeaf() && y != newNode) {
            y = x
            x = if (newNode > x) x.right!!
            else x.left!!
        }
        if (key == y?.key) return y
        return null
    }

    fun initFromArray(arr: Array<Instance?>, keyAction: (Instance) -> K) {
        var time = System.nanoTime()
        arr.requireNoNulls().forEachIndexed { n, x ->
            add(keyAction(x), n)
        }
        LogsFile.writeln("Tree init completed in ${(System.nanoTime() - time) / 1000000} ms")
    }

    fun filterRecursive(arr: MutableList<ReportField>, func: (Node, Check, Boolean) -> Check) {
        fr(arr, root, Check.All, true, func)
    }

    enum class Check {
        LEFT, RIGHT, All, NONE
    }

    fun fr(
        arr: MutableList<ReportField>,
        now: Node,
        next: Check,
        needCheck: Boolean,
        func: (Node, Check, Boolean) -> Check,
    ) {
        val next = func(now, next, needCheck)
        if (now.right.isNullLeaf() && now.left.isNullLeaf() || next == Check.NONE) return
        if (!now.right.isNullLeaf()) fr(arr, now.right!!, next, next == Check.RIGHT || next == Check.All, func)
        if (!now.left.isNullLeaf()) fr(arr, now.left!!, next, next == Check.LEFT || next == Check.All, func)
    }

    fun print() = printTree(root, "")
    fun printWithLeafs() = printTreeWithLeafs(root, "")
    fun lrPrint() = leftRightOrder(root)

    override fun toString(): String {
        val str = StringBuilder()
        toStringTree(root, "", str)
        return str.toString()
    }

    private fun toStringTree(node: Node?, indent: String, sb: StringBuilder) {
        if (node.isNullLeaf()) return
        toStringTree(node?.right, "$indent          ", sb)
        sb.append("$indent $node\n")
        toStringTree(node?.left, "$indent           ", sb)
    }

    private fun leftRightOrder(root: Node?) {
        if (!root.isNullLeaf()) {
            leftRightOrder(root?.left)
            print("${root?.key}->")
            leftRightOrder(root?.right)
        }
    }

    private fun printTreeWithLeafs(node: Node?, indent: String) {
        if (node == null) return
        printTree(node.right, "$indent     ")
        println("$indent $node")
        printTree(node.left, "$indent     ")
    }

    private fun printTree(node: Node?, indent: String) {
        if (node.isNullLeaf()) return
        printTree(node?.right, "$indent     ")
        println("$indent $node")
        printTree(node?.left, "$indent     ")
    }

    val nodes get() = root
}