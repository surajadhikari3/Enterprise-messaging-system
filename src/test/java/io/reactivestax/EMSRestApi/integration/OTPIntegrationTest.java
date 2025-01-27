package io.reactivestax.EMSRestApi.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.reactivestax.EMSRestApi.dto.OtpDTO;
import io.reactivestax.EMSRestApi.enums.Status;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OTPIntegrationTest {

    @LocalServerPort
    private int port;

    private String BASE_URL;

    @BeforeAll
     void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        BASE_URL = "http://localhost:" + port + "/api/v1/otp";

    }

    @Test
     void testCreateOtpForSms() {
        OtpDTO otpDTO = OtpDTO.builder()
                .phone("8893345434")
                .email("suraj@fullstack.digital")
                .isLocked(false)
                .clientId(8L)
                .build();

        Response response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(otpDTO)
                .when()
                .post(BASE_URL + "/sms")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        OtpDTO createdOtp = response.as(OtpDTO.class);
        assertThat(createdOtp).isNotNull();
        assertThat(createdOtp.getOtpId()).isNotNull();
        assertThat(createdOtp.getPhone()).isEqualTo("8893345434");
        assertThat(createdOtp.getEmail()).isEqualTo("suraj@fullstack.digital");
    }

    @Test
     void testCreateOtpForCall() {
        OtpDTO otpDTO = OtpDTO.builder()
                .phone("9886543210")
                .isLocked(false)
                .clientId(8L)
                .build();

        Response response = given()
                .contentType(ContentType.JSON)
                .body(otpDTO)
                .when()
                .post("/api/v1/otp/call")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        OtpDTO createdOtp = response.as(OtpDTO.class);
        assertThat(createdOtp).isNotNull();
        assertThat(createdOtp.getOtpId()).isNotNull();
        assertThat(createdOtp.getPhone()).isEqualTo("9886543210");
    }
    @Test
     void testStatusForOtp() {
        long clientId = 8L;

        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/v1/otp/status/" + clientId)
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();

        Status status = response.as(Status.class);
        assertThat(status).isEqualTo(Status.VALID);
    }
}
