package dronjak.nikola.UniLib.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.GregorianCalendar;
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

class UserTest {

	User user;

	private static Validator validator;

	@BeforeEach
	void setUp() throws Exception {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();

		user = new User(1, "Pera", "Peric", "pera@gmail.com", "test123", UserRole.STUDENT,
				new GregorianCalendar(2024, 10, 3));
	}

	@AfterEach
	void tearDown() throws Exception {
		user = null;
	}

	@Test
	public void testFirstNameNull() {
		user.setFirstName(null);

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream()
				.anyMatch(violation -> violation.getMessage().equals("The first name of the user is required.")));
	}

	@Test
	public void testFirstNameLength() {
		user.setFirstName("A");

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(violation -> violation.getMessage()
				.equals("The first name of the user has to have at least 2 characters.")));
	}

	@Test
	public void testLastNameNull() {
		user.setLastName(null);

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream()
				.anyMatch(violation -> violation.getMessage().equals("The last name of the user is required.")));
	}

	@Test
	public void testLastNameLength() {
		user.setLastName("A");

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(violation -> violation.getMessage()
				.equals("The last name of the user has to have at least 2 characters.")));
	}

	@Test
	public void testEmailNull() {
		user.setEmail(null);

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream()
				.anyMatch(violation -> violation.getMessage().equals("The email of the user is required.")));
	}

	@Test
	public void testEmailInvalid() {
		user.setEmail("A");

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream()
				.anyMatch(violation -> violation.getMessage().equals("The email address of the user must be valid.")));
	}

	@Test
	public void testPasswordNull() {
		user.setPassword(null);

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream()
				.anyMatch(violation -> violation.getMessage().equals("The password of the user is required.")));
	}

	@Test
	public void testPasswordLength() {
		user.setPassword("A");

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream().anyMatch(violation -> violation.getMessage()
				.equals("The password of the user has to have at least 5 characters.")));
	}

	@Test
	public void testDateOfBirthNull() {
		user.setDateOfBirth(null);

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty());
		assertTrue(violations.stream()
				.anyMatch(violation -> violation.getMessage().equals("The user's date of birth is required.")));
	}

	@Test
	public void testValidUser() {
		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertTrue(violations.isEmpty());
	}

	@Test
	public void testEqualsSameObject() {
		User newUser = user;

		assertTrue(user.equals(newUser));
	}

	@Test
	public void testEqualsNull() {
		assertFalse(user.equals(null));
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEqualsDifferentTypes() {
		assertFalse(user.equals(new String("")));
	}

	@ParameterizedTest
	@CsvSource({ "2, Pera, Peric, pera@gmail.com, test123, STUDENT, 2024-10-3, false",
			"1, Mika, Peric, pera@gmail.com, test123, STUDENT, 2024-10-3, false",
			"1, Pera, Mikic, pera@gmail.com, test123, STUDENT, 2024-10-3, false",
			"1, Pera, Peric, mika@gmail.com, test123, STUDENT, 2024-10-3, false",
			"1, Pera, Peric, pera@gmail.com, test1234, STUDENT, 2024-10-3, false",
			"1, Pera, Peric, pera@gmail.com, test123, Admin, 2024-10-3, false",
			"1, Pera, Peric, pera@gmail.com, test123, STUDENT, 2024-10-4, false",
			"1, Pera, Peric, pera@gmail.com, test123, STUDENT, 2024-10-3, true" })
	void testEquals(int userId, String firstName, String lastName, String email, String password, String role,
			String dateOfBirth, boolean eq) {
		GregorianCalendar birthDate = dateOfBirth.equals("2024-10-3") ? new GregorianCalendar(2024, 10, 3)
				: new GregorianCalendar(2024, 10, 4);
		UserRole userRole = role.equals("STUDENT") ? UserRole.STUDENT : UserRole.ADMINISTRATOR;
		User newUser = new User(userId, firstName, lastName, email, password, userRole, birthDate);

		assertEquals(eq, user.equals(newUser));
	}
}
