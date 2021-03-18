package com.mutrano.beerstock.entities.exceptions;

public class BeerStockExceededException extends Exception {
	private static final long serialVersionUID = 1L;

	public BeerStockExceededException(String message) {
		super(message);
	}
}
