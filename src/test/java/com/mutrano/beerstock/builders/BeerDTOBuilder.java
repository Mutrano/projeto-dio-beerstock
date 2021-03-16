package com.mutrano.beerstock.builders;

import com.mutrano.beerstock.dto.BeerDTO;
import com.mutrano.beerstock.enums.BeerType;

public class BeerDTOBuilder {
	public static BeerDTO build() {
		return new BeerDTO(1,"Brahma puro malte","Brahma", 50, 5, BeerType.LAGER);
	}
}
