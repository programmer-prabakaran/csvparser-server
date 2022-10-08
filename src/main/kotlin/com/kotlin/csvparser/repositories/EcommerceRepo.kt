package com.kotlin.csvparser.repositories

import com.kotlin.csvparser.domains.Ecommerce
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface EcommerceRepo: PagingAndSortingRepository<Ecommerce, Long> {
    fun findByCountryContainingIgnoreCase(country: String, pageable: Pageable): Page<Ecommerce>
    fun findByCustomerIdContainingIgnoreCase(customerId: String, pageable: Pageable): Page<Ecommerce>
    fun findByDescriptionContainingIgnoreCase(description: String, pageable: Pageable): Page<Ecommerce>
    fun findByInvoiceNumberContainingIgnoreCase(invoiceNumber: String, pageable: Pageable): Page<Ecommerce>
    fun findByQuantity(quantity: Long, pageable: Pageable): Page<Ecommerce>
    fun findByStockCodeContainingIgnoreCase(stockCode: String, pageable: Pageable): Page<Ecommerce>
    fun findByUnitPrice(quantity: Double, pageable: Pageable): Page<Ecommerce>
}