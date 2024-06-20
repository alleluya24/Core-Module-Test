package com.testing.core.controller;

import com.testing.core.dto.ObjectResponseDto;
import io.minio.messages.Item;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rw.eccellenza.core.objectstorage.service.IObjectStorage;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/object")
public class ObjectStorageController {

  private final IObjectStorage objectStorage;

  @PostMapping("/uploadFile")
  public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
    // Check if the file is not empty
    if (file.isEmpty()) {
      return new ResponseEntity<>("Please select a file to upload", HttpStatus.BAD_REQUEST);
    }
    try {
      Path path = Path.of(Objects.requireNonNull(file.getOriginalFilename()));
      objectStorage.save(path, file.getInputStream());
      return new ResponseEntity<>(
          "Successfully uploaded: " + file.getOriginalFilename(), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Failed to upload the file", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/uploadFileWithPath")
  public ResponseEntity<String> handleFileUploadWithPath(
      @RequestParam("file") MultipartFile file, @RequestParam String filePath) {
    // Check if the file is not empty
    if (file.isEmpty()) {
      return new ResponseEntity<>("Please select a file to upload", HttpStatus.BAD_REQUEST);
    }
    try {
      Path path = Path.of(filePath + Objects.requireNonNull(file.getOriginalFilename()));
      objectStorage.save(path, file.getInputStream());
      return new ResponseEntity<>(
          "Successfully uploaded: " + file.getOriginalFilename(), HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>("Failed to upload the file", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * get file url
   *
   * @param source
   * @return
   */
  @PostMapping("/uploadImage")
  public void uploadImage(
      @RequestParam("source") String source, @RequestParam("base64Image") String base64Image) {
    Path path = Path.of(Objects.requireNonNull(source));
    objectStorage.uploadImage(base64Image, path);
  }

  @GetMapping("/getFile")
  public ResponseEntity<String> getObject(
      @RequestParam("object") String object, HttpServletResponse response) throws IOException {
    try {
      InputStream inputStream = objectStorage.getObject(Path.of(object));

      // Set the content type and attachment header.
      response.addHeader("Content-disposition", "attachment;filename=" + object);
      response.setContentType(URLConnection.guessContentTypeFromName(object));

      // Copy the stream to the response's output stream.
      IOUtils.copy(inputStream, response.getOutputStream());
      response.flushBuffer();

    } catch (Exception e) {
      log.error("Failed to get Object");
    }
    return new ResponseEntity<>("Object not Found", HttpStatus.NOT_FOUND);
  }

  @DeleteMapping("/deleteFile")
  public ResponseEntity<String> deleteFile(@RequestParam("source") String file) {
    Path path = Path.of(Objects.requireNonNull(file));
    objectStorage.remove(path);
    return new ResponseEntity<>("Object deleted", HttpStatus.OK);
  }

  @GetMapping("/info")
  public String getFileStatusInfo(@RequestParam("fileName") String fileName) {
    Path path = Path.of(Objects.requireNonNull(fileName));
    return objectStorage.getMetadata(path).headers().toString();
  }

  /**
   * get file url
   *
   * @param fileName
   * @return
   */
  @GetMapping("/url")
  public String getPreSignedObjectUrl(@RequestParam("fileName") String fileName) {
    Path path = Path.of(Objects.requireNonNull(fileName));
    return objectStorage.getPreSignedObjectUrl(path);
  }

  /**
   * get file url
   *
   * @param fileName
   * @return
   */
  @GetMapping("/urlWithSeconds")
  public String getPreSignedObjectUrlWithTimeInSeconds(
      @RequestParam("fileName") String fileName, @RequestParam("time") Integer timeInSeconds) {
    Path path = Path.of(Objects.requireNonNull(fileName));
    return objectStorage.getPreSignedObjectUrl(path, timeInSeconds);
  }

  /**
   * file download
   *
   * @param fileName
   * @param response
   */
  @GetMapping("/download")
  public void download(@RequestParam("fileName") String fileName, HttpServletResponse response) {
    try {
      Path path = Path.of(Objects.requireNonNull(fileName));
      InputStream fileInputStream = objectStorage.getObject(path);
      response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
      response.setContentType("application/force-download");
      response.setCharacterEncoding("UTF-8");
      IOUtils.copy(fileInputStream, response.getOutputStream());
    } catch (Exception e) {
      log.error("download fail");
    }
  }

  @GetMapping("/objectExists")
  public ResponseEntity<String> objectExists(@RequestParam("source") String file) {
    try {
      Path path = Path.of(Objects.requireNonNull(file));
      if (objectStorage.objectExist(path)) {
        return new ResponseEntity<>("Object Exists", HttpStatus.OK);
      } else {
        return new ResponseEntity<>("Object Does not exists", HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return new ResponseEntity<>("Object Does not exists", HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/getObjectInChunks")
  public ResponseEntity<String> getObject(
      @RequestParam("object") String object,
      @RequestParam("offset") Long offset,
      @RequestParam("length") Long length,
      HttpServletResponse response)
      throws IOException {
    try {
      InputStream inputStream = objectStorage.getInputStream(Path.of(object), offset, length);

      // Set the content type and attachment header.
      response.addHeader("Content-disposition", "attachment;filename=" + object);
      response.setContentType(URLConnection.guessContentTypeFromName(object));

      // Copy the stream to the response's output stream.
      IOUtils.copy(inputStream, response.getOutputStream());
      response.flushBuffer();

    } catch (Exception e) {
      log.error("Can not get Object in chunks");
    }
    return new ResponseEntity<>("Can not get Object in chunks", HttpStatus.NOT_FOUND);
  }

  @GetMapping("/listFiles/prefix")
  public ResponseEntity<List<ObjectResponseDto>> getFullListByPrefix(
      @RequestParam("fileName") String fileName) {
    Path path = Path.of(Objects.requireNonNull(fileName));
    List<Item> results = objectStorage.getFullList(path);
    List<ObjectResponseDto> response = new ArrayList<>();
    // for each item in the results, print the object name
    for (Item item : results) {
      ObjectResponseDto objectResponseDto =
          new ObjectResponseDto(
              item.objectName(),
              String.valueOf(item.size()),
              item.isDir(),
              item.isDir() ? "" : item.lastModified().toString());
      response.add(objectResponseDto);
    }
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/listFiles")
  public ResponseEntity<List<ObjectResponseDto>> getFullListByPrefix() {
    List<Item> results = objectStorage.list();
    List<ObjectResponseDto> response = new ArrayList<>();
    // for each item in the results, print the object name
    for (Item item : results) {
      ObjectResponseDto objectResponseDto =
          new ObjectResponseDto(
              item.objectName(),
              String.valueOf(item.size()),
              item.isDir(),
              item.isDir() ? "" : item.lastModified().toString());
      response.add(objectResponseDto);
    }
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
