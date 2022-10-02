package com.kotlin.csvparser.resources

import com.kotlin.csvparser.domains.Ecommerce
import com.kotlin.csvparser.pojos.Response
import com.kotlin.csvparser.services.EcommerceService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
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
    fun getData(@RequestParam("from") from: Long, @RequestParam("to") to: Long): Response {
        return ecommerceService.getAllData(from, to)
    }

    @PostMapping("/upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile): Response {
        return ecommerceService.parseCSV(file)
    }

    @PostMapping("/static/upload")
    fun staticUpload(): Response {
        return ecommerceService.parseData(Optional.empty())
    }
}