package de.kordondev.attendee.exception

import de.kordondev.attendee.exception.ErrorConstants.ACCESS_DENIED_ERROR
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ExistingDependencyException::class)
    fun handleExistingDependencyException(ex: ExistingDependencyException): ResponseEntity<ErrorResponse> {
        return exceptionToBody(ex, HttpStatus.FORBIDDEN, ex.key)
    }

    @ExceptionHandler(ResourceAlreadyExistsException::class)
    fun handleResourceAlreadyExistsException(ex: ResourceAlreadyExistsException): ResponseEntity<ErrorResponse> {
        return exceptionToBody(ex, HttpStatus.FORBIDDEN, ex.key)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<ErrorResponse> {
        return exceptionToBody(ex, HttpStatus.FORBIDDEN, ACCESS_DENIED_ERROR)
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(ex: BadRequestException): ResponseEntity<ErrorResponse> {
        return exceptionToBody(ex, HttpStatus.BAD_REQUEST, ex.key)
    }

    @ExceptionHandler(NotDeletableException::class)
    fun handleNotDeletableException(ex: NotDeletableException): ResponseEntity<ErrorResponse> {
        return exceptionToBody(ex, HttpStatus.BAD_REQUEST, ex.key)
    }

    @ExceptionHandler(UniqueException::class)
    fun handleUniqueException(ex: UniqueException): ResponseEntity<ErrorResponse> {
        return exceptionToBody(ex, HttpStatus.BAD_REQUEST, ex.key)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: NotFoundException): ResponseEntity<ErrorResponse> {
        return exceptionToBody(ex, HttpStatus.NOT_FOUND, ex.key)
    }

    @ExceptionHandler(WrongTimeException::class)
    fun handleWrongTimeException(ex: WrongTimeException): ResponseEntity<ErrorResponse> {
        return exceptionToBody(ex, HttpStatus.BAD_REQUEST, ex.key)
    }

    data class ErrorResponse(val key: String, val messages: List<ErrorMessage>)
    data class ErrorMessage(val message: String?, val fieldName: String?)

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val messages: List<ErrorMessage> = ex.bindingResult
            .fieldErrors.mapNotNull { ErrorMessage(it.defaultMessage, it.field) }
        return exceptionResponse(
            messages,
            HttpStatus.BAD_REQUEST,
            ErrorConstants.VALIDATION_ERROR
        ) as ResponseEntity<Any>
    }

    fun exceptionToBody(ex: RuntimeException, status: HttpStatus, key: String): ResponseEntity<ErrorResponse> {
        return exceptionResponse(listOf(ErrorMessage(ex.message, null)), status, key);
    }

    fun exceptionResponse(message: List<ErrorMessage>, status: HttpStatus, key: String): ResponseEntity<ErrorResponse> {
        logger.error("$status - $key: $message")
        return ResponseEntity(ErrorResponse(key, message), status)
    }
}
