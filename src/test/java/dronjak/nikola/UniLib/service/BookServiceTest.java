package dronjak.nikola.UniLib.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import dronjak.nikola.UniLib.domain.Author;
import dronjak.nikola.UniLib.domain.Book;
import dronjak.nikola.UniLib.domain.BookGenre;
import dronjak.nikola.UniLib.dto.BookDTO;
import dronjak.nikola.UniLib.repository.AuthorRepository;
import dronjak.nikola.UniLib.repository.BookRepository;

@SpringBootTest
class BookServiceTest {

	Author author;

	Set<Author> authors;

	Book book1;

	Book book2;

	@Mock
	private AuthorRepository authorRepository;

	@Mock
	private BookRepository bookRepository;

	@InjectMocks
	private BookService bookService;

	@BeforeEach
	void setUp() throws Exception {
		author = new Author(1, "Pera Peric", new GregorianCalendar(2024, 10, 3), new GregorianCalendar(2024, 10, 4),
				new HashSet<>());

		authors = new HashSet<>();
		authors.add(author);

		book1 = new Book("1234567890123", "Test1", BookGenre.NOVEL, 100, 5, true, new HashSet<>());
		book2 = new Book("0987654321098", "Test2", BookGenre.THRILLER, 150, 6, true, new HashSet<>());

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
	void testGetAllError() {
		when(bookRepository.findAll()).thenThrow(new RuntimeException("Something went wrong"));

		ResponseEntity<?> response = bookService.getAll();

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Something went wrong", response.getBody());
	}

	@Test
	void testGetAll() {
		List<Book> books = new ArrayList<Book>();
		books.add(book1);
		books.add(book2);
		when(bookRepository.findAll()).thenReturn(books);

		ResponseEntity<?> response = bookService.getAll();
		List<BookDTO> bookDTOs = books.stream().map(this::convertToDTO).collect(Collectors.toList());

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(bookDTOs, response.getBody());
	}

	@Test
	void testGetByIdBadId() {
		when(bookRepository.findById("12345678901234")).thenReturn(Optional.empty());

		ResponseEntity<?> response = bookService.getByISBN("1234567890123");

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("There is no book with the given isbn.", response.getBody());
	}

	@Test
	void testGetById() {
		when(bookRepository.findById("1234567890123")).thenReturn(Optional.of(book1));

		ResponseEntity<?> response = bookService.getByISBN("1234567890123");

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(convertToDTO(book1), response.getBody());
	}

	@Test
	void testAdd() {
		when(bookRepository.findById(book1.getIsbn())).thenReturn(Optional.empty());
		when(authorRepository.findById(1)).thenReturn(Optional.of(author));
		when(bookRepository.save(any(Book.class))).thenReturn(book1);

		ResponseEntity<?> response = bookService.add(convertToDTO(book1));

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(convertToDTO(book1), response.getBody());
	}

	@Test
	void testUpdateBadId() {
		when(bookRepository.findById(book1.getIsbn())).thenReturn(Optional.empty());

		ResponseEntity<?> response = bookService.update("1234567890123", convertToDTO(book1));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("There is no book with the given isbn.", response.getBody());
	}

	@Test
	void testUpdateDuplicateBook() {
		when(bookRepository.findById(book1.getIsbn())).thenReturn(Optional.of(book1));
		when(authorRepository.findById(1)).thenReturn(Optional.of(author));
		when(bookRepository.findById(book1.getIsbn())).thenReturn(Optional.of(book1));
		when(bookRepository.save(any(Book.class))).thenThrow(new RuntimeException("This book already exists."));

		ResponseEntity<?> response = bookService.update(book1.getIsbn(), convertToDTO(book1));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("This book already exists.", response.getBody());
	}

	@Test
	void testUpdate() {
		when(bookRepository.findById(book1.getIsbn())).thenReturn(Optional.of(book1));
		when(authorRepository.findById(1)).thenReturn(Optional.of(author));
		when(bookRepository.save(any(Book.class))).thenReturn(book1);

		ResponseEntity<?> response = bookService.update("1234567890123", convertToDTO(book1));

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(convertToDTO(book1), response.getBody());
	}

	@Test
	void testDeleteBadId() {
		when(bookRepository.findById(book1.getIsbn())).thenReturn(Optional.empty());

		ResponseEntity<?> response = bookService.delete("1234567890123");

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("There is no book with the given isbn.", response.getBody());
	}

	@Test
	void testDelete() {
		when(bookRepository.findById(book1.getIsbn())).thenReturn(Optional.of(book1));

		ResponseEntity<?> response = bookService.delete("1234567890123");

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(convertToDTO(book1), response.getBody());
	}

	private BookDTO convertToDTO(Book book) {
		BookDTO bookDTO = new BookDTO();
		bookDTO.setIsbn(book.getIsbn());
		bookDTO.setTitle(book.getTitle());
		bookDTO.setGenre(book.getGenre());
		bookDTO.setNumberOfPages(book.getNumberOfPages());
		bookDTO.setNumberOfCopies(book.getNumberOfCopies());

		Set<Integer> authorIds = book.getAuthors().stream().map(Author::getAuthorId).collect(Collectors.toSet());
		bookDTO.setAuthorIds(authorIds);

		return bookDTO;
	}
}
