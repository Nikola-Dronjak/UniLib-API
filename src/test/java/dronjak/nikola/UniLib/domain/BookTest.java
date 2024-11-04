package dronjak.nikola.UniLib.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class BookTest {

	Author author;

	Set<Author> authors;

	Book book;

	private static Validator validator;

	@BeforeEach
	void setUp() throws Exception {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();

		author = new Author(1, "Pera Peric", new GregorianCalendar(2024, 10, 3), new GregorianCalendar(2024, 10, 4),
				new HashSet<>());

		authors = new HashSet<>();
		authors.add(author);

		book = new Book("1234567890123", "Test", BookGenre.NOVEL, 100, 5, true, authors);
	}

	@AfterEach
	void tearDown() throws Exception {
		author = null;

		authors = null;

		book = null;
	}

	@Test
	public void testISBNNull() {
		book.setIsbn(null);

		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream()
				.anyMatch(violation -> violation.getMessage().equals("The isbn of the book is required.")));
	}

	@Test
	public void testISBNLength() {
		book.setIsbn("A");

		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(
				violation -> violation.getMessage().equals("The isbn of the book has to have exactly 13 characters.")));
	}

	@Test
	public void testTitleNull() {
		book.setTitle(null);

		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream()
				.anyMatch(violation -> violation.getMessage().equals("The title of the book is required.")));
	}

	@Test
	public void testTitleLength() {
		book.setTitle("A");

		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(violation -> violation.getMessage()
				.equals("The title of the book has to have at least 2 characters.")));
	}

	@Test
	public void testGenreNull() {
		book.setGenre(null);

		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream()
				.anyMatch(violation -> violation.getMessage().equals("The genre of the book is required.")));
	}

	@Test
	public void testNumberOfPagesNull() {
		book.setNumberOfPages(null);

		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream()
				.anyMatch(violation -> violation.getMessage().equals("The number of pages for the book is required.")));
	}

	@Test
	public void testNumberOfPagesNegativeValue() {
		book.setNumberOfPages(-1);

		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(violation -> violation.getMessage()
				.equals("The number of pages for the book has to be a positive integer.")));
	}

	@Test
	public void testNumberOfCopiesNull() {
		book.setNumberOfCopies(null);

		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream()
				.anyMatch(violation -> violation.getMessage().equals("The number of copies of the book is required.")));
	}

	@Test
	public void testNumberOfCopiesNegativeValue() {
		book.setNumberOfCopies(-1);

		Set<ConstraintViolation<Book>> violations = validator.validate(book);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(violation -> violation.getMessage()
				.equals("The number of copies of the book has to be a positive integer.")));
	}

	@Test
	public void testEqualsSameObject() {
		Book newBook = book;

		assertTrue(book.equals(newBook));
	}

	@Test
	public void testEqualsNull() {
		assertFalse(book.equals(null));
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEqualsDifferentTypes() {
		assertFalse(book.equals(new String("")));
	}

	@ParameterizedTest
	@CsvSource({ "1, Test, Novel, 100, 5, true, false", "1234567890123, Test1, Novel, 100, 5, true, false",
			"1234567890123, Test, '', 100, 5, true, false", "1234567890123, Test, Novel, 50, 5, true, false",
			"1234567890123, Test, Novel, 100, 4, true, false", "1234567890123, Test, Novel, 100, 5, false, false",
			"1234567890123, Test, Novel, 100, 5, true, true" })
	void testEquals(String isbn, String title, String genre, int numberOfPages, int numberOfCopies, boolean available,
			boolean eq) {
		BookGenre bookGenre = genre.equals("Novel") ? BookGenre.NOVEL : null;
		Book newBook = new Book(isbn, title, bookGenre, numberOfPages, numberOfCopies, available, null);

		assertEquals(eq, book.equals(newBook));
	}
}
