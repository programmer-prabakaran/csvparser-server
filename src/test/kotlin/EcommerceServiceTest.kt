import com.kotlin.csvparser.domains.Ecommerce
import com.kotlin.csvparser.repositories.EcommerceRepo
import com.kotlin.csvparser.services.EcommerceService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime
import javax.persistence.EntityManager


class EcommerceServiceTest {
    var entityManager = Mockito.mock(EntityManager::class.java)
    var ecommerceRepo = Mockito.mock(EcommerceRepo::class.java)

    private lateinit var ecommerceService: EcommerceService

    @BeforeEach
    fun setup() {
        val ecommerce = Ecommerce(1, "12345", "12345", "test", 1, LocalDateTime.now(), 2.12, "1234", "United Kingdom")

        val result = mutableListOf<Ecommerce>()
        result.add(ecommerce)
        Mockito.`when`(ecommerceRepo.findAll()).thenReturn(result)
        ecommerceService = EcommerceService(ecommerceRepo, entityManager)
    }

    @Test
    fun repoTest() {
        val request: Pageable = PageRequest.of(0, 5)
        val response = ecommerceService.getAllData(request)

        assert(response.status)

    }
}