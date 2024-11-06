package dronjak.nikola.UniLib.service;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import dronjak.nikola.UniLib.domain.Book;
import dronjak.nikola.UniLib.domain.BookLoan;
import dronjak.nikola.UniLib.domain.User;
import dronjak.nikola.UniLib.domain.UserRole;
import dronjak.nikola.UniLib.dto.BookLoanDTO;
import dronjak.nikola.UniLib.dto.UserDTO;
import dronjak.nikola.UniLib.repository.BookLoanRepository;
import dronjak.nikola.UniLib.repository.BookRepository;
import dronjak.nikola.UniLib.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Service
public class UserService {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private BookLoanRepository bookLoanRepository;

	@Autowired
	private UserRepository userRepository;

	private Validator validator;

	public UserService() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		this.validator = factory.getValidator();
	}

	public ResponseEntity<?> loanBook(BookLoanDTO bookLoanDTO) {
		try {
			Set<ConstraintViolation<BookLoanDTO>> violations = validator.validate(bookLoanDTO);
			if (!violations.isEmpty()) {
				Map<String, String> errors = new HashMap<>();
				for (ConstraintViolation<BookLoanDTO> violation : violations) {
					errors.put(violation.getPropertyPath().toString(), violation.getMessage());
				}
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
			}

			Optional<User> userFromDb = userRepository.findById(bookLoanDTO.getUserId());
			if (!userFromDb.isPresent())
				throw new RuntimeException("There is no user with the given id.");
			User user = userFromDb.get();

			Optional<Book> bookFromDb = bookRepository.findById(bookLoanDTO.getBookISBN());
			if (!bookFromDb.isPresent())
				throw new RuntimeException("There is no book with the given isbn.");

			Optional<BookLoan> activeBookLoan = bookLoanRepository.findActiveLoanByUserAndBook(bookLoanDTO.getUserId(),
					bookLoanDTO.getBookISBN());
			if (activeBookLoan.isPresent())
				throw new RuntimeException(
						"There is an active book loan for the given userId and bookISBN combination. You cannot loan the same book twice. Please return the book first.");

			Book book = bookFromDb.get();
			if (book.getNumberOfCopies() < 0 || !book.getAvailable())
				throw new RuntimeException("There are no available copies of this book.");

			book.setIsbn(bookLoanDTO.getBookISBN());
			book.setNumberOfCopies(book.getNumberOfCopies() - 1);
			book.setAvailable(book.getNumberOfCopies() > 0);
			bookRepository.save(book);

			BookLoan bookLoan = new BookLoan();
			bookLoan.setUser(user);
			bookLoan.setBook(book);
			bookLoan.setLoanDate(new GregorianCalendar());
			bookLoan.setReturnDate(null);
			bookLoanRepository.save(bookLoan);
			return ResponseEntity.ok("A copy of the book: " + book + " was loand out to user: " + user);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	public ResponseEntity<?> returnBook(BookLoanDTO bookLoanDTO) {
		try {
			Set<ConstraintViolation<BookLoanDTO>> violations = validator.validate(bookLoanDTO);
			if (!violations.isEmpty()) {
				Map<String, String> errors = new HashMap<>();
				for (ConstraintViolation<BookLoanDTO> violation : violations) {
					errors.put(violation.getPropertyPath().toString(), violation.getMessage());
				}
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
			}

			Optional<BookLoan> bookLoanFromDb = bookLoanRepository.findActiveLoanByUserAndBook(bookLoanDTO.getUserId(),
					bookLoanDTO.getBookISBN());
			if (!bookLoanFromDb.isPresent())
				throw new RuntimeException(
						"There is no active book loan for the given userId and bookISBN combination.");
			BookLoan bookLoan = bookLoanFromDb.get();

			User user = bookLoanFromDb.get().getUser();

			Book book = bookLoanFromDb.get().getBook();
			book.setIsbn(bookLoanDTO.getBookISBN());
			book.setNumberOfCopies(book.getNumberOfCopies() + 1);
			book.setAvailable(book.getNumberOfCopies() > 0);
			bookRepository.save(book);

			bookLoan.setBookLoandId(bookLoanFromDb.get().getBookLoandId());
			bookLoan.setLoanDate(bookLoanFromDb.get().getLoanDate());
			bookLoan.setReturnDate(new GregorianCalendar());
			bookLoanRepository.save(bookLoan);
			return ResponseEntity.ok("User: " + user + " returned book: " + book);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	public ResponseEntity<?> update(Integer id, UserDTO userDTO) {
		try {
			Optional<User> userFromDb = userRepository.findById(id);
			if (!userFromDb.isPresent())
				throw new RuntimeException("There is no user with the given id.");

			User user = convertFromDTO(userDTO);
			Set<ConstraintViolation<User>> violations = validator.validate(user);
			if (!violations.isEmpty()) {
				Map<String, String> errors = new HashMap<>();
				for (ConstraintViolation<User> violation : violations) {
					errors.put(violation.getPropertyPath().toString(), violation.getMessage());
				}
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
			}

			if (userDTO.getDateOfBirth().after(new GregorianCalendar()))
				throw new RuntimeException("The users's date of birth has to be in the future.");

			if (!userFromDb.get().getEmail().equals(userDTO.getEmail())) {
				Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
				if (existingUser.isPresent() && existingUser.get().getUserId() != id) {
					throw new RuntimeException("This user already exists.");
				}
			}

			user.setUserId(id);
			user.setPassword(new BCryptPasswordEncoder(12).encode(userDTO.getPassword()));
			user.setRole(UserRole.STUDENT);
			User updatedUser = userRepository.save(user);
			UserDTO updatedUserDTO = convertToDTO(updatedUser);
			return ResponseEntity.ok(updatedUserDTO);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	public ResponseEntity<?> delete(Integer id) {
		try {
			Optional<User> userFromDb = userRepository.findById(id);
			if (!userFromDb.isPresent())
				throw new RuntimeException("There is no user with the given id.");

			userRepository.deleteById(id);
			UserDTO deletedUserDTO = convertToDTO(userFromDb.get());
			return ResponseEntity.ok(deletedUserDTO);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
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

	private User convertFromDTO(UserDTO userDTO) {
		User user = new User();
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setEmail(userDTO.getEmail());
		user.setPassword(userDTO.getPassword());
		user.setDateOfBirth(userDTO.getDateOfBirth());

		return user;
	}
}
