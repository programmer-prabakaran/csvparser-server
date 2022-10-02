package com.kotlin.csvparser.pojos

import com.kotlin.csvparser.domains.Ecommerce

data class UploadResponse(val totalRecords: Long, val data: List<Ecommerce>, val from: Long, val to:Long)
