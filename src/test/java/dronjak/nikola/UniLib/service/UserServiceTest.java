package dronjak.nikola.UniLib.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
import dronjak.nikola.UniLib.domain.BookLoan;
import dronjak.nikola.UniLib.domain.User;
import dronjak.nikola.UniLib.domain.UserRole;
import dronjak.nikola.UniLib.dto.BookLoanDTO;
import dronjak.nikola.UniLib.dto.UserDTO;
import dronjak.nikola.UniLib.repository.BookLoanRepository;
import dronjak.nikola.UniLib.repository.BookRepository;
import dronjak.nikola.UniLib.repository.UserRepository;

@SpringBootTest
class UserServiceTest {

	User user1;

	User user2;

	Book book1;

	Book book2;

	Author author;

	Set<Author> authors;

	BookLoan bookLoan1;

	BookLoan bookLoan2;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private BookLoanRepository bookLoanRepository;

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@BeforeEach
	void setUp() throws Exception {
		user1 = new User(1, "Pera", "Peric", "pera@gmail.com", "test123", UserRole.STUDENT,
				new GregorianCalendar(2024, 10, 4));
		user2 = new User(2, "Mika", "Mikic", "mika@gmail.com", "test321", UserRole.STUDENT,
				new GregorianCalendar(2024, 10, 5));

		book1 = new Book("1234567890123", "Test1", BookGenre.NOVEL, 100, 5, true, new HashSet<Author>());
		book2 = new Book("1234567890123", "Test2", BookGenre.FICTION, 100, 0, false, new HashSet<Author>());

		author = new Author(1, "Pera Peric", new GregorianCalendar(2024, 10, 3), new GregorianCalendar(2024, 10, 4),
				new HashSet<>());

		authors = new HashSet<>();
		authors.add(author);

		book1.setAuthors(authors);
		book2.setAuthors(authors);

		bookLoan1 = new BookLoan(1, new GregorianCalendar(2024, 10, 3), null, user1, book1);
		bookLoan2 = new BookLoan(2, new GregorianCalendar(2024, 10, 4), new GregorianCalendar(2024, 10, 5), user1,
				book2);
	}

	@AfterEach
	void tearDown() throws Exception {
		user1 = null;
		user2 = null;

		book1 = null;
		book2 = null;

		author = null;

		authors = null;

		bookLoan1 = null;
		bookLoan2 = null;
	}

	@Test
	void testLoanBookBadUserId() {
		when(userRepository.findById(user1.getUserId())).thenReturn(Optional.empty());

		ResponseEntity<?> response = userService.loanBook(convertToDTO(bookLoan1));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("There is no user with the given id.", response.getBody());
	}

	@Test
	void testLoanBookBadBookISBN() {
		when(userRepository.findById(user1.getUserId())).thenReturn(Optional.of(user1));
		when(bookRepository.findById(book1.getIsbn())).thenReturn(Optional.empty());

		ResponseEntity<?> response = userService.loanBook(convertToDTO(bookLoan1));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("There is no book with the given isbn.", response.getBody());
	}

	@Test
	void testLoanBookActiveLoan() {
		when(userRepository.findById(user1.getUserId())).thenReturn(Optional.of(user1));
		when(bookRepository.findById(book1.getIsbn())).thenReturn(Optional.of(book1));
		when(bookLoanRepository.findActiveLoanByUserAndBook(user1.getUserId(), book1.getIsbn()))
				.thenReturn(Optional.of(bookLoan1));

		ResponseEntity<?> response = userService.loanBook(convertToDTO(bookLoan1));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(
				"There is an active book loan for the given userId and bookISBN combination. You cannot loan the same book twice. Please return the book first.",
				response.getBody());
	}

	@Test
	void testLoanBookNoAvailableCopies() {
		when(userRepository.findById(user1.getUserId())).thenReturn(Optional.of(user1));
		when(bookRepository.findById(book2.getIsbn())).thenReturn(Optional.of(book2));
		when(bookLoanRepository.findActiveLoanByUserAndBook(user1.getUserId(), book2.getIsbn()))
				.thenReturn(Optional.empty());

		ResponseEntity<?> response = userService.loanBook(convertToDTO(bookLoan1));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("There are no available copies of this book.", response.getBody());
	}

