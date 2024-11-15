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

class AuthorTest {

	Set<Book> books;

	Author author;

	private static Validator validator;

	@BeforeEach
	void setUp() throws Exception {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();

		books = new HashSet<Book>();

		author = new Author(1, "Pera Peric", new GregorianCalendar(2024, 10, 3), new GregorianCalendar(2024, 10, 4),
				books);
	}

	@AfterEach
	void tearDown() throws Exception {
		books = null;

		author = null;
	}

	@Test
	public void testNameNull() {
		author.setName(null);

		Set<ConstraintViolation<Author>> violations = validator.validate(author);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream()
				.anyMatch(violation -> violation.getMessage().equals("The name of the author is required.")));
	}

	@Test
	public void testNameLength() {
		author.setName("A");

		Set<ConstraintViolation<Author>> violations = validator.validate(author);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(violation -> violation.getMessage()
				.equals("The name of the author has to have at least 5 characters.")));
	}

	@Test
	public void testDateOfBirthNull() {
		author.setDateOfBirth(null);

		Set<ConstraintViolation<Author>> violations = validator.validate(author);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream()
				.anyMatch(violation -> violation.getMessage().equals("The author's date of birth is required.")));
	}

	@Test
	public void testValidAuthor() {
		Set<ConstraintViolation<Author>> violations = validator.validate(author);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testEqualsSameObject() {
		Author newAuthor = author;

		assertTrue(author.equals(newAuthor));
	}

	@Test
	public void testEqualsNull() {
		assertFalse(author.equals(null));
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEqualsDifferentTypes() {
		assertFalse(author.equals(new String("")));
	}

	@ParameterizedTest
	@CsvSource({ "2, Pera Peric, 2024-10-3, 2024-10-4, false", "1, Mika Mikic, 2024-10-3, 2024-10-4, false",
			"1, Pera Peric, 2024-10-2, 2024-10-4, false", "2, Pera Peric, 2024-10-3, 2024-10-5, false",
			"1, Pera Peric, 2024-10-3, 2024-10-4, true" })
	void testEquals(int authorId, String name, String dateOfBirth, String dateOfDeath, boolean eq) {
		GregorianCalendar birthDate = dateOfBirth.equals("2024-10-3") ? new GregorianCalendar(2024, 10, 3)
				: new GregorianCalendar(2024, 10, 2);
		GregorianCalendar deathDate = dateOfDeath.equals("2024-10-4") ? new GregorianCalendar(2024, 10, 4)
				: new GregorianCalendar(2024, 10, 5);
		Author newAuthor = new Author(authorId, name, birthDate, deathDate, null);

		assertEquals(eq, author.equals(newAuthor));
	}
}
