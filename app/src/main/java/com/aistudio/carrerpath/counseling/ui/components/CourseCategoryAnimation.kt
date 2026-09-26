package com.aistudio.carrerpath.counseling.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.carrerpath.counseling.ui.theme.*
import kotlin.math.*

@Composable
fun CourseCategoryAnimationHeader(
    category: String,
    courseName: String,
    duration: String,
    fees: Double,
    isKn: Boolean = false,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val normCat = category.lowercase().trim()

    val gradientColors = when {
        normCat.contains("med") || normCat.contains("health") || normCat.contains("nurs") ->
            listOf(Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF00897B))
        normCat.contains("eng") || normCat.contains("tech") || normCat.contains("it") || normCat.contains("comp") ->
            listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF4338CA))
        normCat.contains("com") || normCat.contains("manag") || normCat.contains("fin") || normCat.contains("bba") || normCat.contains("bcom") ->
            listOf(Color(0xFF064E3B), Color(0xFF047857), Color(0xFF0D9488))
        normCat.contains("avia") || normCat.contains("aero") || normCat.contains("pilot") ->
            listOf(Color(0xFF0F172A), Color(0xFF0284C7), Color(0xFF38BDF8))
        normCat.contains("law") || normCat.contains("legal") || normCat.contains("judic") ->
            listOf(Color(0xFF3B0764), Color(0xFF6B21A8), Color(0xFF9333EA))
        normCat.contains("arch") || normCat.contains("desig") ->
            listOf(Color(0xFF7C2D12), Color(0xFFC2410C), Color(0xFFEA580C))
        else ->
            listOf(GeoGradientStart, GeoGradientMiddle, GeoGradientEnd)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Brush.linearGradient(gradientColors))
            .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Bar with Category Badge & Close Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        val catIcon = when {
                            normCat.contains("med") || normCat.contains("health") -> Icons.Default.MedicalServices
                            normCat.contains("nurs") -> Icons.Default.LocalHospital
                            normCat.contains("eng") || normCat.contains("tech") -> Icons.Default.Engineering
                            normCat.contains("it") || normCat.contains("comp") -> Icons.Default.Terminal
                            normCat.contains("com") || normCat.contains("manag") -> Icons.Default.AccountBalance
                            normCat.contains("avia") -> Icons.Default.FlightTakeoff
                            normCat.contains("law") -> Icons.Default.Gavel
                            normCat.contains("arch") || normCat.contains("desig") -> Icons.Default.Architecture
                            else -> Icons.Default.School
                        }
                        Icon(catIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = category.uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Animated Visual Canvas Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.Black.copy(alpha = 0.25f))
            ) {
                when {
                    normCat.contains("med") || normCat.contains("mbbs") || normCat.contains("bds") || normCat.contains("health") -> {
                        MedicalVisualAnimation()
                    }
                    normCat.contains("nurs") -> {
                        NursingVisualAnimation()
                    }
                    normCat.contains("it") || normCat.contains("comp") || normCat.contains("bca") || normCat.contains("mca") -> {
                        TechCodeVisualAnimation()
                    }
                    normCat.contains("eng") || normCat.contains("b.tech") || normCat.contains("mech") || normCat.contains("civil") -> {
                        EngineeringGearsVisualAnimation()
                    }
                    normCat.contains("avia") || normCat.contains("aero") || normCat.contains("pilot") || normCat.contains("cpl") -> {
                        AviationFlightVisualAnimation()
                    }
                    normCat.contains("com") || normCat.contains("manag") || normCat.contains("bba") || normCat.contains("b.com") || normCat.contains("mba") -> {
                        CommerceFinanceVisualAnimation()
                    }
                    normCat.contains("law") || normCat.contains("llb") || normCat.contains("judic") -> {
                        LawScalesVisualAnimation()
                    }
                    normCat.contains("arch") || normCat.contains("desig") -> {
                        ArchitectureVisualAnimation()
                    }
                    else -> {
                        AcademicDefaultVisualAnimation()
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = courseName,
                color = Color.White,
                fontSize = 21.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${if (isKn) "ಅವಧಿ" else "Duration"}: $duration",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Default.Payments, contentDescription = null, tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${if (isKn) "ಶುಲ್ಕ" else "Avg Fee"}: ₹${fees}L/yr",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// 1. MEDICAL ANIMATION: ECG Heartbeat Pulse + Stethoscope & Cross
@Composable
fun MedicalVisualAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "MedicalTransition")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ECGPhase"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Pulse"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerY = height * 0.5f

            // Background Grid Lines
            val gridColor = Color(0xFF64B5F6).copy(alpha = 0.15f)
            val step = 24.dp.toPx()
            var x = 0f
            while (x < width) {
                drawLine(gridColor, Offset(x, 0f), Offset(x, height), strokeWidth = 1f)
                x += step
            }
            var y = 0f
            while (y < height) {
                drawLine(gridColor, Offset(0f, y), Offset(width, y), strokeWidth = 1f)
                y += step
            }

            // ECG Path Drawing
            val path = Path()
            val pointCount = 120
            for (i in 0..pointCount) {
                val progress = (i.toFloat() / pointCount)
                val px = progress * width
                val waveX = (progress * 4f + phase * 2f) % 1f

                val dy = when {
                    waveX in 0.20f..0.24f -> -sin((waveX - 0.20f) / 0.04f * Math.PI.toFloat()) * 12f
                    waveX in 0.35f..0.38f -> sin((waveX - 0.35f) / 0.03f * Math.PI.toFloat()) * 14f
                    waveX in 0.38f..0.43f -> -sin((waveX - 0.38f) / 0.05f * Math.PI.toFloat()) * 48f // QRS Peak
                    waveX in 0.43f..0.47f -> sin((waveX - 0.43f) / 0.04f * Math.PI.toFloat()) * 22f
                    waveX in 0.58f..0.66f -> -sin((waveX - 0.58f) / 0.08f * Math.PI.toFloat()) * 18f // T Wave
                    else -> 0f
                }

                val py = centerY + dy
                if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
            }

            // Glow Stroke
            drawPath(
                path = path,
                color = Color(0xFF00E676).copy(alpha = 0.3f),
                style = Stroke(width = 7.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
            // Core ECG Stroke
            drawPath(
                path = path,
                color = Color(0xFF00E676),
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        // Floating Medical Badge
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFF00E676).copy(alpha = 0.2f),
                shape = CircleShape,
                modifier = Modifier.size((42 * pulseScale).dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

// 2. NURSING ANIMATION: Caring Hands + Life Cross Pulse
@Composable
fun NursingVisualAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "NursingTransition")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "NursingPulse"
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            // Radiating rings
            drawCircle(Color(0xFF00E5FF).copy(alpha = 0.15f * pulse), radius = 45.dp.toPx() * pulse)
            drawCircle(Color(0xFF00E5FF).copy(alpha = 0.25f), radius = 32.dp.toPx())
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = CircleShape,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.LocalHospital, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text("Clinical Healthcare & Patient Care", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Vital Monitoring • Therapeutic Support", color = Color(0xFFE0F7FA), fontSize = 11.sp)
            }
        }
    }
}

// 3. TECH & IT ANIMATION: Code Brackets < / > & Binary Stream
@Composable
fun TechCodeVisualAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "TechTransition")
    val cursorBlink by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(500, easing = LinearEasing), RepeatMode.Reverse),
        label = "CursorBlink"
    )
    val binaryOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Restart),
        label = "BinaryOffset"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridColor = Color(0xFF818CF8).copy(alpha = 0.12f)
            val step = 20.dp.toPx()
            var x = 0f
            while (x < size.width) {
                drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                x += step
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "const career = await AI.solve();",
                        color = Color(0xFF67E8F9),
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    if (cursorBlink > 0.5f) {
                        Box(modifier = Modifier.size(width = 6.dp, height = 14.dp).background(Color(0xFF38BDF8)))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "// 10100101 • Neural Systems • Cloud Architecture",
                    color = Color(0xFFC7D2FE).copy(alpha = 0.8f),
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    fontSize = 10.sp
                )
            }

            Surface(
                color = Color(0xFF6366F1).copy(alpha = 0.35f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF818CF8))
            ) {
                Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Code, contentDescription = null, tint = Color(0xFF67E8F9), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("< dev />", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                }
            }
        }
    }
}