	@Test
	void testLoanBook() {
		when(userRepository.findById(user1.getUserId())).thenReturn(Optional.of(user1));
		when(bookRepository.findById(book1.getIsbn())).thenReturn(Optional.of(book1));
		when(bookLoanRepository.findActiveLoanByUserAndBook(user1.getUserId(), book1.getIsbn()))
				.thenReturn(Optional.empty());
		when(bookRepository.findById(book1.getIsbn())).thenReturn(Optional.of(book1));

		ResponseEntity<?> response = userService.loanBook(convertToDTO(bookLoan1));

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("A copy of the book: " + book1 + " was loand out to user: " + user1, response.getBody());
	}

	@Test
	void testReturnBookNoActiveLoan() {
		when(bookLoanRepository.findActiveLoanByUserAndBook(user1.getUserId(), book1.getIsbn()))
				.thenReturn(Optional.empty());

		ResponseEntity<?> response = userService.returnBook(convertToDTO(bookLoan1));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("There is no active book loan for the given userId and bookISBN combination.", response.getBody());
	}

	@Test
	void testReturnBook() {
		when(bookLoanRepository.findActiveLoanByUserAndBook(user1.getUserId(), book1.getIsbn()))
				.thenReturn(Optional.of(bookLoan1));

		ResponseEntity<?> response = userService.returnBook(convertToDTO(bookLoan1));

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("User: " + user1 + " returned book: " + book1, response.getBody());
	}

	@Test
	void testUpdateBadId() {
		when(userRepository.findById(user1.getUserId())).thenReturn(Optional.empty());

		ResponseEntity<?> response = userService.update(1, convertToDTO(user1));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("There is no user with the given id.", response.getBody());
	}

	@Test
	void testUpdateBadDateOfBirth() {
		when(userRepository.findById(1)).thenReturn(Optional.of(user1));
		when(userRepository.save(any(User.class)))
				.thenThrow(new RuntimeException("The user's date of birth has to be before his/hers date of death."));

		UserDTO userDTO = convertToDTO(user1);
		GregorianCalendar birthDate = user1.getDateOfBirth();
		birthDate.add(Calendar.DAY_OF_MONTH, 1);
		userDTO.setDateOfBirth(birthDate);

		ResponseEntity<?> response = userService.update(1, userDTO);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("The user's date of birth has to be before his/hers date of death.", response.getBody());
	}

	@Test
	void testUpdateDuplicateAuthor() {
		when(userRepository.findById(user1.getUserId())).thenReturn(Optional.of(user1));
		when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
		when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("This user already exists."));

		ResponseEntity<?> response = userService.update(user1.getUserId(), convertToDTO(user1));

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("This user already exists.", response.getBody());
	}

	@Test
	void testUpdate() {
		when(userRepository.findById(user1.getUserId())).thenReturn(Optional.of(user1));
		when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.empty());
		when(userRepository.save(any(User.class))).thenReturn(user1);

		ResponseEntity<?> response = userService.update(1, convertToDTO(user1));

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(convertToDTO(user1), response.getBody());
	}

	@Test
	void testDeleteBadId() {
		when(userRepository.findById(user1.getUserId())).thenReturn(Optional.empty());

		ResponseEntity<?> response = userService.delete(1);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("There is no user with the given id.", response.getBody());
	}

	@Test
	void testDelete() {
		when(userRepository.findById(user1.getUserId())).thenReturn(Optional.of(user1));

		ResponseEntity<?> response = userService.delete(1);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(convertToDTO(user1), response.getBody());
	}

	private UserDTO convertToDTO(User user) {
		UserDTO userDTO = new UserDTO();
		userDTO.setFirstName(user.getFirstName());
		userDTO.setLastName(user.getLastName());
		userDTO.setEmail(user.getEmail());
		userDTO.setPassword(user.getPassword());
		userDTO.setDateOfBirth(user.getDateOfBirth());

		return userDTO;
	}

	private BookLoanDTO convertToDTO(BookLoan bookLoan) {
		BookLoanDTO bookLoanDTO = new BookLoanDTO();
		bookLoanDTO.setUserId(bookLoan.getUser().getUserId());
		bookLoanDTO.setBookISBN(bookLoan.getBook().getIsbn());

		return bookLoanDTO;
	}
}
