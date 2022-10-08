package com.kotlin.csvparser.services

import com.kotlin.csvparser.domains.Ecommerce
import com.kotlin.csvparser.pojos.Response
import com.kotlin.csvparser.pojos.SearchParams
import com.kotlin.csvparser.pojos.UploadResponse
import com.kotlin.csvparser.repositories.EcommerceRepo
import com.opencsv.CSVReader
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


@Service
class EcommerceService(val ecommerceRepo: EcommerceRepo) {

    fun getAllData(pageable: Pageable): Response {
        val data: Page<Ecommerce> = ecommerceRepo.findAll(pageable)

        return Response(true, null, data);
    }

    fun parseCSV(file: MultipartFile): Response {
        if(!file.contentType.equals("text/csv")) {
            return Response(false, "File format is not valid.", null)
        }
        val fileFormat = file.originalFilename?.split(".")
        if(!fileFormat?.get(1).equals("csv")) {
            return Response(false, "File extension is not valid.", null)
        }

        return parseData(Optional.of(file))
    }

    fun searchData(searchParams: SearchParams, pageable: Pageable): Response {

        if(!searchParams.country.isNullOrEmpty()) {
            return Response(true, null, ecommerceRepo.findByCountryContainingIgnoreCase(searchParams.country, pageable))
        }

        if(!searchParams.customerId.isNullOrEmpty()) {
            return Response(true, null, ecommerceRepo.findByCustomerIdContainingIgnoreCase(searchParams.customerId, pageable))
        }

        if(!searchParams.description.isNullOrEmpty()) {
            return Response(true, null, ecommerceRepo.findByDescriptionContainingIgnoreCase(searchParams.description, pageable))
        }

        if(!searchParams.invoiceNumber.isNullOrEmpty()) {
            return Response(true, null, ecommerceRepo.findByInvoiceNumberContainingIgnoreCase(searchParams.invoiceNumber, pageable))
        }

        if(searchParams.quantity != null) {
            return Response(true, null, ecommerceRepo.findByQuantity(searchParams.quantity, pageable))
        }

        if(!searchParams.stockCode.isNullOrEmpty()) {
            return Response(true, null, ecommerceRepo.findByStockCodeContainingIgnoreCase(searchParams.stockCode, pageable))
        }

        if(searchParams.unitPrice != null) {
            return Response(true, null, ecommerceRepo.findByUnitPrice(searchParams.unitPrice, pageable))
        }

        return Response(false, "Search failed", null)
    }

    fun getParsedDate(input: String): LocalDateTime {
        var str = input
        return if(str.contains("/")) {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            LocalDateTime.parse(format.format(Date(str)).replace(" ","T"))
        } else {
            str = str.replace("  ", " ")
            str = str.replace("-", "/")
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

            LocalDateTime.parse(format.format(Date(str)).replace(" ", "T"))
        }
    }

    fun parseData(fileInput: Optional<MultipartFile>): Response {
        if(fileInput.isEmpty) {
            return Response(false, "File is not valid", null)
        }

        val ips = InputStreamReader(fileInput.get().inputStream)
        val reader = BufferedReader(ips)
        try {
            val csvReader = CSVReader(reader)
            var line = csvReader.readNext()
            val list: MutableList<List<String>> = mutableListOf()
            while (line != null) {
                list.add(line.toList())
                line = csvReader.readNext()
            }

            val samples: MutableList<Ecommerce> = mutableListOf()
            list.stream().skip(1).forEach{
                samples.add(Ecommerce(null, it[0],it[1],it[2],it[3].toLong(), getParsedDate(it[4]), it[5].toDouble(), it[6], it[7]))
            }
            if(samples.isNotEmpty()) {
                val updatedData = ecommerceRepo.saveAll(samples)
                val obj = UploadResponse(updatedData.count())

                return Response(true, "File uploaded successfully.", obj)
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        } finally {
            reader.close()
            ips.close()

        }
        return Response(false, "File upload process failed.", null)
    }

}