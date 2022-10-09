package com.kotlin.csvparser.repositories

import com.kotlin.csvparser.domains.Ecommerce
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface EcommerceRepo: PagingAndSortingRepository<Ecommerce, Long> {
}