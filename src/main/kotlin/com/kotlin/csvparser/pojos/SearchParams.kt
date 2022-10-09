package com.kotlin.csvparser.pojos

import java.time.LocalDateTime

data class SearchParams(
    val invoiceNumber: String?,
    val stockCode: String?,
    val description: String?,
    val quantity: Int?,
    val quantityType: String?,
    val unitPrice: Double?,
    val unitPriceType: String?,
    val customerId: String?,
    val country: String?,
    val invoiceDate: LocalDateTime?,
    val invoiceDateType: String?
)
