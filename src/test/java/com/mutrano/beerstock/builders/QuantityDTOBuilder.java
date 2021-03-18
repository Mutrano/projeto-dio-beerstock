package com.mutrano.beerstock.builders;

import com.mutrano.beerstock.dto.QuantityDTO;

public class QuantityDTOBuilder {
	public static QuantityDTO build() {
		return new QuantityDTO(1,10);
	}
}
