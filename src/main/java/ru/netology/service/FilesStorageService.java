package ru.netology.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FilesStorageService {
	
	public void save(String home, MultipartFile file);

	public void delete(String home, String filename);

	public Resource load(String home, String filename);

	public void rename(String home, String filename, String newfilename);

	public ResponseEntity<?> list(String home, String token);

	public void createHome(String newUser);

}
