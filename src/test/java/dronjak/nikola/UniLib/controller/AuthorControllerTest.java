package dronjak.nikola.UniLib.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import dronjak.nikola.UniLib.domain.Author;
import dronjak.nikola.UniLib.dto.AuthorDTO;
import dronjak.nikola.UniLib.service.AuthorService;

@SpringBootTest
class AuthorControllerTest {

	Author author1;

	Author author2;

	@Mock
	private AuthorService authorService;

	@InjectMocks
	private AuthorController authorController;

	@BeforeEach
	void setUp() throws Exception {
		author1 = new Author(1, "Pera Peric", new GregorianCalendar(2024, 10, 3), new GregorianCalendar(2024, 10, 4));
		author2 = new Author(2, "Mika Mikic", new GregorianCalendar(2024, 10, 5), new GregorianCalendar(2024, 10, 6));
	}

	@AfterEach
	void tearDown() throws Exception {
		author1 = null;
		author2 = null;
	}

	@Test
	void testGetAllAuthorsError() throws Exception {
		try {
			lenient().when(authorService.getAll()).thenThrow(new RuntimeException());
		} catch (Exception e) {
			MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authorController).build();
			mockMvc.perform(get("/api/authors").contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isInternalServerError());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testGetAllAuthors() throws Exception {
		List<Author> authors = new ArrayList<Author>();
		authors.add(author1);
		authors.add(author2);
		when(authorService.getAll()).thenReturn((ResponseEntity) ResponseEntity.ok(authors));

		String authorsJson = new ObjectMapper().writeValueAsString(authors);
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authorController).build();
		mockMvc.perform(get("/api/authors").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().json(authorsJson));
	}

	@Test
	void testGetAuthorByIdError() throws Exception {
		try {
			lenient().when(authorService.getById(1)).thenThrow(new RuntimeException());
		} catch (Exception e) {
			MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authorController).build();
			mockMvc.perform(get("/api/authors/1").contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testGetAuthorById() throws Exception {
		when(authorService.getById(1)).thenReturn((ResponseEntity) ResponseEntity.ok(author1));

		String authorJson = new ObjectMapper().writeValueAsString(author1);
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authorController).build();
		mockMvc.perform(get("/api/authors/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().json(authorJson));
	}

	@Test
	void testAddAuthorError() throws Exception {
		lenient().when(authorService.add(convertToDTO(author1))).thenThrow(new RuntimeException());

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authorController).build();
		mockMvc.perform(post("/api/authors").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testAddAuthor() throws Exception {
		when(authorService.add(any(AuthorDTO.class))).thenReturn((ResponseEntity) ResponseEntity.ok(author1));

		String authorJson = new ObjectMapper().writeValueAsString(convertToDTO(author1));
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authorController).build();
		mockMvc.perform(post("/api/authors").contentType(MediaType.APPLICATION_JSON).content(authorJson))
				.andExpect(status().isOk()).andExpect(content().json(authorJson));
	}

	@Test
	void testUpdateAuthorError() throws Exception {
		lenient().when(authorService.update(1, convertToDTO(author1))).thenThrow(new RuntimeException());

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authorController).build();
		mockMvc.perform(put("/api/authors/1").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testUpdateAuthor() throws Exception {
		when(authorService.update(eq(1), any(AuthorDTO.class))).thenReturn((ResponseEntity) ResponseEntity.ok(author1));

		String authorJson = new ObjectMapper().writeValueAsString(convertToDTO(author1));
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authorController).build();
		mockMvc.perform(put("/api/authors/1").contentType(MediaType.APPLICATION_JSON).content(authorJson))
				.andExpect(status().isOk()).andExpect(content().json(authorJson));
	}

	@Test
	void testDeleteAuthorrError() throws Exception {
		try {
			lenient().when(authorService.delete(1)).thenThrow(new RuntimeException());
		} catch (Exception e) {
			MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authorController).build();
			mockMvc.perform(delete("/api/authors/1").contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testDeleteAuthor() throws Exception {
		when(authorService.delete(1)).thenReturn((ResponseEntity) ResponseEntity.ok(author1));

		String authorJson = new ObjectMapper().writeValueAsString(author1);
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(authorController).build();
		mockMvc.perform(delete("/api/authors/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().json(authorJson));
	}

	private AuthorDTO convertToDTO(Author author) {
		AuthorDTO authorDTO = new AuthorDTO();
		authorDTO.setName(author.getName());
		authorDTO.setDateOfBirth(author.getDateOfBirth());
		authorDTO.setDateOfDeath(author.getDateOfDeath());

		return authorDTO;
	}
}
