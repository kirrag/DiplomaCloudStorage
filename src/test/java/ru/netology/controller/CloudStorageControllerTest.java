package ru.netology.controller;

//import java.nio.file.Paths;
//import java.util.stream.Stream;

//import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;

//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;

//import org.mockito.exceptions.base.MockitoException;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.web.server.LocalServerPort;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import static org.hamcrest.Matchers.containsString;

//import static org.mockito.BDDMockito.given;
//import static org.mockito.BDDMockito.then;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import java.net.URI;

//import ru.netology.exception.FileUploadExceptionAdvice;
import ru.netology.service.FilesStorageService;
//import ru.netology.service.FilesStorageServiceImpl;

@AutoConfigureMockMvc
// @ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CloudStorageControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private FilesStorageService storageService;

	@InjectMocks
	private CloudStorageController storageController;

	@Test
	public void shouldSaveUploadedFile() throws Exception {

		MockMultipartFile multipartFile = new MockMultipartFile(
				"file",
				"test.txt",
				"text/plain",
				"Spring Framework".getBytes());

		MockMultipartHttpServletRequestBuilder multipartRequest = MockMvcRequestBuilders.multipart("/file");

		this.mockMvc.perform(multipartRequest.file(multipartFile)).andDo(print()).andExpect(status().isOk());

	}

}
