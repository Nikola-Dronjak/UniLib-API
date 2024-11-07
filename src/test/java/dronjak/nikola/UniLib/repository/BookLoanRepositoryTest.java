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

import dronjak.nikola.UniLib.domain.BookLoan;
import dronjak.nikola.UniLib.domain.Author;
import dronjak.nikola.UniLib.domain.Book;
import dronjak.nikola.UniLib.domain.BookGenre;
import dronjak.nikola.UniLib.domain.User;
import dronjak.nikola.UniLib.domain.UserRole;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookLoanRepositoryTest {

	User user;

	Book book;
	
	Author author;

	Set<Author> authors;

	BookLoan bookLoan1;

	BookLoan bookLoan2;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthorRepository authorRepository;

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private BookLoanRepository bookLoanRepository;

	@BeforeEach
	void setUp() throws Exception {
		user = new User(1, "Pera", "Peric", "pera@gmail.com", "test123", UserRole.STUDENT,
				new GregorianCalendar(2024, 10, 4));
		userRepository.save(user);

		book = new Book("1234567890123", "Test1", BookGenre.NOVEL, 100, 5, true, new HashSet<Author>());

		author = new Author(1, "Pera Peric", new GregorianCalendar(2024, 10, 3), new GregorianCalendar(2024, 10, 4),
				new HashSet<>());

		authorRepository.save(author);

		authors = new HashSet<>();
		authors.add(author);

		book.setAuthors(authors);

		bookRepository.save(book);

		bookLoan1 = new BookLoan(1, new GregorianCalendar(2024, 10, 3), null, user, book);
		bookLoan2 = new BookLoan(2, new GregorianCalendar(2024, 10, 4), new GregorianCalendar(2024, 10, 5), user, book);
	}

	@AfterEach
	void tearDown() throws Exception {
		user = null;

		author = null;

		authors = null;

		book = null;

		bookLoan1 = null;
		bookLoan2 = null;
	}

	@Test
	void testFindAllEmpty() {
		List<BookLoan> bookLoans = bookLoanRepository.findAll();

		assertEquals(0, bookLoans.size());
	}

	@Test
	void testFindAll() {
		bookLoanRepository.save(bookLoan1);
		bookLoanRepository.save(bookLoan2);

		List<BookLoan> bookLoans = bookLoanRepository.findAll();

		assertEquals(2, bookLoans.size());
		assertEquals(bookLoan1, bookLoans.get(0));
		assertEquals(bookLoan2, bookLoans.get(1));
	}

	@Test
	void testFindByIdBadId() {
		bookLoanRepository.save(bookLoan1);

		Optional<BookLoan> bookLoan = bookLoanRepository.findById(2);

		assertTrue(bookLoan.isEmpty());
	}

	@Test
	void testFindById() {
		bookLoanRepository.save(bookLoan1);

		Optional<BookLoan> bookLoan = bookLoanRepository.findById(1);

		assertTrue(bookLoan.isPresent());
		assertEquals(bookLoan1, bookLoan.get());
	}

	@Test
	void testFindActiveLoanByUserAndBookBadUserId() {
		bookLoanRepository.save(bookLoan1);

		Optional<BookLoan> bookLoan = bookLoanRepository.findActiveLoanByUserAndBook(2, "1234567890123");

		assertTrue(bookLoan.isEmpty());
	}

	@Test
	void testFindActiveLoanByUserAndBookBadBookISBN() {
		bookLoanRepository.save(bookLoan1);

		Optional<BookLoan> bookLoan = bookLoanRepository.findActiveLoanByUserAndBook(1, "1234567890124");

		assertTrue(bookLoan.isEmpty());
	}

	@Test
	void testFindActiveLoanByUserAndBookInactive() {
		bookLoanRepository.save(bookLoan1);

		Optional<BookLoan> bookLoan = bookLoanRepository.findActiveLoanByUserAndBook(2, "1234567890123");

		assertTrue(bookLoan.isEmpty());
	}

	@Test
	void testSave() {
		BookLoan savedBookLoan = bookLoanRepository.save(bookLoan1);

		assertEquals(bookLoan1, savedBookLoan);
	}

	@Test
	void testDeleteById() {
		bookLoanRepository.save(bookLoan1);

		bookLoanRepository.deleteById(1);

		Optional<BookLoan> deletedBookLoan = bookLoanRepository.findById(1);
		assertFalse(deletedBookLoan.isPresent());
	}
}
