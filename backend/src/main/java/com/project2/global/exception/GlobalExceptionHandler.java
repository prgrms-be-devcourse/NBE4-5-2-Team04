package com.project2.global.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.project2.global.dto.RsData;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<RsData<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

		String message = e.getBindingResult().getFieldErrors()
			.stream()
			.map(fe -> fe.getField() + " : " + fe.getCode() + " : " + fe.getDefaultMessage())
			.sorted()
			.collect(Collectors.joining("\n"));

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(
				new RsData<>(
					String.valueOf(HttpStatus.BAD_REQUEST.value()),
					message
				)
			);
	}

	@ResponseStatus
	@ExceptionHandler(ServiceException.class)
	public ResponseEntity<RsData<Void>> ServiceExceptionHandle(ServiceException ex) {

		return ResponseEntity
			.status(ex.getStatusCode())
			.body(
				new RsData<>(
					ex.getCode(),
					ex.getMsg()
				)
			);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	@ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
	public ResponseEntity<RsData<Void>> handleMaxSizeException(MaxUploadSizeExceededException ex) {

		return ResponseEntity
			.status(ex.getStatusCode())
			.body(
				new RsData<>(String.valueOf(HttpStatus.PAYLOAD_TOO_LARGE.value()),
					"파일 크기가 너무 큽니다. 각 파일은 최대 10MB, 총 50MB까지 업로드 가능합니다.")
			);
	}

}
