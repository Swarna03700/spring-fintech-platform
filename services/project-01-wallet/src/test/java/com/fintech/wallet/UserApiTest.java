package com.fintech.wallet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserApiTest {
	
	@LocalServerPort
	private int port;
	
	private final String accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImdwLW84SWx4UWlHODVDYXJTd2NZciJ9.eyJpc3MiOiJodHRwczovL3NwcmluZy1maW50ZWNoLnVzLmF1dGgwLmNvbS8iLCJzdWIiOiJSdTdHWkdxcXVqSkdvTjNrSlhvYTBEQ3ZSYTdxd1ljcEBjbGllbnRzIiwiYXVkIjoiaHR0cHM6Ly93YWxsZXQtYXBpIiwiaWF0IjoxNzU4NzE3Mjg2LCJleHAiOjE3NTg4MDM2ODYsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyIsImF6cCI6IlJ1N0daR3FxdWpKR29OM2tKWG9hMERDdlJhN3F3WWNwIiwicGVybWlzc2lvbnMiOltdfQ.ykzgMSsegzVUj-fRS4Vht4vnWyI_9zQMOz6oostAmsZBwp1YHlui9jrCXnzyy5u1vk4f0AQfvo6ZHwkwWWycw3T1vcWiv-0kqFyrDTOgKjr871oliRA_6_2taXc7WhrTSN49J5chMwtJ7qB_MOg1QcL8_2mwQjHXQcN2yuIh8faYB2zK96OMe5xPej_PBTxm3ruEePzG0_N4STSEoRUEzm0YpVUrfEzDhD9BSCtxNDQLJp0ZtSVVvM3VqBA0h4cEckAMGJgrhBbexMq1m6ycPzaSFiJXINwr1D6haNWIshSv4_ddxIJZfyEqPfz3YWMj-pqfeU5kekvNE9Tw40kPjA";
    @BeforeAll
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1";
    }
    
 
    void registerUser_success() {
    	String requestBody = """
    			{
    			  "email": "john.doe@example.com",
    			  "password": "john@123"
    			}
    			""";
    	given()
    	    .contentType(ContentType.JSON)
    	    .body(requestBody)
        .when()
            .post("users/register")
        .then()
            .statusCode(201)
            .body("email", equalTo("john.doe@example.com"))
            .body("role", equalTo("USER"));
     
            
    }
    
 
    void registerUser_invalidInput() {
    	String requestBody = """
    			{
    			  "email": "john.doe@example.com"
    			}
    			""";
    	given()
    		 .contentType(ContentType.JSON)
    		 .body(requestBody)
    	.when()
    		 .post("users/register")
    	.then()
    		 .statusCode(400)
    		 .body("code", equalTo("BAD_REQUEST"))
    		 .body("message", containsString("Password is required"));
    }
    
    void registerUser_preventDuplicateRegistration() {
    	String requestBody = """
    			{
    			  "email": "charlie.herper@example.com",
    			  "password": "charlie#123"
    			}
    			""";
    	// First Registration
    	given()
    		.contentType(ContentType.JSON)
    		.body(requestBody)
    	.when()
    		.post("users/register")
    	.then()
    		.statusCode(201)
    		.body("email", equalTo("charlie.herper@example.com"))
            .body("role", equalTo("USER"));
    	// Duplicate registration
    	given()
    		.contentType(ContentType.JSON)
    		.body(requestBody)
    	.when()
    		.post("users/register")
    	.then()
    		.statusCode(409)
    		.body("code", equalTo("USER_ALREADY_EXISTS"))
    		.body("message", containsString("Email already exists: charlie.herper@example.com"));
    }
    
    
    void getWallet_success() {
    	// First register a user
    	String userId =
    			given()
    				.contentType(ContentType.JSON)
    				.body("{ \"email\": \"carol@example.com\", \"password\": \"carol123\" }")
    			.when()
    				.post("users/register")
    			.then()
    				.statusCode(201)
    				.extract()
    				.path("id");
    	// Fetch wallet
    	given()
    		.auth().oauth2(accessToken)
    		.pathParam("id", userId)
    	.when()
    		.get("/users/{id}/wallet")
    	.then()
    		.statusCode(200)
    		.body("balance", equalTo(0.0F))
    		.body("currency", equalTo("INR"));
    }
    
 
    void getWallet_walletNotFound() {
    	given()
    		.auth().oauth2(accessToken)
    		.pathParam("id", "non-existent-id")
    	.when()
    		.get("users/{id}/wallet")
    	.then()
    		.statusCode(404)
    		.body("code", equalTo("WALLET_NOT_FOUND"))
    		.body("message", containsString("Wallet not found"));
    }
    
}
