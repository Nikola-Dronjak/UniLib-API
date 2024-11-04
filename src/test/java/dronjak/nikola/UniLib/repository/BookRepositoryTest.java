package dronjak.nikola.UniLib.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import dronjak.nikola.UniLib.domain.Author;
import dronjak.nikola.UniLib.domain.Book;
import dronjak.nikola.UniLib.domain.BookGenre;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookRepositoryTest {

	Author author;

	Set<Author> authors;

	Book book1;

	Book book2;

	@Autowired
	private AuthorRepository authorRepository;

	@Autowired
	private BookRepository bookRepository;

	@BeforeEach
	void setUp() throws Exception {
		book1 = new Book("1234567890123", "Test1", BookGenre.NOVEL, 100, 5, true, new HashSet<>());
		book2 = new Book("0987654321098", "Test2", BookGenre.THRILLER, 150, 6, true, new HashSet<>());

		author = new Author(1, "Pera Peric", new GregorianCalendar(2024, 10, 3), new GregorianCalendar(2024, 10, 4),
				new HashSet<>());

		authorRepository.save(author);

		authors = new HashSet<>();
		authors.add(author);

		book1.setAuthors(authors);
		book2.setAuthors(authors);
	}

	@AfterEach
	void tearDown() throws Exception {
		authors = null;

		book1 = null;
		book2 = null;
	}

	@Test
	void testFindAllEmpty() {
		List<Book> books = bookRepository.findAll();

		assertEquals(0, books.size());
	}

	@Test
	void testFindAll() {
		bookRepository.save(book1);
		bookRepository.save(book2);

		List<Book> books = bookRepository.findAll();

		assertEquals(2, books.size());
		assertEquals(book1, books.get(0));
		assertEquals(book2, books.get(1));
	}

	@Test
	void testFindByIdBadId() {
		bookRepository.save(book1);

		Optional<Book> book = bookRepository.findById("0987654321098");

		assertTrue(book.isEmpty());
	}

	@Test
	void testFindById() {
		bookRepository.save(book1);

		Optional<Book> book = bookRepository.findById("1234567890123");

		assertTrue(book.isPresent());
		assertEquals(book1, book.get());
	}

	@Test
	void testSave() {
		Book savedBook = bookRepository.save(book1);

		assertEquals(book1, savedBook);
	}

	@Test
	void testDeleteById() {
		bookRepository.save(book1);

		bookRepository.deleteById("1234567890123");

		Optional<Book> deletedBook = bookRepository.findById("1234567890123");
		assertFalse(deletedBook.isPresent());
	}
}
