package presentation.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import structures.RedBlackTree.TreeColor
import kotlin.math.max
import kotlin.math.min

fun DrawScope.drawTreeNode(node: PositionedNode, textMeasurer: TextMeasurer) {
    node.left?.let {
        drawLine(
            color = Color.Black,
            start = Offset(node.x, node.y),
            end = Offset(it.x, it.y),
            strokeWidth = 2f
        )
        drawTreeNode(it, textMeasurer)
    }

    node.right?.let {
        drawLine(
            color = Color.Black,
            start = Offset(node.x, node.y),
            end = Offset(it.x, it.y),
            strokeWidth = 2f
        )
        drawTreeNode(it, textMeasurer)
    }

    val radius = 45f
    drawCircle(
        color = if (node.node.color == TreeColor.RED) Color.Red else Color.Black,
        radius = radius,
        center = Offset(node.x, node.y)
    )

    val textLayout = textMeasurer.measure(
        text = node.node.key.toString(),
        style = TextStyle(
            color = Color.White,
            fontSize = 9.sp,
            textAlign = TextAlign.Center
        )
    )

    drawText(
        textLayoutResult = textLayout,
        topLeft = Offset(
            x = node.x - textLayout.size.width / 2,
            y = node.y - textLayout.size.height / 2
        )
    )
}

fun DrawScope.drawVisibleNodes(
    node: PositionedNode,
    textMeasurer: TextMeasurer,
    radius: Float = 40f,
    textCache: MutableMap<String, TextLayoutResult> = mutableMapOf(),
) {

    node.left?.let { left ->
            drawLine(
                color = Color.Black,
                start = Offset(node.x, node.y),
                end = Offset(left.x, left.y),
                strokeWidth = 2f
            )
            drawVisibleNodes(left, textMeasurer, radius, textCache)
    }

    node.right?.let { right ->
            drawLine(
                color = Color.Black,
                start = Offset(node.x, node.y),
                end = Offset(right.x, right.y),
                strokeWidth = 2f
            )
            drawVisibleNodes(right, textMeasurer, radius, textCache)
    }

    drawCircle(
        color = if (node.node.color == TreeColor.RED) Color.Red else Color.Black,
        radius = radius,
        center = Offset(node.x, node.y)
    )

    val textLayout = textCache.getOrPut(node.node.key.toString()) {
        textMeasurer.measure(
            text = "${node.node.key}\n[${node.node.duplicates}]",
            style = TextStyle(
                color = Color.White,
                fontSize = 5.sp,
                textAlign = TextAlign.Center
            )
        )
    }

    drawText(
        textLayoutResult = textLayout,
        topLeft = Offset(
            x = node.x - textLayout.size.width / 2,
            y = node.y - textLayout.size.height / 2
        )
    )
}