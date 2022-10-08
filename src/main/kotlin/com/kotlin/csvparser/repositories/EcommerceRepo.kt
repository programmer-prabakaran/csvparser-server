package com.kotlin.csvparser.repositories

import com.kotlin.csvparser.domains.Ecommerce
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface EcommerceRepo: PagingAndSortingRepository<Ecommerce, Long> {
    fun findByCountryLike(country: String, pageable: Pageable): List<Ecommerce>
    fun findByCustomerIdLike(customerId: String, pageable: Pageable): List<Ecommerce>
    fun findByDescriptionLike(description: String, pageable: Pageable): List<Ecommerce>
    fun findByInvoiceNumberLike(invoiceNumber: String, pageable: Pageable): List<Ecommerce>
    fun findByQuantity(quantity: Long, pageable: Pageable): List<Ecommerce>
    fun findByStockCodeLike(stockCode: String, pageable: Pageable): List<Ecommerce>
    fun findByUnitPrice(quantity: Double, pageable: Pageable): List<Ecommerce>
}