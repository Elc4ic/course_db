package presentation.components

import androidx.compose.ui.geometry.Offset
import structures.RedBlackTree.Node
import kotlin.math.pow
import kotlin.math.sqrt

data class PositionedNode(
    val node: Node,
    val x: Float,
    val y: Float,
    val left: PositionedNode? = null,
    val right: PositionedNode? = null,
) {
    fun clicked(offset: Offset): Node? {
        val distance = sqrt((x - offset.x).pow(2) + (y - offset.y).pow(2))
        if (distance <= 45f) return node

        return left?.clicked(offset) ?: right?.clicked(offset)
    }
}

fun layoutTree(
    node: Node?,
    depth: Int = 1,
    xOffset: Float = 0f,
    xStep: Float = 80f,
    yStep: Float = 120f,
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