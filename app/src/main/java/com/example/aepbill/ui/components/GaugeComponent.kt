package com.example.aepbill.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aepbill.ui.theme.PrimaryGradientColors
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GaugeComponent(
    value: Float,
    maxValue: Float = 100f,
    unit: String = "A",
    label: String = "Current",
    modifier: Modifier = Modifier,
    thickness: Dp = 20.dp
) {
    val animatedValue by animateFloatAsState(
        targetValue = value,
        animationSpec = tween(durationMillis = 1000),
        label = "GaugeAnimation"
    )

    Box(modifier = modifier.aspectRatio(1f)) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2
            val startAngle = 135f
            val sweepAngle = 270f
            val percent = (animatedValue / maxValue).coerceIn(0f, 1f)
            
            // Draw Background Arc
            drawArc(
                color = Color.Gray.copy(alpha = 0.2f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = thickness.toPx(), cap = StrokeCap.Round)
            )

            // Draw Progress Arc
            drawArc(
                brush = Brush.linearGradient(PrimaryGradientColors),
                startAngle = startAngle,
                sweepAngle = sweepAngle * percent,
                useCenter = false,
                style = Stroke(width = thickness.toPx(), cap = StrokeCap.Round)
            )
            
            // Glow Effect
            drawIntoCanvas { canvas ->
                val paint = Paint().asFrameworkPaint().apply {
                    color = PrimaryGradientColors.last().toArgb()
                    maskFilter = android.graphics.BlurMaskFilter(30f, android.graphics.BlurMaskFilter.Blur.NORMAL)
                    alpha = 100
                }
                
                // Calculate end point of the arc for the glow
                val currentAngle = Math.toRadians((startAngle + sweepAngle * percent).toDouble())
                val glowX = center.x + radius * cos(currentAngle).toFloat()
                val glowY = center.y + radius * sin(currentAngle).toFloat()
                
                canvas.nativeCanvas.drawCircle(glowX, glowY, 40f, paint)
            }
        }
        
        // Text in Center
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = String.format("%.3f", animatedValue),
                style = MaterialTheme.typography.displayMedium,
                color = androidx.compose.ui.graphics.Color.White
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.headlineSmall,
                color = androidx.compose.ui.graphics.Color.Gray
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = com.example.aepbill.ui.theme.PrimaryBlue
            )
        }
    }
}
