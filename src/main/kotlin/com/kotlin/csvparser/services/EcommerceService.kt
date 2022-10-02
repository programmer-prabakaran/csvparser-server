package com.kotlin.csvparser.services

import com.kotlin.csvparser.domains.Ecommerce
import com.kotlin.csvparser.domains.FileDetails
import com.kotlin.csvparser.pojos.Response
import com.kotlin.csvparser.pojos.UploadResponse
import com.kotlin.csvparser.repositories.EcommerceRepo
import com.kotlin.csvparser.repositories.FileDetailsRepo
import com.opencsv.CSVReader
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*


@Service
class EcommerceService(val ecommerceRepo: EcommerceRepo, val fileDetailsRepo: FileDetailsRepo) {

    fun getAllData(from: Long, to: Long): Response {
        val count = ecommerceRepo.getRecordsCount()
        val response = UploadResponse(count, ecommerceRepo.getDataWithPagination(from, to), from, to)
        return Response(true, "", response)
    }

    fun parseCSV(file: MultipartFile): Response {
        val fileName = file.originalFilename
        if(!file.contentType.equals("text/csv")) {
            return Response(false, "File format is not valid.", null)
        }
        val fileFormat = file.originalFilename?.split(".")
        if(!fileFormat?.get(1).equals("csv")) {
            return Response(false, "File extension is not valid.", null)
        }

        if(fileName != null) {
            val fileExist = fileDetailsRepo.findByFileName(fileName)

            if(fileExist.isPresent) {
                return Response(false, "File Already processed.", null)
            }
        }


        val response = parseData(Optional.of(file))

        if(response.status) {
            fileDetailsRepo.save(FileDetails(null, fileName, LocalDateTime.now()))
        }

        return response;
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
        val file = File("src/main/resources/static/data.csv")
        var ips = InputStreamReader(file.inputStream())
        if(fileInput.isPresent) {
            ips = InputStreamReader(fileInput.get().inputStream)
        }
        val reader = BufferedReader(ips)
        try {
            val csvReader = CSVReader(reader)
            //println("Total file row count - "+csvReader.count())
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
            //println("Total data process row count - "+samples.count())
            if(samples.isNotEmpty()) {
                val updatedData = ecommerceRepo.saveAll(samples)
                val count = ecommerceRepo.getRecordsCount()
                val obj = UploadResponse(count.toLong(), updatedData.stream().limit(500).toList(), 0, 500)

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