// 4. ENGINEERING ANIMATION: Rotating Interlocking Gears
@Composable
fun EngineeringGearsVisualAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "GearTransition")
    val rotationA by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "GearA"
    )
    val rotationB by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "GearB"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridColor = Color(0xFF93C5FD).copy(alpha = 0.12f)
            val step = 20.dp.toPx()
            var x = 0f
            while (x < size.width) {
                drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                x += step
            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("CAD Blueprint & System Kinetics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Precision Automation • Innovation Engine", color = Color(0xFFBFDBFE), fontSize = 11.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color(0xFF60A5FA),
                    modifier = Modifier
                        .size(46.dp)
                        .rotate(rotationA)
                )
                Spacer(modifier = Modifier.width(-10.dp))
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color(0xFF93C5FD),
                    modifier = Modifier
                        .size(32.dp)
                        .rotate(rotationB)
                )
            }
        }
    }
}

// 5. AVIATION ANIMATION: Climbing Airplane & Trajectory
@Composable
fun AviationFlightVisualAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "AviationTransition")
    val flightProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart),
        label = "FlightProgress"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Horizon radar rings
            drawCircle(Color.White.copy(alpha = 0.08f), radius = 50.dp.toPx(), center = Offset(w * 0.85f, h * 0.5f))
            drawCircle(Color.White.copy(alpha = 0.05f), radius = 80.dp.toPx(), center = Offset(w * 0.85f, h * 0.5f))

            // Flight Path Curve
            val path = Path()
            path.moveTo(0f, h * 0.85f)
            path.quadraticBezierTo(w * 0.45f, h * 0.7f, w * 0.85f, h * 0.25f)

            drawPath(
                path = path,
                color = Color(0xFF38BDF8).copy(alpha = 0.5f),
                style = Stroke(
                    width = 2.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Flight Deck & Aerodynamics", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Altitude 35,000 FT • Commercial Pilot Training", color = Color(0xFFBAE6FD), fontSize = 11.sp)
            }

            Surface(
                color = Color(0xFF0284C7).copy(alpha = 0.4f),
                shape = CircleShape,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.FlightTakeoff,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(28.dp)
                            .rotate(-15f)
                    )
                }
            }
        }
    }
}

