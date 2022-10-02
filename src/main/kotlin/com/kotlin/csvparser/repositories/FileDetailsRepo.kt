package com.kotlin.csvparser.repositories

import com.kotlin.csvparser.domains.FileDetails
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface FileDetailsRepo: JpaRepository<FileDetails, Long> {
    fun findByFileName(name: String): Optional<FileDetails>
}