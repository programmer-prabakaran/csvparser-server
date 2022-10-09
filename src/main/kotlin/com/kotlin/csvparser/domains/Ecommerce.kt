package com.kotlin.csvparser.domains

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "ECOMMERCE")
data class Ecommerce(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,
    val invoiceNumber: String,
    val stockCode: String,
    val description: String,
    val quantity: Int,
    val invoiceDate: LocalDateTime,
    val unitPrice: Double,
    val customerId: String,
    val country: String
)
