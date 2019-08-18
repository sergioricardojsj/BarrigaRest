package br.sergior.rest.core;

import io.restassured.http.ContentType;

public interface Constantes {

	String BASE_URL = "http://barrigarest.wcaquino.me";
	Integer HTTP_PORT = 80;
	String BASE_PATH = "";
	
	ContentType CONTENT_TYPE = ContentType.JSON;
	Long MAX_TIMEOUT = 5000L;
	
}
