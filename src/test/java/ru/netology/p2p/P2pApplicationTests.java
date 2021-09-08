package ru.netology.p2p;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import ru.netology.p2p.objectsDTO.RqConfirmDTO;
import ru.netology.p2p.objectsDTO.RqTransferDTO;
import ru.netology.p2p.objectsDTO.RsTransferDTO;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class P2pApplicationTests {
    @Autowired
    private TestRestTemplate testRestTemplate;

    private static final int PORT = 5500;
    private static final String HOST_NAME = "http://localhost:";

    private static final int BAD_REQUEST = 400;
    private static final int INTERNAL_SERVER_ERROR = 500;

    private static final Gson gson = new Gson();
    private RqConfirmDTO rqConfirm = new RqConfirmDTO()
            .setCode("0000");
    private RqTransferDTO rqTransfer = new RqTransferDTO()
            .setCardFromNumber("1111111111111111")
            .setCardToNumber("2222222222222222")
            .setCardFromValidTill("11/22")
            .setCardFromCVV("123")
            .setAmount(new RqTransferDTO.Amount(250, "810"));

    private static RsTransferDTO response;
    private static HttpEntity<String> request;
    private static ResponseEntity<String>  transferEntity;
    private static HttpHeaders headers = new HttpHeaders();

    private static final GenericContainer<?> transferService = new GenericContainer<>("service/transfer")
            .withExposedPorts(PORT);

    @BeforeAll
    public static void startDockerContainer() {
        transferService.start();
    }

    @AfterAll
    public static void stopDockerContainer() {
        transferService.stop();
    }

    @Test
    public void checkTransferValidRq() {
        final String baseURL = HOST_NAME+transferService.getMappedPort(PORT) + "/transfer";

        headers.setContentType(MediaType.APPLICATION_JSON);
        request = new HttpEntity<>(gson.toJson(rqTransfer), headers);
        transferEntity = testRestTemplate.postForEntity(baseURL, request, String.class);
        response = gson.fromJson(transferEntity.getBody(), RsTransferDTO.class);
        assertNotNull(response.getOperationId());
    }

    @Test
    public void checkConfirmValidRq() {
        String transferURL = HOST_NAME+transferService.getMappedPort(PORT) + "/transfer";
        String acceptURL = HOST_NAME+transferService.getMappedPort(PORT) + "/confirmOperation";

        headers.setContentType(MediaType.APPLICATION_JSON);
        request = new HttpEntity<>(gson.toJson(rqTransfer), headers);
        transferEntity = testRestTemplate.postForEntity(transferURL, request, String.class);
        response = gson.fromJson(transferEntity.getBody(), RsTransferDTO.class);

        rqConfirm.setOperationId(response.getOperationId());

        request = new HttpEntity<>(gson.toJson(rqConfirm), headers);
        transferEntity = testRestTemplate.postForEntity(acceptURL, request, String.class);
        response = gson.fromJson(transferEntity.getBody(), RsTransferDTO.class);
        assertNotNull(response.getOperationId());
    }

    @Test
    public void checkTransferInputError() {
        String actualResult;
        final String expectedResult = "Invalid input data";
        final String baseURL = HOST_NAME+transferService.getMappedPort(PORT) + "/transfer";

        rqTransfer.setCardToNumber("1111111111111111");

        headers.setContentType(MediaType.APPLICATION_JSON);
        request = new HttpEntity<>(gson.toJson(rqTransfer), headers);
        transferEntity = testRestTemplate.postForEntity(baseURL, request, String.class);
        actualResult = transferEntity.getBody();
        assertEquals(expectedResult, actualResult);
        assertEquals(transferEntity.getStatusCodeValue(), BAD_REQUEST);
    }

    @Test
    public void checkTransferServerError() {
        String actualResult;
        final String expectedResult = "Not enough money";
        final String baseURL = HOST_NAME+transferService.getMappedPort(PORT) + "/transfer";

        rqTransfer.setAmount(new RqTransferDTO.Amount(1000000, "810"));

        headers.setContentType(MediaType.APPLICATION_JSON);
        request = new HttpEntity<>(gson.toJson(rqTransfer), headers);
        transferEntity = testRestTemplate.postForEntity(baseURL, request, String.class);
        actualResult = transferEntity.getBody();
        assertEquals(expectedResult, actualResult);
        assertEquals(transferEntity.getStatusCodeValue(), INTERNAL_SERVER_ERROR);
    }

    @Test
    public void checkConfirmInputError() {
        String actualResult;
        final String expectedResult = "Code or operationId may not be null";
        final String baseURL = HOST_NAME+transferService.getMappedPort(PORT) + "/confirmOperation";

        rqConfirm.setCode(null);

        headers.setContentType(MediaType.APPLICATION_JSON);
        request = new HttpEntity<>(gson.toJson(rqConfirm), headers);
        transferEntity = testRestTemplate.postForEntity(baseURL, request, String.class);
        actualResult = transferEntity.getBody();
        assertEquals(expectedResult, actualResult);
        assertEquals(transferEntity.getStatusCodeValue(), BAD_REQUEST);
    }

    @Test
    public void checkConfirmInvalidGUID() {
        String actualResult;
        final String expectedResult = "Incorrect GUID";
        final String baseURL = HOST_NAME+transferService.getMappedPort(PORT) + "/confirmOperation";

        rqConfirm.setOperationId("3f457b73-0b5b-449e-8a13-55779ba0a4d7");

        headers.setContentType(MediaType.APPLICATION_JSON);
        request = new HttpEntity<>(gson.toJson(rqConfirm), headers);
        transferEntity = testRestTemplate.postForEntity(baseURL, request, String.class);
        actualResult = transferEntity.getBody();
        assertEquals(expectedResult, actualResult);
        assertEquals(transferEntity.getStatusCodeValue(), INTERNAL_SERVER_ERROR);
    }
}
