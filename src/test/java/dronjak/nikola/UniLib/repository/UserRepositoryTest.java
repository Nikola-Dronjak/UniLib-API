package dronjak.nikola.UniLib.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import dronjak.nikola.UniLib.domain.User;
import dronjak.nikola.UniLib.domain.UserRole;
import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryTest {

	User user1;

	User user2;

	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() throws Exception {
		user1 = new User(1, "Pera", "Peric", "pera@gmail.com", "test123", UserRole.STUDENT,
				new GregorianCalendar(2024, 10, 4));
		user2 = new User(2, "Mika", "Mikic", "mika@gmail.com", "test321", UserRole.STUDENT,
				new GregorianCalendar(2024, 10, 5));
	}

	@AfterEach
	void tearDown() throws Exception {
		user1 = null;
		user2 = null;
	}

	@Test
	void testFindAllEmpty() {
		List<User> users = userRepository.findAll();

		assertEquals(0, users.size());
	}

	@Test
	void testFindAll() {
		userRepository.save(user1);
		userRepository.save(user2);

		List<User> users = userRepository.findAll();

		assertEquals(2, users.size());
		assertEquals(user1, users.get(0));
		assertEquals(user2, users.get(1));
	}

	@Test
	void testFindByIdBadId() {
		userRepository.save(user1);

		Optional<User> user = userRepository.findById(2);

		assertTrue(user.isEmpty());
	}

	@Test
	void testFindById() {
		userRepository.save(user1);

		Optional<User> user = userRepository.findById(1);

		assertTrue(user.isPresent());
		assertEquals(user1, user.get());
	}

	@Test
	void testFindByEmailBadEmail() {
		userRepository.save(user1);

		Optional<User> user = userRepository.findByEmail("mika@gmail.com");

		assertTrue(user.isEmpty());
	}

	@Test
	void testFindByName() {
		userRepository.save(user1);

		Optional<User> user = userRepository.findByEmail("pera@gmail.com");

		assertTrue(user.isPresent());
		assertEquals(user1, user.get());
	}

	@Test
	void testSave() {
		User savedAuthor = userRepository.save(user1);

		assertEquals(user1, savedAuthor);
	}

	@Test
	void testDeleteById() {
		userRepository.save(user1);

		userRepository.deleteById(1);

		Optional<User> deletedAuthor = userRepository.findById(1);
		assertFalse(deletedAuthor.isPresent());
	}
}
