package com.example.data.repository

import com.example.data.local.MenuDao
import com.example.data.local.OrderDao
import com.example.data.model.MenuItem
import com.example.data.model.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CuisineRepository(
    private val menuDao: MenuDao,
    private val orderDao: OrderDao
) {
    val menuItems: Flow<List<MenuItem>> = menuDao.getAllMenuItems()
    val allOrders: Flow<List<Order>> = orderDao.getAllOrders()

    fun getOrderById(orderId: Int): Flow<Order?> = orderDao.getOrderById(orderId)

    suspend fun populateMenuIfEmpty() {
        val count = menuDao.getMenuItemCount()
        if (count == 0) {
            val initialList = listOf(
                MenuItem(
                    name = "Classic Singju",
                    description = "Fiery traditional Manipuri salad of finely shredded lotus root, cabbage, raw papaya, and roasted chickpea powder, mixed with roasted red chili paste.",
                    price = 120.0,
                    category = "Singju & Salads",
                    imageType = "singju",
                    isAvailable = true,
                    prepTimeMinutes = 10
                ),
                MenuItem(
                    name = "Yongchak/Tree-bean Singju",
                    description = "Highly-prized seasonal Manipur special featuring local tree beans (Yongchak) shredded with cabbage, rich herbs, toasted sesame, and traditional spicy dundalk.",
                    price = 160.0,
                    category = "Singju & Salads",
                    imageType = "singju",
                    isAvailable = true,
                    prepTimeMinutes = 12
                ),
                MenuItem(
                    name = "Traditional Sareng Fish Thali",
                    description = "A premium gourmet meal of authentic Steamed rice, traditional high-value Sareng fish curry, robust Eromba, fresh Singju salad, warm vegetable soup, and local Chutney.",
                    price = 320.0,
                    category = "Main Course",
                    imageType = "thali",
                    isAvailable = true,
                    prepTimeMinutes = 25
                ),
                MenuItem(
                    name = "Manipuri Veg Thali",
                    description = "Healthy platter featuring black/white rice, Kangshoi vegetable stew, boiled potato-shoot Eromba, Ooti (pea stew), and Singju salad side.",
                    price = 200.0,
                    category = "Main Course",
                    imageType = "thali",
                    isAvailable = true,
                    prepTimeMinutes = 20
                ),
                MenuItem(
                    name = "Organic Bamboo Shoot Eromba",
                    description = "A spicy, oil-free rustic delicacy made of mashed potatoes, boiled bamboo shoots, smoked chilies, and flavored with authentic fermented fish (Ngari).",
                    price = 140.0,
                    category = "Side Dishes",
                    imageType = "eromba",
                    isAvailable = true,
                    prepTimeMinutes = 15
                ),
                MenuItem(
                    name = "Chamthong / Kangshoi",
                    description = "A comforting, restorative broth loaded with fresh green vegetables, ginger, sliced garlic, bay leaves, and flavored lightly with grilled fish paste.",
                    price = 130.0,
                    category = "Soups & Stews",
                    imageType = "kangshoi",
                    isAvailable = true,
                    prepTimeMinutes = 12
                ),
                MenuItem(
                    name = "Royal Chak-hao Kheer",
                    description = "Exquisite traditional dessert cooked with GI-tagged Manipur organic black wild rice, infused with whole milk, sweet cardamoms, cashews, and raisins. Vibrant royal purple hue!",
                    price = 110.0,
                    category = "Desserts",
                    imageType = "kheer",
                    isAvailable = true,
                    prepTimeMinutes = 15
                ),
                MenuItem(
                    name = "Heikru Lemon Drink",
                    description = "Refreshing local traditional drink made from squeezed Indian gooseberries (Heikru) and fresh mint, perfect accompaniment to spicy food.",
                    price = 60.0,
                    category = "Desserts",
                    imageType = "generic",
                    isAvailable = true,
                    prepTimeMinutes = 5
                )
            )
            menuDao.insertMenuItems(initialList)
        }
    }

    suspend fun insertMenuItem(item: MenuItem) {
        menuDao.insertMenuItem(item)
    }

    suspend fun updateMenuItem(item: MenuItem) {
        menuDao.updateMenuItem(item)
    }

    suspend fun deleteMenuItem(item: MenuItem) {
        menuDao.deleteMenuItem(item)
    }

    suspend fun placeOrder(order: Order): Long {
        return orderDao.insertOrder(order)
    }

    suspend fun updateOrder(order: Order) {
        orderDao.updateOrder(order)
    }

    suspend fun clearAllOrders() {
        orderDao.clearAllOrders()
    }
}
