package presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import structures.RedBlackTree
import structures.RedBlackTree.Node
import structures.RedBlackTree.TreeColor

@Composable
fun TreeRow(node: RedBlackTree<*>.Node?, onSearch: (RedBlackTree<*>.Node?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Row(modifier = Modifier
        .background(
            if (node?.color == TreeColor.RED) {
                Color.Red.copy(alpha = 0.2f)
            } else {
                Color.Black.copy(alpha = 0.2f)
            }
        )
        .clickable {
            onSearch(node)
        }
        .padding(4.dp)

    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(imageVector = if(expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight, contentDescription = "Expand")
        }
        SelectionContainer{
            Text(node?.key.toString() + " " + node?.duplicates.toString())
        }
    }
    if (expanded) {
        Column(modifier = Modifier.fillMaxWidth().padding(start = 20.dp)) {
            node?.left?.let { TreeRow(it, onSearch) }
            node?.right?.let { TreeRow(it, onSearch) }
        }
    }

}