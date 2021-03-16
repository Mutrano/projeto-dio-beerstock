package com.mutrano.beerstock.enums;

public enum BeerType {
	LAGER("Lager"),
	MALZBIER("Malzbier"),
	WITBIER("Witbier"),
	WEISS("Weiss"),
	ALE("Ale"),
	IPA("Ipa"),
	STOUT("Stout");
	
	private final String description;

	private BeerType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
}
