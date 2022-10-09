package com.kotlin.csvparser.services

import com.kotlin.csvparser.domains.Ecommerce
import com.kotlin.csvparser.pojos.Response
import com.kotlin.csvparser.pojos.SearchParams
import com.kotlin.csvparser.pojos.UploadResponse
import com.kotlin.csvparser.repositories.EcommerceRepo
import com.opencsv.CSVReader
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.criteria.Path
import javax.persistence.criteria.Predicate


@Service
class EcommerceService(val ecommerceRepo: EcommerceRepo, val em: EntityManager) {

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

    fun searchCriteria(searchParams: SearchParams, pageable: Pageable): Response {
        val builder = em.criteriaBuilder
        val criteria = builder.createQuery(Ecommerce::class.java)
        val root = criteria.from(Ecommerce::class.java)

        val predicates = mutableListOf<Predicate>()

        if(!searchParams.country.isNullOrEmpty()) {
            predicates.add(builder.like(builder.lower(root.get("country")),"%"+searchParams.country.lowercase()+"%"))
        }

        if(!searchParams.invoiceNumber.isNullOrEmpty()) {
            predicates.add(builder.equal(builder.lower(root.get("invoiceNumber")),searchParams.invoiceNumber.lowercase()))
        }

        if(searchParams.unitPrice != null && searchParams.unitPriceType != null) {
            val up: Path<Double> = root.get("unitPrice")
            when (searchParams.unitPriceType) {
                "=" -> {
                    predicates.add(builder.equal(up, searchParams.unitPrice))
                }
                ">=" -> {
                    predicates.add(builder.greaterThanOrEqualTo(root.get("unitPrice"),searchParams.unitPrice))
                }
                "<=" -> {
                    predicates.add(builder.lessThanOrEqualTo(root.get("unitPrice"),searchParams.unitPrice))
                }
            }
        }

        if(searchParams.quantity != null && searchParams.quantityType != null) {
            val q: Path<Int> = root.get("quantity")
            when (searchParams.quantityType) {
                "=" -> {
                    predicates.add(builder.equal(q, searchParams.quantity))
                }
                ">=" -> {
                    predicates.add(builder.greaterThanOrEqualTo(root.get("unitPrice"),searchParams.quantity))
                }
                "<=" -> {
                    predicates.add(builder.lessThanOrEqualTo(root.get("unitPrice"),searchParams.quantity))
                }
            }
        }

        if(searchParams.invoiceDate != null && searchParams.invoiceDateType != null) {
            val id: Path<Long> = root.get("invoiceDate")
            when (searchParams.invoiceDateType) {
                "=" -> {
                    predicates.add(builder.equal(id, searchParams.invoiceDate))
                }
                ">=" -> {
                    predicates.add(builder.greaterThanOrEqualTo(root.get("invoiceDate"),searchParams.invoiceDate))
                }
                "<=" -> {
                    predicates.add(builder.lessThanOrEqualTo(root.get("invoiceDate"),searchParams.invoiceDate))
                }
            }
        }

        if(!searchParams.stockCode.isNullOrEmpty()) {
            predicates.add(builder.equal(builder.lower(root.get("stockCode")),searchParams.stockCode.lowercase()))
        }

        if(!searchParams.customerId.isNullOrEmpty()) {
            predicates.add(builder.equal(builder.lower(root.get("customerId")),searchParams.customerId.lowercase()))
        }

        if(!searchParams.description.isNullOrEmpty()) {
            predicates.add(builder.like(builder.lower(root.get("description")),"%"+searchParams.description.lowercase()+"%"))
        }

        criteria.where(builder.and(*predicates.toTypedArray()))

        val result = em.createQuery(criteria).setFirstResult(pageable.pageNumber).setMaxResults(pageable.pageSize).resultList

        val countQuery = builder.createQuery(Long::class.java)
        val rootCount = countQuery.from(Ecommerce::class.java)

        countQuery.select(builder.count(rootCount)).where(builder.and(*predicates.toTypedArray()))

        val count = em.createQuery(countQuery).singleResult

        val finalResult: Page<Ecommerce> = PageImpl(result, pageable, count)

        return Response(true, null, finalResult)
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
                samples.add(Ecommerce(null, it[0],it[1],it[2],it[3].toInt(), getParsedDate(it[4]), it[5].toDouble(), it[6], it[7]))
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