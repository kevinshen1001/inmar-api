package com.inmar.metadata.config

import com.inmar.metadata.dto.ErrorResponse
import com.inmar.metadata.service.BadRequestException
import com.inmar.metadata.service.ConflictException
import com.inmar.metadata.service.NotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException, req: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.warn("Not found: {}", ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(error(HttpStatus.NOT_FOUND, "Not Found", ex.message ?: "", req))
    }

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(ex: ConflictException, req: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.warn("Conflict: {}", ex.message)
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(error(HttpStatus.CONFLICT, "Conflict", ex.message ?: "", req))
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException, req: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(error(HttpStatus.BAD_REQUEST, "Bad Request", ex.message ?: "", req))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException, req: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val msg = ex.bindingResult.allErrors
            .filterIsInstance<FieldError>()
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        log.warn("Validation error: {}", msg)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(error(HttpStatus.BAD_REQUEST, "Validation Failed", msg, req))
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(ex: BadCredentialsException, req: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.warn("Bad credentials for path={}", req.requestURI)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(error(HttpStatus.UNAUTHORIZED, "Unauthorized", "Invalid username or password", req))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception, req: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error("Unhandled exception at path={}", req.requestURI, ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred", req))
    }

    private fun error(status: HttpStatus, error: String, message: String, req: HttpServletRequest) = ErrorResponse(
        status = status.value(),
        error = error,
        message = message,
        path = req.requestURI,
        traceId = MDC.get("traceId")
    )
}
