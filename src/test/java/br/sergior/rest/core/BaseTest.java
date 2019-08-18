package br.sergior.rest.core;

import static org.hamcrest.Matchers.lessThan;

import org.junit.BeforeClass;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;

public class BaseTest implements Constantes {

	@BeforeClass
	public static void setUp() {
		RestAssured.baseURI = BASE_URL;
		//RestAssured.port = HTTP_PORT;
		//RestAssured.basePath = BASE_PATH;
		
		RequestSpecBuilder reqBuilder = new RequestSpecBuilder(); 
		reqBuilder.setContentType(CONTENT_TYPE);
		RestAssured.requestSpecification = reqBuilder.build();
		
		ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
		resBuilder.expectResponseTime(lessThan(MAX_TIMEOUT));
		RestAssured.responseSpecification = resBuilder.build();
		
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
	}
	
}
