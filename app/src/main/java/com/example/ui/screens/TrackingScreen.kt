package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Moped
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShareLocation
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Order
import com.example.ui.CuisineViewModel

@Composable
fun TrackingScreen(
    viewModel: CuisineViewModel,
    modifier: Modifier = Modifier
) {
    val trackedOrderId by viewModel.trackedOrderId.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    
    var showHistoryDialog by remember { mutableStateOf(false) }

    val activeOrder = remember(allOrders, trackedOrderId) {
        allOrders.find { it.id == trackedOrderId } ?: allOrders.find { it.status != "Delivered" } ?: allOrders.firstOrNull()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Toolbar header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ShareLocation,
                contentDescription = "Tracking Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                "Live Delivery Tracker",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Order history selector shortcut
            IconButton(
                onClick = { showHistoryDialog = true },
                modifier = Modifier.testTag("order_history_shortcut")
            ) {
                Icon(Icons.Default.History, contentDescription = "Order history")
            }
        }

        if (activeOrder == null) {
            // EMPTY STATE GUIDE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "No active trace",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No orders in tracking queue",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                    Text(
                        "Once you place an order for delicious Singju salad or premium traditional Manipuri thali, you can track your delivery here in real-time.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { viewModel.selectTab("menu") },
                        modifier = Modifier.testTag("track_go_to_menu")
                    ) {
                        Text("View categorized digital menu")
                    }
                }
            }
        } else {
            // ACTIVE TRACKING DASHBOARD
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 16.dp)
            ) {
                // Tracking Status Overhead Banner
                item {
                    StatusOverheadCard(order = activeOrder)
                }

                // Interactive Animated Map Canvas
                item {
                    InteractiveMapCanvas(progress = activeOrder.progressPercent)
                }

                // Stepper Milestones Card
                item {
                    DeliveryStepperCard(order = activeOrder)
                }

                // Rider details card
                item {
                    RiderDetailsCard(order = activeOrder)
                }
            }
        }
    }

    // ORDER HISTORY DIALOG SELECTOR
    if (showHistoryDialog) {
        Dialog(onDismissRequest = { showHistoryDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Select Order to Track",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (allOrders.isEmpty()) {
                        Text(
                            "You haven't placed any traditional food orders yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.height(260.dp)
                        ) {
                            items(allOrders) { ord ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (ord.id == trackedOrderId) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.setTrackedOrder(ord.id)
                                            showHistoryDialog = false
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = when (ord.status) {
                                                "Delivered" -> Icons.Default.CheckCircle
                                                "Out for Delivery" -> Icons.Default.Moped
                                                else -> Icons.Default.Restaurant
                                            },
                                            contentDescription = ord.status,
                                            tint = if (ord.id == trackedOrderId) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                "Order #${ord.id} - ₹${ord.totalAmount.toInt()}",
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                ord.itemsSummary,
                                                style = MaterialTheme.typography.bodySmall,
                                                maxLines = 1,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                            )
                                        }
                                        Surface(
                                            color = when(ord.status) {
                                                "Delivered" -> MaterialTheme.colorScheme.secondaryContainer
                                                "Out for Delivery" -> MaterialTheme.colorScheme.primaryContainer
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            },
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                ord.status,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showHistoryDialog = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        }
    }
}

@Composable
fun StatusOverheadCard(order: Order) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("status_overhead_card")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Order #${order.id}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = when (order.status) {
                        "Pending" -> "Waiting for Confirmation..."
                        "Preparing" -> "Traditional Kitchen is Preparing..."
                        "Out for Delivery" -> "Rider is heading your way!"
                        "Delivered" -> "Delivered safely!"
                        else -> "Processing"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                Text(
                    text = "Delivering to: ${order.customerAddress}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f)
                )
                Text(
                    text = "Payment Mode: ${order.paymentMethod}",
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
                if (order.discountAmount > 0) {
                    Text(
                        text = "Applied Discount: -₹${order.discountAmount.toInt()}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    text = "${order.etaMinutes}",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "MINS ETA",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun InteractiveMapCanvas(progress: Float) {
    // Pulsing animations loop
    val pulseTransition = rememberInfiniteTransition(label = "Radar beacon")
    val pulseRadius by pulseTransition.animateFloat(
        initialValue = 10f,
        targetValue = 35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "Pulse circle radius"
    )
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "Pulse transparency"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Elegant background Grid Canvas Drawing
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Draw decorative grid patterns matching cozy traditional land maps
                val gridGap = 40f
                var x = 0f
                while (x < w) {
                    drawLine(
                        color = Color(0x0F000000),
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 2f
                    )
                    x += gridGap
                }
                var y = 0f
                while (y < h) {
                    drawLine(
                        color = Color(0x0F000000),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 2f
                    )
                    y += gridGap
                }

                // Decorative Lake/Park area (Imphal Kangla Fort style circle)
                drawCircle(
                    color = Color(0x184CAF50),
                    radius = 90f,
                    center = Offset(w * 0.4f, h * 0.4f)
                )

                // Define winding road path lines from Restaurant hub to Customer home
                val shopPoint = Offset(w * 0.2f, h * 0.75f)
                val destPoint = Offset(w * 0.8f, h * 0.25f)
                
                // control point 1, control point 2 for beautiful curve (Simulating Manipur roads)
                val cP1 = Offset(w * 0.35f, h * 0.15f)
                val cP2 = Offset(w * 0.65f, h * 0.85f)

                val windingRoad = Path().apply {
                    moveTo(shopPoint.x, shopPoint.y)
                    cubicTo(cP1.x, cP1.y, cP2.x, cP2.y, destPoint.x, destPoint.y)
                }

                // Draw the wide gray main road
                drawPath(
                    path = windingRoad,
                    color = Color(0x4090A4AE),
                    style = Stroke(width = 18f, pathEffect = PathEffect.cornerPathEffect(15f))
                )

                // Draw dashed active route (animated progression)
                drawPath(
                    path = windingRoad,
                    color = Color(0xFFD84315).copy(alpha = 0.6f),
                    style = Stroke(
                        width = 8f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                    )
                )

                // Calculate current driver position on the cubic Bezier spline curve
                // B(t) = (1-t)³*P0 + 3(1-t)²*t*P1 + 3(1-t)*t²*P2 + t³*P3
                val t = progress.coerceIn(0f, 1f)
                val mt = 1f - t
                val curX = (mt * mt * mt * shopPoint.x) + 
                          (3 * mt * mt * t * cP1.x) + 
                          (3 * mt * t * t * cP2.x) + 
                          (t * t * t * destPoint.x)
                val curY = (mt * mt * mt * shopPoint.y) + 
                          (3 * mt * mt * t * cP1.y) + 
                          (3 * mt * t * t * cP2.y) + 
                          (t * t * t * destPoint.y)

                val driverPos = Offset(curX, curY)

                // Draw radar beacons ONLY if Out for Delivery (Rider active)
                if (progress in 0.20f..0.98f) {
                    drawCircle(
                        color = Color(0xFFD84315).copy(alpha = pulseAlpha),
                        radius = pulseRadius,
                        center = driverPos
                    )
                }

                // DRAW RESTAURANT PIN (Keishampat)
                drawCircle(color = Color(0xFF2E7D32), radius = 14f, center = shopPoint)
                drawCircle(color = Color.White, radius = 6f, center = shopPoint)

                // DRAW CUSTOMER DESTINATION PIN
                drawCircle(color = Color(0xFFC62828), radius = 16f, center = destPoint)
                val iconTriangle = Path().apply {
                    moveTo(destPoint.x, destPoint.y)
                    lineTo(destPoint.x - 10f, destPoint.y - 18f)
                    lineTo(destPoint.x + 10f, destPoint.y - 18f)
                    close()
                }
                drawPath(iconTriangle, color = Color(0xFFC62828))
                drawCircle(color = Color.White, radius = 7f, center = destPoint)

                // DRAW THE DRIVER DOT (Rider icon/moped dot)
                drawCircle(
                    color = Color(0xFFD84315),
                    radius = 11f,
                    center = driverPos
                )
                drawCircle(
                    color = Color.White,
                    radius = 4f,
                    center = driverPos
                )
            }

            // Labels overlays
            Text(
                "Keishampat\nKitchen",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 32.dp, bottom = 26.dp)
            )

            Text(
                "Home Address",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC62828),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 40.dp, top = 22.dp)
            )

            // Current Rider HUD Indicator Overlay
            Surface(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(6.dp)
                ) {
                    Icon(
                        Icons.Default.ShareLocation,
                        contentDescription = "location icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "GPS Tracking Active",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DeliveryStepperCard(order: Order) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Status Milestones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val currentStep = when (order.status) {
                "Pending" -> 1
                "Preparing" -> 2
                "Out for Delivery" -> 3
                "Delivered" -> 4
                else -> 1
            }

            StepItem(
                stepNum = 1,
                title = "Order Confirmed",
                subtitle = "Sellers accepted and registered your delivery",
                isActive = currentStep >= 1,
                isCompleted = currentStep > 1
            )
            StepDivider(isCompleted = currentStep > 1)

            StepItem(
                stepNum = 2,
                title = "In The Kitchen",
                subtitle = "Traditional chefs are hand-crafting spices & veggies",
                isActive = currentStep >= 2,
                isCompleted = currentStep > 2
            )
            StepDivider(isCompleted = currentStep > 2)

            StepItem(
                stepNum = 3,
                title = "Out for Delivery",
                subtitle = "Rider carrying traditional packs in insulation bags",
                isActive = currentStep >= 3,
                isCompleted = currentStep > 3
            )
            StepDivider(isCompleted = currentStep > 3)

            StepItem(
                stepNum = 4,
                title = "Delivered & Happy",
                subtitle = "Arrived at your doorstep! Enjoy raw hand-made taste",
                isActive = currentStep >= 4,
                isCompleted = currentStep >= 4
            )
        }
    }
}

@Composable
fun StepItem(
    stepNum: Int,
    title: String,
    subtitle: String,
    isActive: Boolean,
    isCompleted: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Bubble Indicator
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    color = if (isCompleted) MaterialTheme.colorScheme.secondary
                    else if (isActive) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "completed status",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = "$stepNum",
                    color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = title,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (isActive) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
            )
        }
    }
}

@Composable
fun StepDivider(isCompleted: Boolean) {
    Box(
        modifier = Modifier
            .padding(start = 11.dp)
            .width(2.dp)
            .height(20.dp)
            .background(
                if (isCompleted) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.surfaceVariant
            )
    )
}

@Composable
fun RiderDetailsCard(order: Order) {
    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Driver Avatar Icon Shape
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.DirectionsBike,
                        contentDescription = "Rider Profile",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.driverName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Insulated Delivery Partner",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                // Call Button
                IconButton(
                    onClick = { /* Simulated Call action */ },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer, shape = CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call rider",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Moped,
                    contentDescription = "Transport Mode",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Delivered on rapid eco-friendly e-bike with insulated carrier box.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
