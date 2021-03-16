package com.mutrano.beerstock.services;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.mutrano.beerstock.builders.BeerDTOBuilder;
import com.mutrano.beerstock.dto.BeerDTO;
import com.mutrano.beerstock.entities.Beer;
import com.mutrano.beerstock.repositories.BeerRepository;
import com.mutrano.beerstock.services.exceptions.BeerAlreadyRegisteredException;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class BeerServiceTest {

	@Mock
	BeerRepository beerRepository;
	
	@InjectMocks
	BeerService beerService;

	
	@Test
	void whenBeerIsInformedShouldCreateBeer() throws BeerAlreadyRegisteredException {
		//given
		BeerDTO createdBeerDTO = BeerDTOBuilder.build();
		createdBeerDTO.setId(null);
		Beer createdBeer = beerService.fromDTO(createdBeerDTO);
		BeerDTO expectedBeerDTO = BeerDTOBuilder.build();
		Beer expectedBeer = beerService.fromDTO(expectedBeerDTO);
		
		//when
		when(beerRepository.save(createdBeer)).thenReturn(expectedBeer);
		when(beerRepository.findByName(expectedBeer.getName())).thenReturn(Optional.empty());
		//then

		createdBeerDTO = beerService.insertBeer(createdBeerDTO);
		
		assertThat(expectedBeerDTO.getId(),is(equalTo(createdBeerDTO.getId())));
		assertThat(expectedBeerDTO.getName(),is(equalTo(createdBeerDTO.getName())));
		assertThat(expectedBeerDTO.getBrand(),is(equalTo(createdBeerDTO.getBrand())));
	}
	@Test
	void whenBeerInformedIsAlreadyRegisteredAnExceptionShouldBeThrown() {
		//given
		BeerDTO createdBeerDTO = BeerDTOBuilder.build();
		Beer createdBeer = beerService.fromDTO(createdBeerDTO);
		BeerDTO expectedBeerDTO = BeerDTOBuilder.build();
		Beer expectedBeer = beerService.fromDTO(expectedBeerDTO);
		//when
		when(beerRepository.findByName(expectedBeer.getName())).thenReturn(Optional.of(createdBeer));
		//then
		Assertions.assertThrows(BeerAlreadyRegisteredException.class, ()->beerService.insertBeer(createdBeerDTO));
	}

}
