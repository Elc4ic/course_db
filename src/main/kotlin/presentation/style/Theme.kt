package presentation.style

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppTheme(content: @Composable () -> Unit) {

    val colorScheme = Colors(
        primary = Color(0xFF6537AD),
        onPrimary = Color.White,
        background = Color(0xFF313131),
        onBackground = Color.White,
        secondary = Color(0xFFB39DDB),
        onSecondary = Color.White,
        surface = Color(0xFF575757),
        onSurface = Color(0xFFBFBFBF),
        error = Color(0xFFDB7070),
        primaryVariant = Color(0xFF7E57BD),
        secondaryVariant = Color(0xFFFFFFFF),
        onError = Color(0xFFFFFFFF),
        isLight = false,
    )

    val typography = Typography(
        h2 = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        ),
        body1 = TextStyle(
            fontSize = 16.sp,
            color = colorScheme.onPrimary
        ),
        subtitle1 = TextStyle(
            fontSize = 12.sp,
            color = colorScheme.onPrimary
        )
    )

    val shapes = Shapes(
        small = RoundedCornerShape(6.dp),
        medium = RoundedCornerShape(10.dp),
        large = RoundedCornerShape(16.dp)
    )

    MaterialTheme(
        colors = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}