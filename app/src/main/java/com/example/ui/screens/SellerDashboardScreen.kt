package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Moped
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MenuItem
import com.example.data.model.Order
import com.example.ui.CuisineViewModel
import com.example.ui.components.FoodIcon

@Composable
fun SellerDashboardScreen(
    viewModel: CuisineViewModel,
    modifier: Modifier = Modifier
) {
    val menuList by viewModel.menuItems.collectAsStateWithLifecycle()
    val orderList by viewModel.allOrders.collectAsStateWithLifecycle()

    var showAddDishDialog by remember { mutableStateOf(false) }

    // Derive Metrics
    val totalRevenue = remember(orderList) {
        orderList.filter { it.status == "Delivered" }.sumOf { it.totalAmount }
    }
    val activeOrdersCount = remember(orderList) {
        orderList.count { it.status != "Delivered" }
    }
    val catalogCount = menuList.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Portal Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Storefront,
                        contentDescription = "Seller portal logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "MAHAO SINGJU",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            "Seller Operations Dashboard",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
                
                IconButton(
                    onClick = { viewModel.resetDemo() },
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.errorContainer, shape = RoundedCornerShape(8.dp))
                        .testTag("reset_demo_btn")
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reset Demo Data",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        // METRICS GRIDS CONTROLS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Revenue Card
                MetricCard(
                    title = "Revenue",
                    value = "₹${totalRevenue.toInt()}",
                    icon = Icons.Default.CurrencyRupee,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                // Active orders Card
                MetricCard(
                    title = "Active Orders",
                    value = "$activeOrdersCount",
                    icon = Icons.Default.DeliveryDining,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )

                // Catalog Card
                MetricCard(
                    title = "Menu Dishes",
                    value = "$catalogCount",
                    icon = Icons.Default.RestaurantMenu,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // INCOMING ORDERS TRACKING CONTROLS
        item {
            Text(
                "Live Direct Orders (${orderList.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (orderList.isEmpty()) {
            item {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.ListAlt,
                            contentDescription = "No orders",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "No orders received yet",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f)
                        )
                    }
                }
            }
        } else {
            items(orderList) { order ->
                SellerOrderControlCard(
                    order = order,
                    onUpdateStatus = { ord, status -> viewModel.updateOrderStatus(ord, status) },
                    onTrackClicked = { viewModel.setTrackedOrder(order.id) }
                )
            }
        }

        // MANAGE CUISINE CATALOG SECTION
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Manage Digital Menu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
                )

                Button(
                    onClick = { showAddDishDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("add_dish_catalog_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Dish", fontWeight = FontWeight.Bold)
                }
            }
        }

        items(menuList) { item ->
            SellerMenuManageRow(
                item = item,
                onToggleAvailability = { viewModel.toggleMenuItemAvailability(item) },
                onDelete = { viewModel.deleteMenuItem(item) }
            )
        }
    }

    // ADD NEW DISH DIALOG
    if (showAddDishDialog) {
        var dishName by remember { mutableStateOf("") }
        var dishPrice by remember { mutableStateOf("") }
        var dishCategory by remember { mutableStateOf("Main Course") }
        var dishDescription by remember { mutableStateOf("") }
        var dishImageType by remember { mutableStateOf("singju") } // singju, thali, eromba, kheer, kangshoi

        val styleOptions = listOf("singju", "thali", "eromba", "kheer", "kangshoi", "generic")
        val categoryOptions = listOf("Singju & Salads", "Main Course", "Side Dishes", "Soups & Stews", "Desserts")

        Dialog(onDismissRequest = { showAddDishDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            "Add Authentic Manipur Dish",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = dishName,
                            onValueChange = { dishName = it },
                            label = { Text("Dish Name") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_dish_name_input")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = dishPrice,
                            onValueChange = { dishPrice = it },
                            label = { Text("Price (INR)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_dish_price_input")
                        )
                    }

                    item {
                        Text("Category Picker", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LazyColumn(modifier = Modifier.height(130.dp).fillMaxWidth()) {
                                items(categoryOptions) { cat ->
                                    val isSelected = cat == dishCategory
                                    Surface(
                                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { dishCategory = cat }
                                            .padding(vertical = 4.dp, horizontal = 6.dp)
                                    ) {
                                        Text(
                                            cat,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.padding(8.dp),
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = dishDescription,
                            onValueChange = { dishDescription = it },
                            label = { Text("Ingredients & Preparation Summary") },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Text("Custom Style / Graphic Shape", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            styleOptions.forEach { opt ->
                                val isSelected = opt == dishImageType
                                Surface(
                                    color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { dishImageType = opt }
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    ) {
                                        Text(
                                            opt.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { showAddDishDialog = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }
                            Button(
                                onClick = {
                                    val finalPrice = dishPrice.toDoubleOrNull() ?: 100.0
                                    if (dishName.isNotBlank()) {
                                        viewModel.addMenuItem(
                                            dishName,
                                            dishDescription.ifBlank { "Delicious authentic freshly-prepared Manipuri dish." },
                                            finalPrice,
                                            dishCategory,
                                            dishImageType
                                        )
                                        showAddDishDialog = false
                                    }
                                },
                                modifier = Modifier
                                    .weight(1.5f)
                                    .testTag("submit_dish_button")
                            ) {
                                Text("Add to Menu", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = tint.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = tint,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Text(
                title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SellerOrderControlCard(
    order: Order,
    onUpdateStatus: (Order, String) -> Unit,
    onTrackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Order #${order.id} — ₹${order.totalAmount.toInt()}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Name: ${order.customerName} (${order.customerPhone})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.70f)
                    )
                }

                Surface(
                    color = when (order.status) {
                        "Pending" -> Color(0xFFFFECEB)
                        "Preparing" -> Color(0xFFFFF3E0)
                        "Out for Delivery" -> Color(0xFFE8F5E9)
                        "Delivered" -> Color(0xFFECEFF1)
                        else -> Color(0xFFEEEEEE)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = order.status,
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.labelSmall,
                        color = when (order.status) {
                            "Pending" -> Color(0xFFC62828)
                            "Preparing" -> Color(0xFFE65100)
                            "Out for Delivery" -> Color(0xFF2E7D32)
                            "Delivered" -> Color(0xFF546E7A)
                            else -> Color(0xFF333333)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = order.itemsSummary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Moped,
                    contentDescription = "location home",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = order.customerAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Action Status row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left action
                if (order.status != "Delivered") {
                    Button(
                        onClick = {
                            val nextStatus = when (order.status) {
                                "Pending" -> "Preparing"
                                "Preparing" -> "Out for Delivery"
                                "Out for Delivery" -> "Delivered"
                                else -> "Delivered"
                            }
                            onUpdateStatus(order, nextStatus)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when(order.status) {
                                "Pending" -> MaterialTheme.colorScheme.primaryContainer
                                "Preparing" -> MaterialTheme.colorScheme.secondaryContainer
                                "Out for Delivery" -> Color(0xFF2E7D32)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            contentColor = when(order.status) {
                                "Out for Delivery" -> Color.White
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        ),
                        modifier = Modifier.testTag("advance_status_btn_${order.id}")
                    ) {
                        Text(
                            text = when (order.status) {
                                "Pending" -> "Accept & Cook"
                                "Preparing" -> "Ship (E-Bike)"
                                "Out for Delivery" -> "Mark Delivered"
                                else -> "Complete"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        "Order Completed",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }

                // Right action - track
                OutlinedButton(
                    onClick = onTrackClicked,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("track_order_btn_${order.id}")
                ) {
                    Text("Track in Real-time")
                }
            }
        }
    }
}

@Composable
fun SellerMenuManageRow(
    item: MenuItem,
    onToggleAvailability: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FoodIcon(imageType = item.imageType, modifier = Modifier.size(54.dp))
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(
                    "₹${item.price.toInt()} — ${item.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Available toggle switch
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    if (item.isAvailable) "Available" else "Sold out",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (item.isAvailable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = item.isAvailable,
                    onCheckedChange = { onToggleAvailability() },
                    modifier = Modifier.testTag("toggle_availability_switch_${item.id}")
                )
                
                // Delete button
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Delete dish",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.75f)
                    )
                }
            }
        }
    }
}
