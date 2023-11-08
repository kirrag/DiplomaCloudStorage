package ru.netology.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FilesStorageServiceImpl implements FilesStorageService {

	String storage = "storage";
	
	@Override
	public void save(String home, MultipartFile file) {
		try {
			Path homeDir = Paths.get(storage + "/" + home);
			Files.copy(file.getInputStream(), homeDir.resolve(file.getOriginalFilename()));
		} catch (Exception e) {
			if (e instanceof FileAlreadyExistsException) {
				throw new RuntimeException("A file of that name already exists.");
			}
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void delete(String home, String filename) {
		try {
			Path homeDir = Paths.get(storage + "/" + home);
			Path file = homeDir.resolve(filename);
			Files.delete(file);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public Resource load(String home, String filename) {
		try {

			Path homeDir = Paths.get(storage + "/" + home);
			Path file = homeDir.resolve(filename).normalize();
			Resource resource = new UrlResource(file.toUri());

			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new RuntimeException("Could not read the file!");
			}
		} catch (MalformedURLException e) {
			throw new RuntimeException("Error: " + e.getMessage());
		}
	}

	@Override
	public void rename(String home, String filename, String newfilename) {
		try {
			Path homeDir = Paths.get(storage + "/" + home);
			Path fileToMovePath = Paths.get(homeDir + "/" + filename);
			Path targetPath = Paths.get(homeDir + "/" + newfilename);
			Files.move(fileToMovePath, targetPath);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ResponseEntity<?> list(String home, String token) {
		Path homeDir = Paths.get(storage + "/" + home);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("auth-token", token);
		responseHeaders.set("Content-Type", "application/json;charset=UTF-8");

		StringBuilder body = new StringBuilder();

		body.append("[");
		if (homeDir.toFile().exists() && homeDir.toFile().isDirectory()) {

			String delim = "";
			for (File item : homeDir.toFile().listFiles()) {
				body.append(delim);
				body.append("{");
				body.append("\"filename\"" + ":" + "\"" + item.getName() + "\",");
				body.append("\"size\"" + ":" + "\"" + item.length() + "\"");
				body.append("}");
				delim = ",";
			}
		}
		body.append("]");
		return new ResponseEntity(body.toString(), responseHeaders, HttpStatus.OK);
	}

	@Override
	public void createHome(String newUser) {
		try {
			File newHomeDir = new File(storage, newUser);
			if(newHomeDir.exists()) {
				newHomeDir.delete();
			} else {
				newHomeDir.mkdir();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
