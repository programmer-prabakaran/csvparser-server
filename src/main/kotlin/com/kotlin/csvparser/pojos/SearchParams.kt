package com.kotlin.csvparser.pojos

data class SearchParams(
    val invoiceNumber: String?,
    val stockCode: String?,
    val description: String?,
    val quantity: Long?,
    val unitPrice: Double?,
    val customerId: String?,
    val country: String?
)
