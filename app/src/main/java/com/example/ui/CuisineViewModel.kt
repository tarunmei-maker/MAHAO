package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.MenuItem
import com.example.data.model.Order
import com.example.data.repository.CuisineRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CuisineViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CuisineRepository
    
    val menuItems: StateFlow<List<MenuItem>>
    val allOrders: StateFlow<List<Order>>
    val rewardStickersState: StateFlow<RewardStickersState>

    // Cart state: Map of Item Id to Quantity
    private val _cart = MutableStateFlow<Map<MenuItem, Int>>(emptyMap())
    val cart: StateFlow<Map<MenuItem, Int>> = _cart.asStateFlow()

    // Currently tracked order
    private val _trackedOrderId = MutableStateFlow<Int?>(null)
    val trackedOrderId: StateFlow<Int?> = _trackedOrderId.asStateFlow()

    // Active screen navigation (helper)
    private val _currentTab = MutableStateFlow("menu") // "menu", "track", "seller"
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // App Sharing Promotional State (rewards users with instantly applied mock discount)
    private val _isShared = MutableStateFlow(false)
    val isShared: StateFlow<Boolean> = _isShared.asStateFlow()

    fun shareApp() {
        _isShared.value = true
    }

    // Real-time GPS/Order Simulation Job
    private var simulationJob: Job? = null

    // Location coordinates of shop (Imphal West Area)
    private val shopLat = 24.8015
    private val shopLng = 93.9310

    // Coordinates of customer (gently offset)
    private val customerLat = 24.8302
    private val customerLng = 93.9555

    init {
        val database = AppDatabase.getDatabase(application)
        repository = CuisineRepository(database.menuDao(), database.orderDao())
        
        menuItems = repository.menuItems.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allOrders = repository.allOrders.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        rewardStickersState = combine(allOrders, isShared) { orders, shared ->
            val firstBite = orders.isNotEmpty()
            val prepaidPioneer = orders.any { it.paymentMethod.contains("Prepaid", ignoreCase = true) }
            val socialAmbassador = shared
            val eliteConnoisseur = orders.size >= 2 || orders.any { it.totalAmount >= 300.0 }

            var count = 0
            if (firstBite) count++
            if (prepaidPioneer) count++
            if (socialAmbassador) count++
            if (eliteConnoisseur) count++

            val discount = when (count) {
                0 -> 0
                1 -> 5
                2 -> 10
                else -> 15 // Up to 15% discount for 3+ stickers
            }

            RewardStickersState(
                stickerFirstBite = firstBite,
                stickerPrepaidPioneer = prepaidPioneer,
                stickerSocialAmbassador = socialAmbassador,
                stickerEliteConnoisseur = eliteConnoisseur,
                totalUnlockedCount = count,
                rewardDiscountPercent = discount
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RewardStickersState()
        )

        // Seed data and start monitoring
        viewModelScope.launch {
            repository.populateMenuIfEmpty()
            startSimulationLoop()
        }
    }

    fun selectTab(tab: String) {
        _currentTab.value = tab
    }

    // --- Cart Actions ---
    fun addToCart(item: MenuItem) {
        val currentMap = _cart.value.toMutableMap()
        val count = currentMap[item] ?: 0
        currentMap[item] = count + 1
        _cart.value = currentMap
    }

    fun removeFromCart(item: MenuItem) {
        val currentMap = _cart.value.toMutableMap()
        val count = currentMap[item] ?: 0
        if (count > 1) {
            currentMap[item] = count - 1
        } else {
            currentMap.remove(item)
        }
        _cart.value = currentMap
    }

    fun clearCart() {
        _cart.value = emptyMap()
    }

    // --- Order Checkout ---
    fun checkout(name: String, phone: String, address: String, paymentMethod: String = "Cash on Delivery", discountAmount: Double = 0.0) {
        viewModelScope.launch {
            val cartSnapshot = _cart.value
            if (cartSnapshot.isEmpty()) return@launch

            val summary = cartSnapshot.entries.joinToString(", ") { "${it.key.name} x${it.value}" }
            val baseAmount = cartSnapshot.entries.sumOf { it.key.price * it.value }
            val total = if (baseAmount - discountAmount > 0.0) baseAmount - discountAmount else 0.0

            val newOrder = Order(
                customerName = name.ifBlank { "Guest User" },
                customerPhone = phone.ifBlank { "+91 99999 88888" },
                customerAddress = address.ifBlank { "Keishampat, Imphal" },
                itemsSummary = summary,
                totalAmount = total,
                status = "Pending",
                progressPercent = 0.05f,
                driverLatitude = shopLat,
                driverLongitude = shopLng,
                driverName = "Chaoba Singh",
                driverPhone = "+91 98765 43210",
                paymentMethod = paymentMethod,
                discountAmount = discountAmount
            )

            val newId = repository.placeOrder(newOrder).toInt()
            _trackedOrderId.value = newId
            clearCart()
            
            // Navigate automatically directly to track delivery screen!
            _currentTab.value = "track"
        }
    }

    fun setTrackedOrder(orderId: Int) {
        _trackedOrderId.value = orderId
        _currentTab.value = "track"
    }

    // --- Seller Dashboard operations ---
    fun updateOrderStatus(order: Order, newStatus: String) {
        viewModelScope.launch {
            var progress = when(newStatus) {
                "Pending" -> 0.05f
                "Preparing" -> 0.30f
                "Out for Delivery" -> 0.65f
                "Delivered" -> 1.0f
                else -> order.progressPercent
            }
            
            val updated = order.copy(
                status = newStatus,
                progressPercent = progress,
                // Reset coordinates if status resets
                driverLatitude = if (newStatus == "Out for Delivery") shopLat else order.driverLatitude,
                driverLongitude = if (newStatus == "Out for Delivery") shopLng else order.driverLongitude
            )
            repository.updateOrder(updated)
        }
    }

    fun addMenuItem(name: String, description: String, price: Double, category: String, imageType: String) {
        viewModelScope.launch {
            val newItem = MenuItem(
                name = name,
                description = description,
                price = price,
                category = category,
                imageType = imageType
            )
            repository.insertMenuItem(newItem)
        }
    }

    fun toggleMenuItemAvailability(item: MenuItem) {
        viewModelScope.launch {
            val updated = item.copy(isAvailable = !item.isAvailable)
            repository.updateMenuItem(updated)
        }
    }

    fun deleteMenuItem(item: MenuItem) {
        viewModelScope.launch {
            repository.deleteMenuItem(item)
        }
    }

    fun resetDemo() {
        viewModelScope.launch {
            repository.clearAllOrders()
            // Reset cart
            clearCart()
            _trackedOrderId.value = null
        }
    }

    // --- Pseudo-Realtime Delivery Simulation Engine ---
    private fun startSimulationLoop() {
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            while (true) {
                delay(3000) // update state every 3 seconds

                val activeList = allOrders.value
                val currentlyTrackedId = _trackedOrderId.value

                for (order in activeList) {
                    if (order.status == "Delivered") continue

                    // Automatic progression of Pending/Preparing orders
                    if (order.status == "Pending") {
                        // Move to preparing (10% chance per loop, or if currently tracked for interactive response)
                        val shouldAdvance = (Math.random() < 0.25) || (order.id == currentlyTrackedId)
                        if (shouldAdvance) {
                            val updated = order.copy(
                                status = "Preparing",
                                progressPercent = 0.20f,
                                etaMinutes = 20
                            )
                            repository.updateOrder(updated)
                        }
                    } else if (order.status == "Preparing") {
                        // Graduate preparing step-by-step
                        val currentProgress = order.progressPercent
                        if (currentProgress < 0.50f) {
                            val updated = order.copy(
                                progressPercent = currentProgress + 0.10f
                            )
                            repository.updateOrder(updated)
                        } else {
                            // Ready for delivery
                            val updated = order.copy(
                                status = "Out for Delivery",
                                progressPercent = 0.55f,
                                etaMinutes = 15,
                                driverLatitude = shopLat,
                                driverLongitude = shopLng
                            )
                            repository.updateOrder(updated)
                        }
                    } else if (order.status == "Out for Delivery") {
                        // Simulating Delivery GPS coordinates moving closer to customer home!
                        val startLat = shopLat
                        val startLng = shopLng
                        val destLat = customerLat
                        val destLng = customerLng

                        // Current driver coordinates
                        val curLat = order.driverLatitude
                        val curLng = order.driverLongitude

                        // Total distance remaining
                        val diffLat = destLat - curLat
                        val diffLng = destLng - curLng

                        // If very close, mark as delivered
                        if (Math.abs(diffLat) < 0.002 && Math.abs(diffLng) < 0.002) {
                            val updated = order.copy(
                                status = "Delivered",
                                progressPercent = 1.0f,
                                etaMinutes = 0,
                                driverLatitude = destLat,
                                driverLongitude = destLng
                            )
                            repository.updateOrder(updated)
                        } else {
                            // Interpolate moves: leap 20% of the remaining distance
                            val stepFactor = 0.20
                            val nextLat = curLat + (diffLat * stepFactor) + ((Math.random() - 0.5) * 0.001) // with a tiny bit of drift!
                            val nextLng = curLng + (diffLng * stepFactor) + ((Math.random() - 0.5) * 0.001)
                            
                            val nextProgress = Math.min(0.95f, order.progressPercent + 0.10f)
                            val nextEta = Math.max(2, order.etaMinutes - 2)

                            val updated = order.copy(
                                driverLatitude = nextLat,
                                driverLongitude = nextLng,
                                progressPercent = nextProgress,
                                etaMinutes = nextEta
                            )
                            repository.updateOrder(updated)
                        }
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        simulationJob?.cancel()
    }
}

data class RewardStickersState(
    val stickerFirstBite: Boolean = false,
    val stickerPrepaidPioneer: Boolean = false,
    val stickerSocialAmbassador: Boolean = false,
    val stickerEliteConnoisseur: Boolean = false,
    val totalUnlockedCount: Int = 0,
    val rewardDiscountPercent: Int = 0
)
