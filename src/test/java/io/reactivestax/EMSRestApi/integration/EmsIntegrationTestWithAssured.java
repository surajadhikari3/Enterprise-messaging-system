//package io.reactivestax.EMSRestApi.integration;
//
//import io.reactivestax.EMSRestApi.EmsRestApiApplication;
//import io.reactivestax.EMSRestApi.domain.Email;
//import io.reactivestax.EMSRestApi.domain.Phone;
//import io.reactivestax.EMSRestApi.domain.Sms;
//import io.restassured.RestAssured;
//import io.restassured.response.Response;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//import static io.restassured.RestAssured.given;
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@SpringBootTest(classes = EmsRestApiApplication.class)
//class EmsIntegrationTestWithAssured {
//
//    private static final String BASE_URL = "http://localhost:8080/api/ems";
//
//    @BeforeAll
//    public static void setup() {
//        RestAssured.baseURI = "http://localhost";
//        RestAssured.port = 8080;
//        try {
//            given()
//                    .baseUri("http://localhost")
//                    .port(8080)
//                    .when()
//                    .get(BASE_URL)
//                    .then()
//                    .statusCode(200);
//        } catch (Exception e) {
//            throw new IllegalStateException("The Spring Boot application is not running or the endpoint is unavailable", e);
//        }
//    }
//
//    @Test
//     void doCrudIntegrationTestWithRestAssured() {
//        // Test Email Entity
//        Email emailWithoutId = Email.builder()
//                .id(null)
//                .clientId(101L)
//                .receiverEmailId("suraj@fullstack.digital")
//                .subject("Test Subject")
//                .body("This is a test email body.")
//                .build();
//        Email createdEmail = createEntity(emailWithoutId, "/emails", Email.class);
//        getEntityById(createdEmail.getId(), "/emails", Email.class, createdEmail);
//        getAllEntities("/emails", createdEmail);
//        updateEntity(createdEmail, "/emails", Email.class);
//        deleteEntity(createdEmail.getId(), "/emails");
//
//        // Test Phone Entity
//        Phone phoneWithoutId = Phone.builder()
//                .id(null)
//                .clientId(102L)
//                .outgoingPhoneNumber("4378898776")
//                .build();
//        Phone createdPhone = createEntity(phoneWithoutId, "/phones", Phone.class);
//        getEntityById(createdPhone.getId(), "/phones", Phone.class, createdPhone);
//        getAllEntities("/phones", createdPhone);
//        updateEntity(createdPhone, "/phones", Phone.class);
//        deleteEntity(createdPhone.getId(), "/phones");
//
//        // Test Sms Entity
//        Sms smsWithoutId = Sms.builder()
//                .id(null)
//                .clientId(103L)
//                .phone("4378898776")
//                .message("Test message")
//                .build();
//        Sms createdSms = createEntity(smsWithoutId, "/sms", Sms.class);
//        getEntityById(createdSms.getId(), "/sms", Sms.class, createdSms);
//        getAllEntities("/sms", createdSms);
//        updateEntity(createdSms, "/sms", Sms.class);
//        deleteEntity(createdSms.getId(), "/sms");
//    }
//
//    private <T> T createEntity(T entity, String endpoint, Class<T> clazz) {
//        Response response = given()
//                .log().all()
//                .contentType("application/json")
//                .body(entity)
//                .when()
//                .post(BASE_URL + endpoint)
//                .then()
//                .log().all()
//                .statusCode(200)
//                .extract()
//                .response();
//
//        T createdEntity = response.as(clazz);
//        assertThat(createdEntity).isNotNull();
//        return createdEntity;
//    }
//
//    private <T> void getEntityById(Long id, String endpoint, Class<T> clazz, T expectedEntity) {
//        Response response = given()
//                .log().all()
//                .when()
//                .get(BASE_URL + endpoint + "/" + id)
//                .then()
//                .log().all()
//                .statusCode(200)
//                .extract()
//                .response();
//
//        T retrievedEntity = response.as(clazz);
//        assertThat(retrievedEntity).isNotNull();
//        assertThat(retrievedEntity).isEqualTo(expectedEntity);
//    }
//
//    private <T> void getAllEntities(String endpoint, T expectedEntity) {
//        Response response = given()
//                .log().all()
//                .when()
//                .get(BASE_URL + endpoint)
//                .then()
//                .log().all()
//                .statusCode(200)
//                .extract()
//                .response();
//
//        List<T> entities = (List<T>) response.jsonPath().getList(".", expectedEntity.getClass());
//        assertThat(entities).isNotNull();
////        assertThat(entities).contains(expectedEntity);
//    }
//
//    private <T> void updateEntity(T entity, String endpoint, Class<T> clazz) {
//        Response response = given()
//                .log().all()
//                .contentType("application/json")
//                .body(entity)
//                .when()
//                .put(BASE_URL + endpoint)
//                .then()
//                .log().all()
//                .statusCode(200)
//                .extract()
//                .response();
//
//        T updatedEntity = response.as(clazz);
//        assertThat(updatedEntity).isNotNull();
//        assertThat(updatedEntity).isEqualTo(entity);
//    }
//
//    private void deleteEntity(Long id, String endpoint) {
//        given()
//                .log().all()
//                .when()
//                .delete(BASE_URL + endpoint + "/" + id)
//                .then()
//                .log().all()
//                .statusCode(204);
//
//        // Verify deletion
//        given()
//                .log().all()
//                .when()
//                .get(BASE_URL + endpoint + "/" + id)
//                .then()
//                .log().all()
//                .statusCode(404);
//    }
//}
//
