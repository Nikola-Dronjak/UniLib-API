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
import org.springframework.test.context.ActiveProfiles;

import dronjak.nikola.UniLib.domain.Author;
import dronjak.nikola.UniLib.domain.Book;
import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthorRepositoryTest {

	Set<Book> books;

	Author author1;

	Author author2;

	@Autowired
	private AuthorRepository authorRepository;

	@BeforeEach
	void setUp() throws Exception {
		books = new HashSet<Book>();

		author1 = new Author(1, "Pera Peric", new GregorianCalendar(2024, 10, 3), new GregorianCalendar(2024, 10, 4),
				books);
		author2 = new Author(2, "Mika Mikic", new GregorianCalendar(2024, 10, 5), new GregorianCalendar(2024, 10, 6),
				books);
	}

	@AfterEach
	void tearDown() throws Exception {
		books = null;

		author1 = null;
		author2 = null;
	}

	@Test
	void testFindAllEmpty() {
		List<Author> authors = authorRepository.findAll();

		assertEquals(0, authors.size());
	}

	@Test
	void testFindAll() {
		authorRepository.save(author1);
		authorRepository.save(author2);

		List<Author> authors = authorRepository.findAll();

		assertEquals(2, authors.size());
		assertEquals(author1, authors.get(0));
		assertEquals(author2, authors.get(1));
	}

	@Test
	void testFindByIdBadId() {
		authorRepository.save(author1);

		Optional<Author> author = authorRepository.findById(2);

		assertTrue(author.isEmpty());
	}

	@Test
	void testFindById() {
		authorRepository.save(author1);

		Optional<Author> author = authorRepository.findById(1);

		assertTrue(author.isPresent());
		assertEquals(author1, author.get());
	}

	@Test
	void testFindByNameBadName() {
		authorRepository.save(author1);

		Optional<Author> author = authorRepository.findByName("Mika Mikic");

		assertTrue(author.isEmpty());
	}

	@Test
	void testFindByName() {
		authorRepository.save(author1);

		Optional<Author> author = authorRepository.findByName("Pera Peric");

		assertTrue(author.isPresent());
		assertEquals(author1, author.get());
	}

	@Test
	void testSave() {
		Author savedAuthor = authorRepository.save(author1);

		assertEquals(author1, savedAuthor);
	}

	@Test
	void testDeleteById() {
		authorRepository.save(author1);

		authorRepository.deleteById(1);

		Optional<Author> deletedAuthor = authorRepository.findById(1);
		assertFalse(deletedAuthor.isPresent());
	}
}
