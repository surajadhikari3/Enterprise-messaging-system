package io.reactivestax.EMSRestApi.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.reactivestax.EMSRestApi.dto.OtpDTO;
import io.reactivestax.EMSRestApi.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
class OTPControllerTest {

    @BeforeEach
     void setup() {
        RestAssured.baseURI = "http://localhost:8080";  // Change this to your server URI if needed
    }

    @Test
     void testCreateOtpForSms() {
        OtpDTO otpDTO = OtpDTO.builder()
                .otpId(1L)
                .validOtp("123456")
                .createdAt(LocalDateTime.now())
                .lastAccessed(LocalDateTime.now())
                .generationTimeStamps(Collections.singletonList(LocalDateTime.now()))
                .validationRetryCount(0)
                .otpStatus(Status.PENDING)
                .verificationStatus(Status.PENDING)
                .phone("1234567890")
                .isLocked(false)
                .clientId(1L)
                .build();

        given()
            .contentType(ContentType.JSON)
            .body(otpDTO)
        .when()
            .post("/api/v1/otp/sms")
        .then()
            .statusCode(200)
            .body("validOtp", equalTo("123456"))
            .body("phone", equalTo("1234567890"));
    }

    @Test
     void testCreateOtpForCall() {
        OtpDTO otpDTO = OtpDTO.builder()
                .otpId(2L)
                .validOtp("654321")
                .createdAt(LocalDateTime.now())
                .lastAccessed(LocalDateTime.now())
                .generationTimeStamps(Collections.singletonList(LocalDateTime.now()))
                .validationRetryCount(0)
                .otpStatus(Status.PENDING)
                .verificationStatus(Status.PENDING)
                .phone("9876543210")
                .isLocked(false)
                .clientId(2L)
                .build();

        given()
            .contentType(ContentType.JSON)
            .body(otpDTO)
        .when()
            .post("/api/v1/otp/call")
        .then()
            .statusCode(200)
            .body("validOtp", equalTo("654321"))
            .body("phone", equalTo("9876543210"));
    }

    @Test
     void testCreateOtpForEmail() {
        OtpDTO otpDTO = OtpDTO.builder()
                .otpId(3L)
                .validOtp("456783")
                .createdAt(LocalDateTime.now())
                .lastAccessed(LocalDateTime.now())
                .generationTimeStamps(Collections.singletonList(LocalDateTime.now()))
                .validationRetryCount(0)
                .otpStatus(Status.PENDING)
                .verificationStatus(Status.PENDING)
                .email("suraj@fullstack.digital")
                .isLocked(false)
                .clientId(3L)
                .build();

        given()
            .contentType(ContentType.JSON)
            .body(otpDTO)
        .when()
            .post("/api/v1/otp/email")
        .then()
            .statusCode(200)
            .body("validOtp", equalTo("456783"))
            .body("email", equalTo("suraj@fullstack.digital"));
    }

    @Test
     void testVerifyOtp() {
        OtpDTO otpDTO = OtpDTO.builder()
                .otpId(4L)
                .validOtp("654321")
                .createdAt(LocalDateTime.now())
                .lastAccessed(LocalDateTime.now())
                .generationTimeStamps(Collections.singletonList(LocalDateTime.now()))
                .validationRetryCount(1)
                .otpStatus(Status.VERIFIED)
                .verificationStatus(Status.VERIFIED)
                .phone("1234567890")
                .isLocked(false)
                .clientId(4L)
                .build();

        given()
            .contentType(ContentType.JSON)
            .body(otpDTO)
        .when()
            .put("/api/v1/otp/verify")
        .then()
            .statusCode(200)
            .body("verificationStatus", equalTo(Status.VERIFIED.name()));
    }

    @Test
     void testStatusForOtp() {
        Long clientId = 1L;

        given()
            .contentType(ContentType.JSON)
        .when()
            .get("/api/v1/otp/status/" + clientId)
        .then()
            .statusCode(200)
            .body(equalTo(Status.PENDING.name()));
    }
}
