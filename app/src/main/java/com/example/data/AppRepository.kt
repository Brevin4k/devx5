package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val dao: AppDao) {
    // Orders
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()

    suspend fun getOrderById(id: Long): OrderEntity? = dao.getOrderById(id)

    suspend fun insertOrder(order: OrderEntity): Long = dao.insertOrder(order)

    suspend fun updateOrder(order: OrderEntity) = dao.updateOrder(order)

    suspend fun deleteOrder(id: Long) = dao.deleteOrderById(id)

    suspend fun updateOrderStatus(id: Long, status: String) = dao.updateOrderStatus(id, status)

    // Materials
    val allMaterials: Flow<List<MaterialEntity>> = dao.getAllMaterials()

    suspend fun insertMaterial(material: MaterialEntity): Long = dao.insertMaterial(material)

    suspend fun updateMaterial(material: MaterialEntity) = dao.updateMaterial(material)

    suspend fun deleteMaterial(id: Long) = dao.deleteMaterialById(id)

    // Settings
    val settings: Flow<SettingsEntity?> = dao.getSettings()

    suspend fun saveSettings(settings: SettingsEntity) = dao.saveSettings(settings)
}
