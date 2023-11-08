package ru.netology.controller;

import java.util.LinkedHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ResponseBody;

import ru.netology.service.JwtUserDetailsService;
import ru.netology.config.JwtTokenUtil;
import ru.netology.entity.JwtRequest;
import ru.netology.entity.JwtResponse;
import ru.netology.entity.UserDTO;
import ru.netology.entity.FileEntity;
import ru.netology.service.FilesStorageService;
import ru.netology.exception.ExceptionRequestMessages;

@RestController
@CrossOrigin
public class CloudStorageController {

	@Autowired
	private static ExceptionRequestMessages exceptionRequestMessages;

	@Autowired
	private FilesStorageService storageService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	private String home;





	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getLogin(),
					authenticationRequest.getPassword()));

			String login = authenticationRequest.getLogin();
			final UserDetails userDetails = userDetailsService.loadUserByUsername(login);
			home = login;
			final String token = jwtTokenUtil.generateToken(userDetails);
			JwtResponse jwtResponse = new JwtResponse(token);

			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("auth-token", jwtResponse.getToken());
			responseHeaders.set("Content-Type", "application/json;charset=UTF-8");

			StringBuilder body = new StringBuilder();
			body.append("{");
			body.append("\"auth-token\"" + ":" + "\"" + jwtResponse + "\",");
			body.append("\"$ref\"" + ":" + "\"#/components/schemas/Login\",");
			body.append("\"email\"" + ":" + "\"" + login + "\"");
			body.append("}");

			return new ResponseEntity<String>(body.toString(), responseHeaders, HttpStatus.OK);
		} catch (BadCredentialsException e) {
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("Content-Type", "application/json;charset=UTF-8");
			StringBuilder body = new StringBuilder();
			body.append("{");
			body.append("\"$ref\"" + ":" + "\"#/components/schemas/Error\"");
			body.append("}");
			return new ResponseEntity(body.toString(), responseHeaders, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public ResponseEntity<?> logOut(@RequestHeader(value = "auth-token") String token) throws Exception {
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = "/file", method = RequestMethod.POST)
	public ResponseEntity<?> uploadFile(
			@RequestHeader(value = "auth-token") String token,
			// @RequestHeader(value = "home") String home,
			@RequestParam(value = "filename") String filename,
			@RequestBody MultipartFile file) {
		try {
			storageService.save(home, file);

			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("auth-token", token);
			responseHeaders.set("Content-Type", "multipart/form-data");
			StringBuilder body = new StringBuilder();
			body.append("{");
			body.append("\"$ref\"" + ":" + "\"#/components/schemas/File\"");
			body.append("}");
			return new ResponseEntity(body.toString(), responseHeaders, HttpStatus.OK);
		} catch (BadCredentialsException e) {
			return exceptionRequestMessages.getMessage(token, HttpStatus.BAD_REQUEST);
		} catch (Unauthorized e) {
			return exceptionRequestMessages.getMessage(token, HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/file", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteFile(
			@RequestHeader(value = "auth-token") String token,
			// @RequestHeader(value = "home") String home,
			@RequestParam(value = "filename") String filename) {
		try {
			storageService.delete(home, filename);

			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("auth-token", token);
			return ResponseEntity.ok().build();
		} catch (BadCredentialsException e) {
			return exceptionRequestMessages.getMessage(token, HttpStatus.BAD_REQUEST);
		} catch (Unauthorized e) {
			return exceptionRequestMessages.getMessage(token, HttpStatus.UNAUTHORIZED);
		} catch (InternalServerError e) {
			return exceptionRequestMessages.getMessage(token, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/file", method = RequestMethod.GET)
	public ResponseEntity<?> downloadFile(
			@RequestHeader(value = "auth-token") String token,
			// @RequestHeader(value = "home") String home,
			@RequestParam(value = "filename") String filename) {
		try {

			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.set("auth-token", token);
			responseHeaders.set("Content-Type", "multipart/form-data");

			Resource resource = storageService.load(home, filename);
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		} catch (BadCredentialsException e) {
			return exceptionRequestMessages.getMessage(token, HttpStatus.BAD_REQUEST);
		} catch (Unauthorized e) {
			return exceptionRequestMessages.getMessage(token, HttpStatus.UNAUTHORIZED);
		} catch (InternalServerError e) {
			return exceptionRequestMessages.getMessage(token, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/file", method = RequestMethod.PUT)
	public ResponseEntity<?> renameFile(
			@RequestHeader(value = "auth-token") String token,
			// @RequestHeader(value = "home") String home,
			@RequestParam(value = "filename") String filename,
			@RequestBody FileEntity name) {
		// @RequestBody LinkedHashMap name) {
		try {
			storageService.rename(home, filename, name.get("filename").toString());
			return ResponseEntity.ok().build();
		} catch (BadCredentialsException e) {
			return exceptionRequestMessages.getMessage(token, HttpStatus.BAD_REQUEST);
		} catch (Unauthorized e) {
			return exceptionRequestMessages.getMessage(token, HttpStatus.UNAUTHORIZED);
		} catch (InternalServerError e) {
			return exceptionRequestMessages.getMessage(token, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ResponseEntity<?> listFiles(@RequestHeader(value = "auth-token") String token,
			// @RequestHeader(value = "home") String home,
			@RequestParam(value = "limit") Integer limit) throws Exception {

		try {
			return storageService.list(home, token);
		} catch (BadCredentialsException e) {
			return exceptionRequestMessages.getMessage(token, HttpStatus.BAD_REQUEST);
		} catch (Unauthorized e) {
			return exceptionRequestMessages.getMessage(token, HttpStatus.UNAUTHORIZED);
		} catch (InternalServerError e) {
			return exceptionRequestMessages.getMessage(token, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {
		return ResponseEntity.ok(userDetailsService.save(user));
	}
}
