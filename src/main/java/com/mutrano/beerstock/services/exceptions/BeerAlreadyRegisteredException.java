package com.mutrano.beerstock.services.exceptions;

public class BeerAlreadyRegisteredException extends Exception{
	private static final long serialVersionUID = 1L;

	public BeerAlreadyRegisteredException(String message) {
		super(message);
	}
}
