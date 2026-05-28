package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun FoodIcon(
    imageType: String,
    modifier: Modifier = Modifier
) {
    // Beautiful gradients based on food identity
    val backgroundBrush = when (imageType.lowercase()) {
        "singju" -> Brush.linearGradient(
            colors = listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9)) // Light mint greens
        )
        "eromba" -> Brush.linearGradient(
            colors = listOf(Color(0xFFEFEBE9), Color(0xFFD7CCC8)) // Earthy bamboo browns
        )
        "thali" -> Brush.linearGradient(
            colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2)) // Traditional golden saffron
        )
        "kangshoi" -> Brush.linearGradient(
            colors = listOf(Color(0xFFF1F8E9), Color(0xFFDCEDC8)) // Pure healthy greens
        )
        "kheer" -> Brush.linearGradient(
            colors = listOf(Color(0xFFF3E5F5), Color(0xFFE1BEE7)) // Deep imperial royal purples
        )
        else -> Brush.linearGradient(
            colors = listOf(Color(0xFFECEFF1), Color(0xFFCFD8DC)) // Neat grey slates
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundBrush)
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val centerX = w / 2
            val centerY = h / 2

            when (imageType.lowercase()) {
                "singju" -> {
                    // Draw a beautiful dark wooden bowl with leafy herbs and chili specks
                    // Wooden Bowl shadow & body
                    drawArc(
                        color = Color(0xFF5D4037),
                        startAngle = 0f,
                        sweepAngle = 180f,
                        useCenter = true,
                        size = Size(w * 0.75f, h * 0.75f),
                        topLeft = Offset(w * 0.125f, h * 0.18f)
                    )
                    // Herb fillings (Green salad shapes)
                    drawCircle(Color(0xFF4CAF50), radius = w * 0.22f, center = Offset(centerX, centerY * 0.8f))
                    drawCircle(Color(0xFF2E7D32), radius = w * 0.15f, center = Offset(centerX - w * 0.12f, centerY * 0.9f))
                    drawCircle(Color(0xFF81C784), radius = w * 0.12f, center = Offset(centerX + w * 0.14f, centerY * 0.9f))
                    // Red chili flakes
                    drawCircle(Color(0xFFD84315), radius = w * 0.03f, center = Offset(centerX - 8f, centerY - 10f))
                    drawCircle(Color(0xFFD84315), radius = w * 0.02f, center = Offset(centerX + 15f, centerY - 18f))
                    drawCircle(Color(0xFFFF9800), radius = w * 0.025f, center = Offset(centerX, centerY - 40f))
                    // Fork/Spoon indicator line
                    drawLine(
                        color = Color(0xFFE0E0E0),
                        start = Offset(centerX + w * 0.2f, centerY - h * 0.3f),
                        end = Offset(centerX + w * 0.05f, centerY),
                        strokeWidth = 6f
                    )
                }
                "eromba" -> {
                    // Traditional Manipur earthen pot (Uyan) / mash bowl
                    // Earthen pot body - rich terracotta
                    drawCircle(
                        color = Color(0xFFBF360C),
                        radius = w * 0.35f,
                        center = Offset(centerX, centerY * 1.05f)
                    )
                    // Rim
                    drawOval(
                        color = Color(0xFFD84315),
                        size = Size(w * 0.76f, h * 0.16f),
                        topLeft = Offset(w * 0.12f, h * 0.6f)
                    )
                    // Liquid stew/bamboo fillings
                    drawOval(
                        color = Color(0xFFFFB74D),
                        size = Size(w * 0.64f, h * 0.12f),
                        topLeft = Offset(w * 0.18f, h * 0.62f)
                    )
                    // Coriander leaf floating (herb green cluster)
                    drawCircle(Color(0xFF4CAF50), radius = w * 0.08f, center = Offset(centerX - 10f, centerY * 0.62f))
                    // Red fiery smoked chili resting on it
                    val chiliPath = Path().apply {
                        moveTo(centerX + 20f, centerY - 10f)
                        quadraticTo(centerX + 10f, centerY - 45f, centerX + 15f, centerY - 55f)
                        lineTo(centerX + 18f, centerY - 55f)
                        quadraticTo(centerX + 15f, centerY - 45f, centerX + 28f, centerY - 10f)
                        close()
                    }
                    drawPath(chiliPath, color = Color(0xFFD84315))
                }
                "thali" -> {
                    // Golden/silver traditional brass thali platter with small katoris
                    // Big round plate background (Silver/brass shine)
                    drawCircle(
                        color = Color(0xFFB0BEC5),
                        radius = w * 0.45f,
                        center = Offset(centerX, centerY)
                    )
                    drawCircle(
                        color = Color(0xFFECEFF1),
                        radius = w * 0.40f,
                        center = Offset(centerX, centerY)
                    )
                    // Center Mound of white rice
                    drawCircle(Color(0xFFFFFAF0), radius = w * 0.16f, center = Offset(centerX, centerY))
                    // Small katoris (bowls) resting in the plate
                    // Fish Curry bowl (reddish brown)
                    drawCircle(Color(0xFFD84315), radius = w * 0.09f, center = Offset(centerX - w * 0.22f, centerY - h * 0.12f))
                    // Veg curry bowl (green)
                    drawCircle(Color(0xFF2E7D32), radius = w * 0.09f, center = Offset(centerX + w * 0.22f, centerY - h * 0.12f))
                    // Yellow lentils dhal
                    drawCircle(Color(0xFFFFD54F), radius = w * 0.08f, center = Offset(centerX + w * 0.24f, centerY + h * 0.14f))
                    // Singju side
                    drawCircle(Color(0xFF81C784), radius = w * 0.08f, center = Offset(centerX - w * 0.24f, centerY + h * 0.14f))
                }
                "kangshoi" -> {
                    // Piping hot stew bowl with beautiful steam lines
                    // Beautiful porcelain bowl layout
                    drawArc(
                        color = Color(0xFFEEEEEE),
                        startAngle = 0f,
                        sweepAngle = 180f,
                        useCenter = true,
                        size = Size(w * 0.75f, h * 0.70f),
                        topLeft = Offset(w * 0.125f, h * 0.22f)
                    )
                    // Broth level
                    drawOval(
                        color = Color(0xFFAED581),
                        size = Size(w * 0.74f, h * 0.15f),
                        topLeft = Offset(w * 0.13f, h * 0.22f)
                    )
                    // Floaters - leafy greens and beans
                    drawCircle(Color(0xFF2E7D32), radius = w * 0.06f, center = Offset(centerX - 15f, centerY))
                    drawCircle(Color(0xFF4CAF50), radius = w * 0.05f, center = Offset(centerX + 25f, centerY + 10f))
                    drawCircle(Color(0xFF81C784), radius = w * 0.04f, center = Offset(centerX, centerY + 18f))
                    // Comfort Steam waves (vector paths)
                    val path1 = Path().apply {
                        moveTo(centerX - 25f, centerY - 15f)
                        quadraticTo(centerX - 35f, centerY - 45f, centerX - 20f, centerY - 65f)
                    }
                    val path2 = Path().apply {
                        moveTo(centerX + 15f, centerY - 15f)
                        quadraticTo(centerX + 5f, centerY - 45f, centerX + 20f, centerY - 65f)
                    }
                    drawPath(path1, color = Color.White, style = Stroke(width = 5f))
                    drawPath(path2, color = Color.White, style = Stroke(width = 5f))
                }
                "kheer" -> {
                    // Deep royal black rice kheer/pudding (GI Tag organic luxury purple)
                    // Elegant Golden/Amber small dessert bowl
                    drawCircle(
                        color = Color(0xFFFFB300),
                        radius = w * 0.34f,
                        center = Offset(centerX, centerY + h * 0.12f)
                    )
                    // Liquid filling - rich deep magenta purple
                    drawCircle(
                        color = Color(0xFF4A148C),
                        radius = w * 0.29f,
                        center = Offset(centerX, centerY + h * 0.10f)
                    )
                    // Swirl topping details
                    drawCircle(
                        color = Color(0xFFEA80FC),
                        radius = w * 0.18f,
                        center = Offset(centerX, centerY + h * 0.10f)
                    )
                    // Golden almonds scattered
                    drawCircle(Color(0xFFFFEE58), radius = w * 0.025f, center = Offset(centerX - 12f, centerY + 8f))
                    drawCircle(Color(0xFFFFEE58), radius = w * 0.02f, center = Offset(centerX + 18f, centerY + 12f))
                    drawCircle(Color(0xFFFFE082), radius = w * 0.025f, center = Offset(centerX, centerY + 28f))
                }
                else -> {
                    // Generic visual cover: standard cloche table server
                    val clochePath = Path().apply {
                        moveTo(centerX - w * 0.36f, centerY + h * 0.08f)
                        lineTo(centerX + w * 0.36f, centerY + h * 0.08f)
                        drawArc(
                            color = Color(0xFF78909C),
                            startAngle = 180f,
                            sweepAngle = 180f,
                            useCenter = true,
                            size = Size(w * 0.72f, h * 0.72f),
                            topLeft = Offset(w * 0.14f, h * 0.18f)
                        )
                    }
                    drawPath(clochePath, color = Color(0xFF90A4AE))
                    // Ring handle top
                    drawCircle(
                        color = Color(0xFF546E7A),
                        radius = w * 0.08f,
                        center = Offset(centerX, centerY - h * 0.18f),
                        style = Stroke(width = 6f)
                    )
                    // Platter line base
                    drawLine(
                        color = Color(0xFF546E7A),
                        start = Offset(centerX - w * 0.42f, centerY + h * 0.12f),
                        end = Offset(centerX + w * 0.42f, centerY + h * 0.12f),
                        strokeWidth = 8f
                    )
                }
            }
        }
    }
}
