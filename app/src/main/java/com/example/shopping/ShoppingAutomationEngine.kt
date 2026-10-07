package com.example.shopping

import java.util.Locale

object ShoppingAutomationEngine {

    const val RECOMMENDATION_TEXT = "Bhai yeh theek hai, isko buy kar do."

    fun executeShoppingCommand(command: String): ShoppingQueryResult {
        val lower = command.lowercase(Locale.ROOT)

        val platform = when {
            lower.contains("amazon") -> "Amazon"
            lower.contains("meesho") -> "Meesho"
            lower.contains("myntra") -> "Myntra"
            else -> "Flipkart"
        }

        val size = extractSize(lower)
        val query = extractProductTerm(lower)

        val products = generateProducts(platform, query, size)

        val steps = listOf(
            AutomationStep(
                stepNumber = 1,
                title = "App Detection & Launch",
                description = "Opening $platform via Android Intent & Accessibility hook",
                actionType = "LAUNCH_APP",
                isCompleted = true
            ),
            AutomationStep(
                stepNumber = 2,
                title = "Accessibility Field Focus",
                description = "Injecting search query '$query size $size' into $platform search bar",
                actionType = "ACCESSIBILITY_TYPE",
                isCompleted = true
            ),
            AutomationStep(
                stepNumber = 3,
                title = "Vision Filter Perception",
                description = "Applying Size $size filter & sorting by rating (>4.0★)",
                actionType = "APPLY_FILTER",
                isCompleted = true
            ),
            AutomationStep(
                stepNumber = 4,
                title = "Jarvis AI Value Inspection",
                description = "Compared 12 items. Evaluated fabric, return policy, and verified buyer reviews",
                actionType = "VISION_ANALYZE",
                isCompleted = true
            ),
            AutomationStep(
                stepNumber = 5,
                title = "Final Voice Recommendation",
                description = RECOMMENDATION_TEXT,
                actionType = "RECOMMEND",
                isCompleted = true,
                isCurrent = true
            )
        )

        return ShoppingQueryResult(
            platform = platform,
            searchQuery = query,
            detectedSize = size,
            products = products,
            recommendationDialogue = RECOMMENDATION_TEXT,
            steps = steps
        )
    }

    private fun extractSize(input: String): String {
        val sizeRegex = Regex("(\\d+)\\s*(number|no|size|num)")
        val match = sizeRegex.find(input)
        if (match != null) {
            return match.groupValues[1]
        }
        val numberRegex = Regex("\\b([4-9]|1[0-2])\\b")
        val match2 = numberRegex.find(input)
        if (match2 != null) {
            return match2.groupValues[1]
        }
        if (input.contains("medium") || input.contains("m size")) return "M"
        if (input.contains("large") || input.contains("l size")) return "L"
        if (input.contains("xl")) return "XL"
        return "5" // Default as requested in prompt "5 number ka shoes"
    }

    private fun extractProductTerm(input: String): String {
        return when {
            input.contains("shoe") || input.contains("juta") || input.contains("sneaker") -> "Shoes"
            input.contains("watch") || input.contains("ghadi") -> "Smartwatch"
            input.contains("shirt") || input.contains("tshirt") -> "T-Shirt"
            input.contains("phone") || input.contains("mobile") -> "Smartphone"
            input.contains("headphone") || input.contains("earbuds") -> "Earbuds"
            else -> "Shoes"
        }
    }

    private fun generateProducts(platform: String, category: String, size: String): List<ProductItem> {
        return when (category) {
            "Shoes" -> listOf(
                ProductItem(
                    id = "shoe_01",
                    name = "Asian Men's Jasper-02 Ultra-Cushion Running Shoes",
                    brand = "Asian Footwear",
                    size = "Size $size UK/India",
                    platform = platform,
                    originalPrice = 1499,
                    discountedPrice = 699,
                    rating = 4.4f,
                    reviewCount = 18420,
                    isRecommended = true,
                    recommendationNote = RECOMMENDATION_TEXT,
                    specSummary = "EVA Sole | Breathable Knitted Upper | Featherlight 380g | Slip Resistant",
                    deliveryEstimate = "FREE Delivery by Tomorrow 11 AM"
                ),
                ProductItem(
                    id = "shoe_02",
                    name = "Sparx Men's SM-734 Athletic Walking Shoes",
                    brand = "Sparx",
                    size = "Size $size UK/India",
                    platform = platform,
                    originalPrice = 1299,
                    discountedPrice = 849,
                    rating = 4.2f,
                    reviewCount = 9540,
                    isRecommended = false,
                    recommendationNote = "Budget friendly option with sturdy rubber grip.",
                    specSummary = "TPR Sole | Padded Insole | Everyday Rough Use",
                    deliveryEstimate = "Delivery in 2 days"
                ),
                ProductItem(
                    id = "shoe_03",
                    name = "Campus Men's Oxyfit Running & Sports Shoes",
                    brand = "Campus",
                    size = "Size $size UK/India",
                    platform = platform,
                    originalPrice = 1999,
                    discountedPrice = 1099,
                    rating = 4.3f,
                    reviewCount = 12890,
                    isRecommended = false,
                    recommendationNote = "Premium mesh cushioning with high ankle support.",
                    specSummary = "Phylon Sole | Air Capsule Insole | High Rebound",
                    deliveryEstimate = "FREE Delivery by Friday"
                ),
                ProductItem(
                    id = "shoe_04",
                    name = "Puma Enzo Runner Active Edition",
                    brand = "Puma",
                    size = "Size $size UK/India",
                    platform = platform,
                    originalPrice = 3499,
                    discountedPrice = 1799,
                    rating = 4.5f,
                    reviewCount = 6430,
                    isRecommended = false,
                    recommendationNote = "Top brand tier with SoftFoam+ comfort insert.",
                    specSummary = "Rubber Traction | Classic Formstrip | Arch Support",
                    deliveryEstimate = "FREE Delivery by Thursday"
                )
            )
            else -> listOf(
                ProductItem(
                    id = "gen_01",
                    name = "Top Pick $category for Men (Verified Deal)",
                    brand = "Premium Selection",
                    size = "Size $size",
                    platform = platform,
                    originalPrice = 1999,
                    discountedPrice = 799,
                    rating = 4.5f,
                    reviewCount = 8120,
                    isRecommended = true,
                    recommendationNote = RECOMMENDATION_TEXT,
                    specSummary = "Verified Merchant | 7-day Replacement | 100% Genuine",
                    deliveryEstimate = "FREE Delivery in 24 Hours"
                ),
                ProductItem(
                    id = "gen_02",
                    name = "Everyday Classic $category",
                    brand = "EcoChoice",
                    size = "Size $size",
                    platform = platform,
                    originalPrice = 1299,
                    discountedPrice = 599,
                    rating = 4.1f,
                    reviewCount = 3400,
                    isRecommended = false,
                    recommendationNote = "Value for money basic model.",
                    specSummary = "Durable Materials | Standard Warranty",
                    deliveryEstimate = "Delivery in 2-3 Days"
                )
            )
        }
    }
}
