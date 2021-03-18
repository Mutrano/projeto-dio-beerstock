package com.mutrano.beerstock.services;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import com.mutrano.beerstock.builders.BeerDTOBuilder;
import com.mutrano.beerstock.builders.QuantityDTOBuilder;
import com.mutrano.beerstock.dto.BeerDTO;
import com.mutrano.beerstock.dto.QuantityDTO;
import com.mutrano.beerstock.entities.Beer;
import com.mutrano.beerstock.entities.exceptions.BeerStockExceededException;
import com.mutrano.beerstock.repositories.BeerRepository;
import com.mutrano.beerstock.services.exceptions.BeerAlreadyRegisteredException;
import com.mutrano.beerstock.services.exceptions.ResourceNotFoundException;

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
		assertThrows(BeerAlreadyRegisteredException.class, ()->beerService.insertBeer(createdBeerDTO));
	}
	@Test
	void whenBeerNameIsInformedShouldReturnTheBeer() throws ResourceNotFoundException {
		//given
		BeerDTO foundBeerDTO = BeerDTOBuilder.build();
		Beer foundBeer = beerService.fromDTO(foundBeerDTO);
		String name = "Brahma puro malte";
		//when
		when(beerRepository.findByName(name)).thenReturn(Optional.of(foundBeer));
		//then
		BeerDTO returnedBeerDTO =  beerService.findByName(name);
		
		assertThat(returnedBeerDTO, is(equalTo(foundBeerDTO)));
	}
	@Test
	void whenNotRegisteredBeerNameIsInformedAnExceptionShouldBeThrown() throws ResourceNotFoundException {
		//given
		BeerDTO foundBeerDTO = BeerDTOBuilder.build();
		//when
		when(beerRepository.findByName(foundBeerDTO.getName())).thenReturn(Optional.empty());
		//then
		assertThrows(ResourceNotFoundException.class, () -> beerService.findByName(foundBeerDTO.getName()));
	}

	@Test
	void whenListBeerIsCalledThenReturnAListOfBeers() {
		//given
		BeerDTO foundBeerDTO = BeerDTOBuilder.build();
		Beer foundBeer = beerService.fromDTO(foundBeerDTO);

		//when
		when(beerRepository.findAll()).thenReturn(Collections.singletonList(foundBeer));
		//then
		List<BeerDTO> foundListBeers = beerService.findAll();
		
		assertThat(foundListBeers, is(not(empty())));
		assertThat(foundListBeers.get(0),is(equalTo(foundBeerDTO)));
	}
	@Test
	void whenListBeerisCalledThenReturnAnEmptyListOfBeers() {
		//given
		
		//when
		when(beerRepository.findAll()).thenReturn(Collections.emptyList());
		//then
		List<BeerDTO> foundListBeers = beerService.findAll();
		assertThat(foundListBeers,is(empty()));
	}
	@Test
	void whenBeerDeleteIsCalledThenABeerShouldBeDeleted() throws ResourceNotFoundException {
		//given
		BeerDTO beerDTO = BeerDTOBuilder.build();
		//when
		doNothing().when(beerRepository).deleteById(beerDTO.getId());
		//then
		beerService.delete(beerDTO.getId());
		verify(beerRepository,times(1)).deleteById(beerDTO.getId());
	}
	@Test
	void whenIncrementAfterSumIsLessThanMaxThenIncrementBeerStock() throws ResourceNotFoundException, BeerStockExceededException {
		//given
		BeerDTO incrementedBeerDTO = BeerDTOBuilder.build();
		Beer incrementedBeer = beerService.fromDTO(incrementedBeerDTO);
		QuantityDTO quantityDTO = QuantityDTOBuilder.build();
		quantityDTO.setQuantity(44);
		Beer mockedBeer = beerService.fromDTO(BeerDTOBuilder.build());
		mockedBeer.setQuantity(quantityDTO.getQuantity() + incrementedBeer.getQuantity());
		//when
		when(beerRepository.findById(quantityDTO.getId())).thenReturn(Optional.of(incrementedBeer));
		when(beerRepository.save(incrementedBeer)).thenReturn(mockedBeer);
		//then
		incrementedBeerDTO = beerService.incrementStock(quantityDTO.getId(), quantityDTO.getQuantity());
		assertThat(incrementedBeerDTO.getQuantity(), is(equalTo(mockedBeer.getQuantity()  )));
		
	}
	
	@Test
	void whenIncrementAfterSumIsEqualsToMaxThenIncrementBeerStock() throws ResourceNotFoundException, BeerStockExceededException {
		//given
		BeerDTO incrementedBeerDTO = BeerDTOBuilder.build();
		Beer incrementedBeer= beerService.fromDTO(incrementedBeerDTO);
		QuantityDTO quantityDTO = QuantityDTOBuilder.build();
		quantityDTO.setQuantity(45);
		Beer mockedBeer = beerService.fromDTO(BeerDTOBuilder.build());
		mockedBeer.setQuantity(quantityDTO.getQuantity() + incrementedBeer.getQuantity());
		//when
		when(beerRepository.findById(incrementedBeerDTO.getId())).thenReturn(Optional.of(incrementedBeer));
		when(beerRepository.save(incrementedBeer)).thenReturn(mockedBeer);
		//then
		incrementedBeerDTO= beerService.incrementStock(incrementedBeer.getId(), quantityDTO.getQuantity());
		assertThat(incrementedBeerDTO.getQuantity(),is(equalTo(mockedBeer.getQuantity() )));
	
	}
	@Test
	void whenIncrementAfterSumIsGreatherThanMaxThenThrowException() throws ResourceNotFoundException, BeerStockExceededException {
		//given
		BeerDTO incrementedBeerDTO = BeerDTOBuilder.build();
		Beer incrementedBeer= beerService.fromDTO(incrementedBeerDTO);
		QuantityDTO quantityDTO = QuantityDTOBuilder.build();
		quantityDTO.setQuantity(46);
		Beer mockedBeer = beerService.fromDTO(BeerDTOBuilder.build());
		mockedBeer.setQuantity(quantityDTO.getQuantity() + incrementedBeer.getQuantity());
		//when
		when(beerRepository.findById(incrementedBeerDTO.getId())).thenReturn(Optional.of(incrementedBeer));
		//then
		assertThrows(BeerStockExceededException.class, ()-> beerService.incrementStock(incrementedBeer.getId(), quantityDTO.getQuantity()));
	}
		
}
