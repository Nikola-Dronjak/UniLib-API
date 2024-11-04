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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import dronjak.nikola.UniLib.domain.Book;
import dronjak.nikola.UniLib.domain.BookGenre;
import dronjak.nikola.UniLib.dto.BookDTO;
import dronjak.nikola.UniLib.service.BookService;

@SpringBootTest
class BookControllerTest {

	Author author;

	Set<Author> authors;

	Book book1;

	Book book2;

	@Mock
	private BookService bookService;

	@InjectMocks
	private BookController bookController;

	@BeforeEach
	void setUp() throws Exception {
		book1 = new Book("1234567890123", "Test1", BookGenre.NOVEL, 100, 5, true, new HashSet<>());
		book2 = new Book("0987654321098", "Test2", BookGenre.THRILLER, 150, 6, true, new HashSet<>());

		author = new Author(1, "Pera Peric", new GregorianCalendar(2024, 10, 3), new GregorianCalendar(2024, 10, 4),
				new HashSet<>());

		authors = new HashSet<>();
		authors.add(author);

		book1.setAuthors(authors);
		book2.setAuthors(authors);
	}

	@AfterEach
	void tearDown() throws Exception {
		author = null;
		authors = null;

		book1 = null;
		book2 = null;
	}

	@Test
	void testGetAllBooksError() throws Exception {
		try {
			lenient().when(bookService.getAll()).thenThrow(new RuntimeException());
		} catch (Exception e) {
			MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
			mockMvc.perform(get("/api/books").contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isInternalServerError());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testGetAllBooks() throws Exception {
		List<Book> books = new ArrayList<Book>();
		books.add(book1);
		books.add(book2);
		when(bookService.getAll()).thenReturn((ResponseEntity) ResponseEntity.ok(books));

		String booksJson = new ObjectMapper().writeValueAsString(books);
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
		mockMvc.perform(get("/api/books").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().json(booksJson));
	}

	@Test
	void testGetBookByIdError() throws Exception {
		try {
			lenient().when(bookService.getByISBN("1234567890123")).thenThrow(new RuntimeException());
		} catch (Exception e) {
			MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
			mockMvc.perform(get("/api/books/123456789123").contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testGetBookById() throws Exception {
		when(bookService.getByISBN("1234567890123")).thenReturn((ResponseEntity) ResponseEntity.ok(book1));

		String bookJson = new ObjectMapper().writeValueAsString(book1);
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
		mockMvc.perform(get("/api/books/1234567890123").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().json(bookJson));
	}

	@Test
	void testAddBookError() throws Exception {
		lenient().when(bookService.add(convertToDTO(book1))).thenThrow(new RuntimeException());

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
		mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testAddBook() throws Exception {
		when(bookService.add(any(BookDTO.class))).thenReturn((ResponseEntity) ResponseEntity.ok(book1));

		String bookJson = new ObjectMapper().writeValueAsString(convertToDTO(book1));
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
		mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(bookJson))
				.andExpect(status().isOk());
	}

	@Test
	void testUpdateBookError() throws Exception {
		lenient().when(bookService.update("1234567890123", convertToDTO(book1))).thenThrow(new RuntimeException());

		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
		mockMvc.perform(put("/api/books/1234567890123").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testUpdateBook() throws Exception {
		when(bookService.update(eq("1234567890123"), any(BookDTO.class)))
				.thenReturn((ResponseEntity) ResponseEntity.ok(book1));

		String bookJson = new ObjectMapper().writeValueAsString(convertToDTO(book1));
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
		mockMvc.perform(put("/api/books/1234567890123").contentType(MediaType.APPLICATION_JSON).content(bookJson))
				.andExpect(status().isOk());
	}

	@Test
	void testDeleteBookError() throws Exception {
		try {
			lenient().when(bookService.delete("1234567890123")).thenThrow(new RuntimeException());
		} catch (Exception e) {
			MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
			mockMvc.perform(delete("/api/books/123456789123").contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void testDeleteBook() throws Exception {
		when(bookService.delete("1234567890123")).thenReturn((ResponseEntity) ResponseEntity.ok(book1));

		String bookJson = new ObjectMapper().writeValueAsString(book1);
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
		mockMvc.perform(delete("/api/books/1234567890123").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().json(bookJson));
	}

	private BookDTO convertToDTO(Book book) {
		BookDTO bookDTO = new BookDTO();
		bookDTO.setIsbn(book.getIsbn());
		bookDTO.setTitle(book.getTitle());
		bookDTO.setGenre(book.getGenre());
		bookDTO.setNumberOfPages(book.getNumberOfPages());
		bookDTO.setNumberOfCopies(book.getNumberOfCopies());

		Set<Integer> authorIds = new HashSet<Integer>();
		for (Author author : book.getAuthors()) {
			authorIds.add(author.getAuthorId());
		}
		bookDTO.setAuthorIds(authorIds);

		return bookDTO;
	}
}
