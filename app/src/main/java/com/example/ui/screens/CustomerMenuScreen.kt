package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Timer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.RadioButton
import com.example.ui.RewardStickersState
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.MenuItem
import com.example.ui.CuisineViewModel
import com.example.ui.components.FoodIcon

@Composable
fun CustomerMenuScreen(
    viewModel: CuisineViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    
    // Dialog and State holds
    var detailProduct by remember { mutableStateOf<MenuItem?>(null) }
    var showCheckoutDialog by remember { mutableStateOf(false) }

    val menuList by viewModel.menuItems.collectAsStateWithLifecycle()
    val cartItems by viewModel.cart.collectAsStateWithLifecycle()
    val isAppShared by viewModel.isShared.collectAsStateWithLifecycle()
    val stickersState by viewModel.rewardStickersState.collectAsStateWithLifecycle()
    
    val focusManager = LocalFocusManager.current

    // Extract categories
    val categories = remember(menuList) {
        listOf("All") + menuList.map { it.category }.distinct()
    }

    // Filter menu items
    val filteredMenu = remember(menuList, searchQuery, selectedCategory) {
        menuList.filter { item ->
            val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == "All" || item.category == selectedCategory
            matchesCategory && matchesSearch
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Search Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search authentic Singju, Eromba, Thali...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input")
                )
            }

            // Categories LazyRow horizontally
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontWeight = FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            // Screen Content
            if (filteredMenu.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fastfood,
                            contentDescription = "No results",
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No authentic dishes found",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        Text(
                            "Try searching with another spelling, or change the catalog category filter.",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            modifier = Modifier.padding(top = 4.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    item {
                        SharingPromoCard(isAppShared = isAppShared, onShareClicked = { viewModel.shareApp() })
                    }
                    item {
                        StickerBookCard(stickersState = stickersState)
                    }
                    item {
                        Text(
                            text = "Authentic Manipuri Menu",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(filteredMenu) { item ->
                        CuisineItemCard(
                            item = item,
                            cartCount = cartItems[item] ?: 0,
                            onAdd = { viewModel.addToCart(item) },
                            onRemove = { viewModel.removeFromCart(item) },
                            onCardClick = { detailProduct = item }
                        )
                    }
                }
            }
        }

        // Cart floating ribbon
        AnimatedVisibility(
            visible = cartItems.isNotEmpty(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            val totalQty = cartItems.values.sum()
            val totalPrice = cartItems.entries.sumOf { it.key.price * it.value }

            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("cart_ribbon")
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "$totalQty Traditional Item${if (totalQty > 1) "s" else ""}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            "Subtotal: ₹${totalPrice.toInt()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.82f)
                        )
                    }
                    Button(
                        onClick = { showCheckoutDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimary,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("view_cart_button")
                    ) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = "Cart Icon")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("View Cart", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // DISH DETAILS MODAL
    detailProduct?.let { item ->
        Dialog(onDismissRequest = { detailProduct = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column {
                    // Beautiful custom illustration
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        FoodIcon(
                            imageType = item.imageType,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "₹${item.price.toInt()}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Category & Time chip badges
                        Row(
                            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = item.category,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Timer,
                                        contentDescription = "time",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${item.prepTimeMinutes} mins",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )

                        // Authenticity note
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Authentic info",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "This is prepared daily by local Manipuri chefs using direct fresh farm ingredients.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)
                            )
                        }

                        // Cart actions
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = { detailProduct = null },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Close")
                            }
                            Button(
                                onClick = {
                                    viewModel.addToCart(item)
                                    detailProduct = null
                                },
                                modifier = Modifier.weight(1.5f)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "add count")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add to basket")
                            }
                        }
                    }
                }
            }
        }
    }

    // CHECKOUT BASKET DIALOG
    if (showCheckoutDialog) {
        var custName by remember { mutableStateOf("") }
        var custPhone by remember { mutableStateOf("") }
        var custAddress by remember { mutableStateOf("") }
        var chosenPaymentMode by remember { mutableStateOf("Cash on Delivery") }

        // Compute active promos user qualifies for
        val sharingDiscountPercent = if (isAppShared) 10 else 0
        val stickerDiscountPercent = stickersState.rewardDiscountPercent
        val bestDiscountPercent = maxOf(sharingDiscountPercent, stickerDiscountPercent)

        var selectedDiscountPercent by remember(bestDiscountPercent) { mutableStateOf(bestDiscountPercent) }

        Dialog(onDismissRequest = { showCheckoutDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Your delivery basket",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showCheckoutDialog = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close basket")
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Cart detailed listing
                    items(cartItems.entries.toList()) { entry ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(entry.key.name, fontWeight = FontWeight.SemiBold)
                                Text(
                                    "₹${entry.key.price.toInt()} each",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.removeFromCart(entry.key) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Remove")
                                }
                                Text(
                                    "${entry.value}",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                IconButton(
                                    onClick = { viewModel.addToCart(entry.key) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add")
                                }
                            }
                        }
                    }

                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        val totalPrice = cartItems.entries.sumOf { it.key.price * it.value }.toInt()
                        val calculatedDiscount = (totalPrice * selectedDiscountPercent / 100.0).toInt()
                        val finalPrice = totalPrice - calculatedDiscount

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Text("₹$totalPrice", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                        if (calculatedDiscount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CardGiftcard, contentDescription = "Gift Icon", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Applied Discount ($selectedDiscountPercent%)", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                }
                                Text("-₹$calculatedDiscount", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Order Total", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                            Text("₹$finalPrice", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Delivery Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = custName,
                            onValueChange = { custName = it },
                            label = { Text("Your Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Person") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_name_field")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = custPhone,
                            onValueChange = { custPhone = it },
                            label = { Text("Phone Number") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_phone_field")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = custAddress,
                            onValueChange = { custAddress = it },
                            label = { Text("Delivery Address (within Imphal)") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Location") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("checkout_address_field")
                        )
                    }

                    item {
                        Text(
                            "Choose Payment Method",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Cash on Delivery Option
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { chosenPaymentMode = "Cash on Delivery" }
                                    .background(
                                        if (chosenPaymentMode == "Cash on Delivery") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                                        else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                RadioButton(
                                    selected = chosenPaymentMode == "Cash on Delivery",
                                    onClick = { chosenPaymentMode = "Cash on Delivery" },
                                    modifier = Modifier.testTag("pay_cod_radio")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Cash on Delivery (COD)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                    Text("Pay in cash or UPI to driver on delivery of hot food.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }
                            }

                            // Prepaid Option
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { chosenPaymentMode = "Prepaid (UPI / Cards)" }
                                    .background(
                                        if (chosenPaymentMode == "Prepaid (UPI / Cards)") MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                                        else Color.Transparent,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                RadioButton(
                                    selected = chosenPaymentMode == "Prepaid (UPI / Cards)",
                                    onClick = { chosenPaymentMode = "Prepaid (UPI / Cards)" },
                                    modifier = Modifier.testTag("pay_prepaid_radio")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Prepaid (UPI / Cards / Wallet)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                    Text("Simulates instant payment. Earns 'Prepaid Pioneer' sticker!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            "Select Coupon or Offer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Option 1: No Discount
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedDiscountPercent = 0 }
                                    .background(
                                        if (selectedDiscountPercent == 0) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)
                                        else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                RadioButton(
                                    selected = selectedDiscountPercent == 0,
                                    onClick = { selectedDiscountPercent = 0 }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("No discount code applied", style = MaterialTheme.typography.bodyMedium)
                            }

                            // Option 2: App sharing (If shared)
                            if (isAppShared) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedDiscountPercent = 10 }
                                        .background(
                                            if (selectedDiscountPercent == 10) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)
                                            else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp)
                                ) {
                                    RadioButton(
                                        selected = selectedDiscountPercent == 10,
                                        onClick = { selectedDiscountPercent = 10 }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.Share, contentDescription = "Promo", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("10% Off — App Share Promo Code", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                }
                            }

                            // Option 3: Sticker badges (If any count)
                            if (stickersState.rewardDiscountPercent > 0) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedDiscountPercent = stickersState.rewardDiscountPercent }
                                        .background(
                                            if (selectedDiscountPercent == stickersState.rewardDiscountPercent) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f)
                                            else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp)
                                ) {
                                    RadioButton(
                                        selected = selectedDiscountPercent == stickersState.rewardDiscountPercent,
                                        onClick = { selectedDiscountPercent = stickersState.rewardDiscountPercent }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.CardGiftcard, contentDescription = "Promo", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${stickersState.rewardDiscountPercent}% Off — ${stickersState.totalUnlockedCount} Stickers Collected", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                                }
                            }
                        }
                    }

                    item {
                        val totalPrice = cartItems.entries.sumOf { it.key.price * it.value }.toInt()
                        val calculatedDiscount = (totalPrice * selectedDiscountPercent / 100.0)

                        Button(
                            onClick = {
                                if (cartItems.isNotEmpty()) {
                                    viewModel.checkout(
                                        name = custName,
                                        phone = custPhone,
                                        address = custAddress,
                                        paymentMethod = chosenPaymentMode,
                                        discountAmount = calculatedDiscount
                                    )
                                    showCheckoutDialog = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .testTag("place_order_button")
                        ) {
                            Text("Confirm & Track Order", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CuisineItemCard(
    item: MenuItem,
    cartCount: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (item.isAvailable) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("menu_item_card_${item.imageType}")
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food visual custom drawing index
            FoodIcon(
                imageType = item.imageType,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (!item.isAvailable) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                "Sold Out",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.64f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${item.price.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (item.isAvailable) {
                        if (cartCount == 0) {
                            IconButton(
                                onClick = onAdd,
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .testTag("add_item_btn_${item.id}")
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add item",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    "$cartCount",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                IconButton(onClick = onAdd, modifier = Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SharingPromoCard(
    isAppShared: Boolean,
    onShareClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isAppShared) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("sharing_promo_card")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (isAppShared) MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = if (isAppShared) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Share & Get 10% Off! 📱✨",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (isAppShared) "Coupon activated! You will receive 10% off on your next checkout."
                    else "Share MAHAO SINGJU with local food lovers to instantly toggle a 10% discount coupon!",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 2.dp)
                )

                if (!isAppShared) {
                    Button(
                        onClick = {
                            onShareClicked()
                            Toast.makeText(context, "Sharing link copied! Promo activated 🥳", Toast.LENGTH_SHORT).show()
                            
                            // Simulate sending actual share intent
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "Taste authentic Meitei Manipuri recipes on MAHAO SINGJU app! Get delicious fresh Singju, Eromba and Chak-hao Kheer delivered to Keishampat, Imphal. Download here!")
                                type = "text/plain"
                            }
                            try {
                                context.startActivity(Intent.createChooser(sendIntent, "Share MAHAO SINGJU via"))
                            } catch (e: Exception) {
                                // Safe fallback if no activity
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .testTag("share_app_btn")
                    ) {
                        Text("Share via Whatsapp / SMS", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    }
                } else {
                    Text(
                        text = "✅ ACTIVE COUPON",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StickerBookCard(
    stickersState: RewardStickersState,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("sticker_book_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Achievements",
                        tint = Color(0xFFF57C00),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Gourmet Sticker Book",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "${stickersState.rewardDiscountPercent}% Reward",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                "Earn stickers to accumulate up to 15% discount on checkout!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            // Sticker Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Sticker 1
                StickerBadgeItem(
                    title = "First Bite",
                    description = "1st Order Placed",
                    isUnlocked = stickersState.stickerFirstBite,
                    emoji = "🥟",
                    modifier = Modifier.weight(1.5f)
                )

                // Sticker 2
                StickerBadgeItem(
                    title = "Prepaid King",
                    description = "Used Prepaid",
                    isUnlocked = stickersState.stickerPrepaidPioneer,
                    emoji = "💳",
                    modifier = Modifier.weight(1.5f)
                )

                // Sticker 3
                StickerBadgeItem(
                    title = "Promo Star",
                    description = "App Shared",
                    isUnlocked = stickersState.stickerSocialAmbassador,
                    emoji = "📢",
                    modifier = Modifier.weight(1.5f)
                )

                // Sticker 4
                StickerBadgeItem(
                    title = "Singju Fanatic",
                    description = "Bought > ₹300",
                    isUnlocked = stickersState.stickerEliteConnoisseur,
                    emoji = "🏆",
                    modifier = Modifier.weight(1.5f)
                )
            }
        }
    }
}

@Composable
fun StickerBadgeItem(
    title: String,
    description: String,
    isUnlocked: Boolean,
    emoji: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isUnlocked) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Emoticon or representation
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = if (isUnlocked) Color(0xFFFFE082) else Color.Transparent,
                        shape = RoundedCornerShape(18.dp)
                    )
            ) {
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelSmall,
                color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (isUnlocked) "Unlocked!" else "Locked",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = if (isUnlocked) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
