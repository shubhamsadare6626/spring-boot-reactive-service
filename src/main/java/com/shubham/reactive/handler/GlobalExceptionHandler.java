package com.shubham.reactive.handler;

import com.shubham.reactive.dtos.ResponseError;
import java.util.Calendar;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NotFoundException.class)
  public Mono<ResponseEntity<ResponseError>> handleNotFoundException(
      NotFoundException ex, ServerWebExchange exchange) {
    log.error(String.format("handled NotFoundException %s", ex.getMessage()));
    return Mono.just(
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ResponseError.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .error(ex.getMessage())
                    .timestamp(Calendar.getInstance().getTime())
                    .path(exchange.getRequest().getPath().value())
                    .build()));
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(BusinessException.class)
  public Mono<ResponseEntity<ResponseError>> handlebusinessException(
      BusinessException ex, ServerWebExchange request) {
    log.error("handled BusinessException");
    return Mono.just(
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ResponseError.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error(ex.getMessage())
                    .timestamp(Calendar.getInstance().getTime())
                    .path(request.getRequest().getPath().value())
                    .build()));
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(RuntimeException.class)
  public Mono<ResponseEntity<ResponseError>> handleRuntimeException(
      RuntimeException ex, ServerWebExchange exchange) {
    log.error("handled RuntimeException");
    return Mono.just(
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ResponseError.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error(ex.getMessage())
                    .timestamp(Calendar.getInstance().getTime())
                    .path(exchange.getRequest().getPath().value())
                    .build()));
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<ResponseError>> handleGenericException(
      Exception ex, ServerWebExchange exchange) {
    log.error(String.format("handled GlobalException %s", ex.getMessage()));

    return Mono.just(
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ResponseError.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .error(ex.getMessage())
                    .timestamp(Calendar.getInstance().getTime())
                    .path(exchange.getRequest().getPath().value())
                    .build()));
  }
}
