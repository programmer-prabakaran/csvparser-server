package com.kotlin.csvparser.pojos

import java.time.LocalDateTime

data class Sample(val invoiceNumber: String,
                  val stockCode: String,
                  val description: String,
                  val quantity: Long,
                  val invoiceDate: LocalDateTime,
                  val unitPrice: Double,
                  val customerId: String,
                  val country: String)
