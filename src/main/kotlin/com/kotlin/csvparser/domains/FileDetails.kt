package com.kotlin.csvparser.domains

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "FILE_DETAILS")
data class FileDetails(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?,
    val fileName: String?,
    val processedAt: LocalDateTime
)
