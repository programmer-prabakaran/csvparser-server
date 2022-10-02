package com.kotlin.csvparser.repositories

import com.kotlin.csvparser.domains.Ecommerce
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface EcommerceRepo: JpaRepository<Ecommerce, Long> {
    @Query("SELECT * FROM ecommerce where id>=?1 and id<=?2 order by id", nativeQuery = true)
    fun getDataWithPagination(from: Long, to: Long): List<Ecommerce>

    @Query("SELECT count(*) FROM ecommerce", nativeQuery = true)
    fun getRecordsCount(): Long
}