// 6. COMMERCE & FINANCE ANIMATION: Rising Graph Bars & Gold Coins
@Composable
fun CommerceFinanceVisualAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "CommerceTransition")
    val barHeightRatio by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "BarHeight"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Financial Analytics & Capital Growth", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Portfolio ROI +24.8% • Strategic Audit & Tax", color = Color(0xFFA7F3D0), fontSize = 11.sp)
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.height(48.dp)
            ) {
                val heights = listOf(0.4f, 0.6f, 0.85f, 1.0f)
                heights.forEachIndexed { idx, targetH ->
                    val curH = targetH * barHeightRatio
                    Box(
                        modifier = Modifier
                            .width(10.dp)
                            .fillMaxHeight(curH)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF34D399), Color(0xFF059669))
                                )
                            )
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(24.dp))
            }
        }
    }
}

// 7. LAW ANIMATION: Balancing Scales of Justice
@Composable
fun LawScalesVisualAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "LawTransition")
    val tiltAngle by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(1800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "Tilt"
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Constitutional Law & Jurisprudence", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Judicial Advocacy • Corporate Governance", color = Color(0xFFE9D5FF), fontSize = 11.sp)
            }

            Surface(
                color = Color(0xFF9333EA).copy(alpha = 0.35f),
                shape = CircleShape,
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(tiltAngle)
                    )
                }
            }
        }
    }
}

// 8. ARCHITECTURE ANIMATION: Isometric Drafting & Compass Arc
@Composable
fun ArchitectureVisualAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "ArchTransition")
    val arcProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "ArcProgress"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridColor = Color(0xFFFDBA74).copy(alpha = 0.15f)
            val step = 16.dp.toPx()
            var x = 0f
            while (x < size.width) {
                drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                x += step
            }
            // Compass Arc
            drawArc(
                color = Color(0xFFFB923C),
                startAngle = 180f,
                sweepAngle = 180f * arcProgress,
                useCenter = false,
                topLeft = Offset(size.width * 0.72f, size.height * 0.15f),
                size = Size(60.dp.toPx(), 60.dp.toPx()),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Spatial Design & Structural Drafting", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Parametric Modeling • Sustainable Urban Planning", color = Color(0xFFFFEDD5), fontSize = 11.sp)
            }

            Surface(
                color = Color(0xFFC2410C).copy(alpha = 0.35f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(10.dp)) {
                    Icon(Icons.Default.Architecture, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

// 9. ACADEMIC DEFAULT ANIMATION
@Composable
fun AcademicDefaultVisualAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "AcademicTransition")
    val sparkle by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "Sparkle"
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Higher Education & Academic Excellence", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Accredited Degrees • Global Employability", color = Color(0xFFE2E8F0), fontSize = 11.sp)
            }

            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = CircleShape,
                modifier = Modifier.size((44 * sparkle).dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}
