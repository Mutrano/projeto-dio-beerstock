package com.mutrano.beerstock.resources.exceptions;

import java.time.Instant;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mutrano.beerstock.entities.exceptions.BeerStockExceededException;
import com.mutrano.beerstock.services.exceptions.ResourceNotFoundException;

@ControllerAdvice
public class ResourceExceptionHandler  {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationError> validationException(MethodArgumentNotValidException e, HttpServletRequest request){
		String error = "Validation Error";
		HttpStatus status = HttpStatus.BAD_REQUEST;
		ValidationError err = new ValidationError(Instant.now(), status.value(),error, "Cheque novamente os dados de entrada", request.getRequestURI());
		e.getBindingResult().getFieldErrors()
			.stream()
			.forEach((x)-> err.addError(x.getField(),x.getDefaultMessage()));
		
		return ResponseEntity.status(status).body(err);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<StandardError> resourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request){
		String error = "Resource not Found";
		HttpStatus status = HttpStatus.NOT_FOUND;
		StandardError err = new StandardError(Instant.now(), status.value(), error, "Tente procurar por algo diferente", request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}	
	@ExceptionHandler(BeerStockExceededException.class)
	public ResponseEntity<StandardError> beerStockExceededException(BeerStockExceededException expn, HttpServletRequest request){
		String[] requestPath= request.getRequestURI().split("/");
		String method = requestPath[requestPath.length-1];
		String error;
		String message;
		if(method.equalsIgnoreCase("increment")) {
			error="Increment Exceeded Beer Stock of "+expn.getMessage();
			message="Try a lower value";
		}
		else {
			error="Decrement Exceeded Beer Stock of "+expn.getMessage();
			message="Try a bigger value";
		}
		HttpStatus status = HttpStatus.BAD_REQUEST;
		StandardError err = new StandardError(Instant.now(), status.value(), error,message, request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
}
