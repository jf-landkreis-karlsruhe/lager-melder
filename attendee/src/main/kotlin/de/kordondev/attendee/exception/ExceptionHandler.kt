package de.kordondev.attendee.exception

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ExistingDependencyException::class, ResourceAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleExistingDependencyException() {
    }

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessDeniedException(ex: AccessDeniedException, request: WebRequest) {
    }

    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequestException(ex: BadRequestException) {
        logger.error("bad request exception $ex")
        throw ex
    }

    @ExceptionHandler(UniqueException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleUniqueException() {
    }

    data class ValidationError(val message: String?, val fieldName: String)

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val body: List<ValidationError> = ex.bindingResult
            .fieldErrors.mapNotNull { ValidationError(it.defaultMessage, it.field) }
        logger.error("Validation error $body")
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

}
