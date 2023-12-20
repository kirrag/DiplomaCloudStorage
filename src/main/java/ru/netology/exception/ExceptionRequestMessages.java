package ru.netology.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ExceptionRequestMessages {
	public ResponseEntity<?> getMessage(String token, HttpStatus httpStatus) {
		//String ref = "#/components/schemas/Error";
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("auth-token", token);
		responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
		StringBuilder body = new StringBuilder();
		/*
		body.append("{");
		body.append("\"$ref\"" + ":" + "\"" + ref + "\"");
		body.append("}");
		*/
		return new ResponseEntity(body.toString(), responseHeaders, httpStatus);
	}
}
