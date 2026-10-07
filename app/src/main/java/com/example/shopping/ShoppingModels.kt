package com.example.shopping

data class ProductItem(
    val id: String,
    val name: String,
    val brand: String,
    val size: String,
    val platform: String,
    val originalPrice: Int,
    val discountedPrice: Int,
    val rating: Float,
    val reviewCount: Int,
    val isRecommended: Boolean,
    val recommendationNote: String,
    val specSummary: String,
    val deliveryEstimate: String,
    val inStock: Boolean = true
) {
    val discountPercent: Int
        get() = if (originalPrice > discountedPrice && originalPrice > 0) {
            (((originalPrice - discountedPrice).toFloat() / originalPrice) * 100).toInt()
        } else 0
}

data class AutomationStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val actionType: String, // "LAUNCH_APP", "ACCESSIBILITY_TYPE", "APPLY_FILTER", "VISION_ANALYZE", "RECOMMEND"
    val isCompleted: Boolean = false,
    val isCurrent: Boolean = false
)

data class ShoppingQueryResult(
    val platform: String,
    val searchQuery: String,
    val detectedSize: String,
    val products: List<ProductItem>,
    val recommendationDialogue: String = "Bhai yeh theek hai, isko buy kar do.",
    val steps: List<AutomationStep>
)
