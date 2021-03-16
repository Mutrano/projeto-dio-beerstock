package com.mutrano.beerstock.resources;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.mutrano.beerstock.dto.BeerDTO;
import com.mutrano.beerstock.services.BeerService;
import com.mutrano.beerstock.utils.JsonConvertion;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class BeerResourceTest {

	private MockMvc mockMvc;

	@Mock
	private BeerService beerService;

	@InjectMocks
	private BeerResource beerResource;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(beerResource)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.setViewResolvers((s, locale) -> new MappingJackson2JsonView()).build();
	}

	@Test
	void whenValidBeerIsInformedItShouldBeCreated() throws Exception {
		// given
		BeerDTO beerDTO = BeerDTOBuilder.build();
		// when
		when(beerService.insertBeer(beerDTO)).thenReturn(beerDTO);
		// then

		mockMvc.perform(MockMvcRequestBuilders.post("/Beers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonConvertion.asJsonString(beerDTO)))
				.andExpect(status().isCreated())
				.andExpect(redirectedUrlPattern("**/Beers/1"));
	}

	@Test
	void whenInvalidBeerIsInformedAnValidationErrorMustBeReturned() throws Exception {
		// given
		BeerDTO beerDTO = BeerDTOBuilder.build();
		beerDTO.setName(null);
		// when
		// then
		mockMvc.perform(MockMvcRequestBuilders.post("/Beers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(JsonConvertion.asJsonString(beerDTO)))
				.andExpect(status()
				.isBadRequest());

	}
}
