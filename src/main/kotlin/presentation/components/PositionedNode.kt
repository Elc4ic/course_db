package presentation.components

import androidx.compose.ui.geometry.Offset
import structures.RedBlackTree
import kotlin.math.pow
import kotlin.math.sqrt

data class PositionedNode(
    val node: RedBlackTree<*>.Node,
    val x: Float,
    val y: Float,
    val left: PositionedNode? = null,
    val right: PositionedNode? = null,
) {
    fun clicked(offset: Offset): RedBlackTree<*>.Node? {
        val distance = sqrt((x - offset.x).pow(2) + (y - offset.y).pow(2))
        if (distance <= 45f) return node

        return left?.clicked(offset) ?: right?.clicked(offset)
    }
}

fun layoutTree(
    node: RedBlackTree<*>.Node?,
    depth: Int = 1,
    xOffset: Float = 0f,
    xStep: Float = 50f,
    yStep: Float = 80f,
): Pair<PositionedNode?, Float> {
    if (node == null) return null to xOffset

    val (leftNode, leftX) = layoutTree(node.left, depth + 1, xOffset, xStep, yStep)
    var centerX = if (leftNode == null) xOffset else leftX + xStep
    val (rightNode, rightX) = layoutTree(node.right, depth + 1, centerX + xStep, xStep, yStep)

    val positioned = PositionedNode(
        node,
        x = centerX,
        y = depth * yStep,
        left = leftNode,
        right = rightNode
    )

    return return positioned to (rightNode?.let { rightX } ?: centerX)
}