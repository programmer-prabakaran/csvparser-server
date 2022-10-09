package com.kotlin.csvparser.resources

import com.kotlin.csvparser.pojos.Response
import com.kotlin.csvparser.pojos.SearchParams
import com.kotlin.csvparser.services.EcommerceService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RestController
@RequestMapping("/api")
@CrossOrigin
class EcommerceResource(val ecommerceService: EcommerceService) {

    @GetMapping
    fun getData(@RequestParam(name = "page", defaultValue = "0") page: Int, @RequestParam(name = "size", defaultValue = "10") size: Int): Response {
        val request: Pageable = PageRequest.of(page, size)
        return ecommerceService.getAllData(request)
    }

    @PostMapping("/upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile): Response {
        return ecommerceService.parseCSV(file)
    }

    @PostMapping("/search/criteria")
    fun searchCriteriaData(@RequestBody searchParams: SearchParams, @RequestParam(name = "page", defaultValue = "0") page: Int, @RequestParam(name = "size", defaultValue = "10") size: Int): Response {
        val request: Pageable = PageRequest.of(page, size)
        return ecommerceService.searchCriteria(searchParams, request)
    }

}