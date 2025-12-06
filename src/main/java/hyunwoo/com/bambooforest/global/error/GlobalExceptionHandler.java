package hyunwoo.com.bambooforest.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import reactor.core.publisher.Mono;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodNotAllowedException.class)
	protected Mono<ResponseEntity<ErrorResponse>> handle(MethodNotAllowedException e) {
		logError(ErrorCode.METHOD_NOT_ALLOWED);

		return Mono.just(
			createErrorResponseEntity(ErrorCode.METHOD_NOT_ALLOWED)
		);
	}

	@ExceptionHandler(WebExchangeBindException.class)
	protected Mono<ResponseEntity<ErrorResponse>> handle(WebExchangeBindException e) {
		String errorMessage = e.getFieldError().getDefaultMessage();
		logError(errorMessage);

		return Mono.just(
			createErrorResponseEntity(ErrorCode.INVALID_INPUT_VALUE, errorMessage)
		);
	}

	@ExceptionHandler(BusinessBaseException.class)
	protected Mono<ResponseEntity<ErrorResponse>> handle(BusinessBaseException e) {
		logError(e.getMessage());

		return Mono.just(
			createErrorResponseEntity(e.errorCode)
		);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<ErrorResponse> handle(Exception e) {
		logError(e.getMessage());

		return createErrorResponseEntity(ErrorCode.INTERNAL_SERVER_ERROR);
	}

	private void logError(ErrorCode errorCode) {
		log.error("[ERROR] : { errorMessage : {} }", errorCode.message);
	}

	private void logError(String errorMessage) {
		log.error("[ERROR] : { errorMessage : {} }", errorMessage);
	}

	private ResponseEntity<ErrorResponse> createErrorResponseEntity(ErrorCode errorCode) {
		return new ResponseEntity<>(
			ErrorResponse.of(errorCode),
			errorCode.status
		);
	}

	private ResponseEntity<ErrorResponse> createErrorResponseEntity(ErrorCode errorCode, String message) {
		return new ResponseEntity<>(
			ErrorResponse.of(message),
			errorCode.status
		);
	}
}
