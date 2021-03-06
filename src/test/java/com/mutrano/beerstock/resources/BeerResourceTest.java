package com.mutrano.beerstock.resources;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.mutrano.beerstock.builders.BeerDTOBuilder;
import com.mutrano.beerstock.builders.QuantityDTOBuilder;
import com.mutrano.beerstock.dto.BeerDTO;
import com.mutrano.beerstock.dto.QuantityDTO;
import com.mutrano.beerstock.entities.exceptions.BeerStockExceededException;
import com.mutrano.beerstock.repositories.BeerRepository;
import com.mutrano.beerstock.resources.exceptions.ResourceExceptionHandler;
import com.mutrano.beerstock.services.BeerService;
import com.mutrano.beerstock.services.exceptions.ResourceNotFoundException;
import com.mutrano.beerstock.utils.JsonConvertion;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class BeerResourceTest {

	private MockMvc mockMvc;

	@Mock
	private BeerService beerService;

	@Mock
	private BeerRepository beerRepository;

	@InjectMocks
	private BeerResource beerResource;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(beerResource)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.setViewResolvers((s, locale) -> new MappingJackson2JsonView())
				.setControllerAdvice(new ResourceExceptionHandler()).build();
	}

	@Test
	void whenValidBeerIsInformedItShouldBeCreated() throws Exception {
		// given
		BeerDTO beerDTO = BeerDTOBuilder.build();
		// when
		when(beerService.insertBeer(beerDTO)).thenReturn(beerDTO);
		// then

		mockMvc.perform(MockMvcRequestBuilders.post("/Beers").contentType(MediaType.APPLICATION_JSON)
				.content(JsonConvertion.asJsonString(beerDTO))).andExpect(status().isCreated())
				.andExpect(redirectedUrlPattern("**/Beers/1"));
	}

	@Test
	void whenInvalidBeerIsInformedAnValidationErrorMustBeReturned() throws Exception {
		// given
		BeerDTO beerDTO = BeerDTOBuilder.build();
		beerDTO.setName(null);
		// when
		// then
		mockMvc.perform(MockMvcRequestBuilders.post("/Beers").contentType(MediaType.APPLICATION_JSON)
				.content(JsonConvertion.asJsonString(beerDTO))).andExpect(status().isBadRequest());

	}

	@Test
	void whenBeerNameIsInformedABeerMustBeReturned() throws Exception {
		// given
		BeerDTO beerDTO = BeerDTOBuilder.build();
		// when
		when(beerService.findByName(beerDTO.getName())).thenReturn(beerDTO);
		// thenThrow(new ResourceNotFoundException(beerDTO.getName())
		mockMvc.perform(
				MockMvcRequestBuilders.get("/Beers/" + beerDTO.getName()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.name", is(beerDTO.getName())))
				.andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
				.andExpect(jsonPath("$.beerType", is(beerDTO.getBeerType().toString())));
	}

	@Test
	void whenUnregisteredBeerNameIsInformedThenNotFoundStatusIsReturned() throws Exception {
		// given
		BeerDTO beerDTO = BeerDTOBuilder.build();
		// when
		when(beerService.findByName(beerDTO.getName())).thenThrow(new ResourceNotFoundException(beerDTO.getName()));
		// then
		mockMvc.perform(
				MockMvcRequestBuilders.get("/Beers/" + beerDTO.getName()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void whenListBeerIsCalledThenReturnAListOfBeers() throws Exception {
		// given
		BeerDTO beerDTO = BeerDTOBuilder.build();
		// when
		when(beerService.findAll()).thenReturn(Collections.singletonList(beerDTO));
		// then
		mockMvc.perform(MockMvcRequestBuilders.get("/Beers").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.[0].name", is(beerDTO.getName())))
				.andExpect(jsonPath("$.[0].brand", is(beerDTO.getBrand())))
				.andExpect(jsonPath("$.[0].beerType", is(beerDTO.getBeerType().toString())));
	}

	@Test
	void whenListBeerIsCalledThenReturnAnEmptyListOfBeers() throws Exception {
		// given

		// when
		when(beerService.findAll()).thenReturn(Collections.emptyList());
		// then
		mockMvc.perform(MockMvcRequestBuilders.get("/Beers")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", is(empty())));
	}

	@Test
	void whenBeerDeleteIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
		//given
		BeerDTO beerDTO = BeerDTOBuilder.build();
		//when
		doNothing().when(beerService).delete(beerDTO.getId());
		//then
		mockMvc.perform(MockMvcRequestBuilders.delete("/Beers/"+beerDTO.getId())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}
	@Test
	void whenBeerDeleteIsCalledWithInvalidIdThenNoContentStatusIsReturned() throws Exception {
		//given
		BeerDTO beerDTO = BeerDTOBuilder.build();
		String id = beerDTO.getId().toString();
		//when
		doThrow(new ResourceNotFoundException(id)).when(beerService).delete(beerDTO.getId());
		//then
		mockMvc.perform(MockMvcRequestBuilders.delete("/Beers/"+beerDTO.getId())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}
	@Test
	void whenIncrementBeerStockIsCalledWithIncrementAfterSumLessThanMaxThenIncrementBeerStock() throws Exception {
		//given
		BeerDTO incrementedBeerDTO = BeerDTOBuilder.build();
		BeerDTO mockedBeerDTO = BeerDTOBuilder.build();
		
		QuantityDTO quantityDTO = QuantityDTOBuilder.build();
		quantityDTO.setQuantity(44);
		mockedBeerDTO.setQuantity(quantityDTO.getQuantity() + incrementedBeerDTO.getQuantity());
		//when
		when(beerService.incrementStock(incrementedBeerDTO.getId(), quantityDTO.getQuantity())).thenReturn(mockedBeerDTO);
		//then
		mockMvc.perform(MockMvcRequestBuilders.put("/Beers/"+quantityDTO.getId()+"/increment")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonConvertion.asJsonString(quantityDTO)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.quantity", is(equalTo(mockedBeerDTO.getQuantity()))))
				.andExpect(jsonPath("$.id", is(equalTo(mockedBeerDTO.getId()))));
	}
	@Test
	void whenIncrementBeerStockIsCalledWithIncrementAfterSumEqualsToMaxThenIncrementBeerStock() throws Exception {
		//given
		BeerDTO incrementedBeerDTO = BeerDTOBuilder.build();
		BeerDTO mockedBeerDTO = BeerDTOBuilder.build();
		
		QuantityDTO quantityDTO = QuantityDTOBuilder.build();
		quantityDTO.setQuantity(45);
		mockedBeerDTO.setQuantity(quantityDTO.getQuantity() + incrementedBeerDTO.getQuantity());
		//when
		when(beerService.incrementStock(incrementedBeerDTO.getId(), quantityDTO.getQuantity())).thenReturn(mockedBeerDTO);
		//then
		mockMvc.perform(MockMvcRequestBuilders.put("/Beers/"+quantityDTO.getId()+"/increment")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonConvertion.asJsonString(quantityDTO)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.quantity", is(equalTo(mockedBeerDTO.getQuantity()))))
				.andExpect(jsonPath("$.id", is(equalTo(mockedBeerDTO.getId()))));
	}
	@Test
	void whenIncrementBeerStockIsCalledWithIncrementAfterSumGreaterThanMaxThenBadRequestIsReturned() throws Exception {
		//given
		BeerDTO incrementedBeerDTO = BeerDTOBuilder.build();
		QuantityDTO quantityDTO = QuantityDTOBuilder.build();
		quantityDTO.setQuantity(46);
		//when
		when(beerService.incrementStock(quantityDTO.getId(), quantityDTO.getQuantity())).thenThrow(new BeerStockExceededException(incrementedBeerDTO.getName()));         
		//then
		mockMvc.perform(MockMvcRequestBuilders.put("/Beers/"+quantityDTO.getId()+"/increment")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonConvertion.asJsonString(quantityDTO)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error", is(equalTo("Increment Exceeded Beer Stock of "+incrementedBeerDTO.getName()))));
	}
	@Test
	void whenDecrementBeerStockIsCalledWithDecrementAfterSubPositiveThenDecrementBeerStock() throws Exception {
		//given
		BeerDTO decrementedBeerDTO = BeerDTOBuilder.build();
		BeerDTO mockedBeerDTO = BeerDTOBuilder.build();
		
		QuantityDTO quantityDTO = QuantityDTOBuilder.build();
		quantityDTO.setQuantity(4);
		mockedBeerDTO.setQuantity(decrementedBeerDTO.getQuantity() - quantityDTO.getQuantity());
		//when
		when(beerService.decrementStock(quantityDTO.getId(), quantityDTO.getQuantity())).thenReturn(mockedBeerDTO);
		//then
		
		mockMvc.perform(MockMvcRequestBuilders.put("/Beers/"+quantityDTO.getId()+"/decrement")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonConvertion.asJsonString(quantityDTO)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.quantity", is(equalTo(mockedBeerDTO.getQuantity()))));
	}
	@Test
	void whenDecrementBeerStockIsCalledWithDecrementAfterSubEqualsToZeroThenDecrementBeerStock() throws Exception {
		//given
		BeerDTO decrementedBeerDTO = BeerDTOBuilder.build();
		BeerDTO mockedBeerDTO = BeerDTOBuilder.build();
		
		QuantityDTO quantityDTO = QuantityDTOBuilder.build();
		quantityDTO.setQuantity(5);
		mockedBeerDTO.setQuantity(decrementedBeerDTO.getQuantity() - quantityDTO.getQuantity());
		//when
		when(beerService.decrementStock(quantityDTO.getId(), quantityDTO.getQuantity())).thenReturn(mockedBeerDTO);
		//then
		
		mockMvc.perform(MockMvcRequestBuilders.put("/Beers/"+quantityDTO.getId()+"/decrement")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonConvertion.asJsonString(quantityDTO)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.quantity", is(equalTo(mockedBeerDTO.getQuantity()))));
	}
	@Test
	void whenDecrementBeerStockIsCalledWithDecrementAfterSubLessThanZeroThenBadRequestIsReturned() throws Exception {
		//given
		BeerDTO decrementedBeerDTO = BeerDTOBuilder.build();
		QuantityDTO quantityDTO = QuantityDTOBuilder.build();
		quantityDTO.setQuantity(5);
		//when
		when(beerService.decrementStock(quantityDTO.getId(), quantityDTO.getQuantity())).thenThrow(new BeerStockExceededException(decrementedBeerDTO.getName()));
		//then
		mockMvc.perform(MockMvcRequestBuilders.put("/Beers/"+quantityDTO.getId()+"/decrement")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonConvertion.asJsonString(quantityDTO)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error", is(equalTo("Decrement Exceeded Beer Stock of "+decrementedBeerDTO.getName()))));
	}
}
