package com.example.authserviceQuery.filestorage.services;

import com.example.authserviceQuery.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class FileStorageService {

	private final Path fileStorageLocation;

	@Autowired
	public FileStorageService(final FileStorageProperties fileStorageProperties) {
		this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

		try {
			Files.createDirectories(fileStorageLocation);
		} catch (final Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
					ex);
		}
	}

	public String storeFile(final String prefix, final MultipartFile file) {
		final String fileName = prefix.replace("/", "_") + "_" + determineFileName(file);

		try {
			// Ensure the target directory exists
			Path targetLocation = fileStorageLocation.resolve(prefix.replace("/", "_"));
			Files.createDirectories(targetLocation);

			// Resolve the complete file path
			Path destinationPath = targetLocation.resolve(fileName);
			Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

			return fileName;
		} catch (final IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
	}

	private String determineFileName(final MultipartFile file) {
		// Normalize file name
		final String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		// Check if the file's name contains invalid characters
		if (fileName.contains("..")) {
			throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
		}
		return fileName;
	}

	public Optional<String> getExtension(final String filename) {
		return Optional.ofNullable(filename).filter(f -> f.contains("."))
				.map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}

	public Resource loadFileAsResource(final String fileName) {
		try {
			final Path filePath = fileStorageLocation.resolve(fileName).normalize();
			final Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			}
			throw new NotFoundException("File not found " + fileName);
		} catch (final MalformedURLException ex) {
			throw new NotFoundException("File not found " + fileName, ex);
		}
	}
}
