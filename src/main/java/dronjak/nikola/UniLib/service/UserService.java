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

	public ResponseEntity<?> loanBook(int userId, String isbn) {
		try {
			Optional<User> userFromDb = userRepository.findById(userId);
			if (!userFromDb.isPresent())
				throw new RuntimeException("There is no user with the given id.");
			User user = userFromDb.get();

			Optional<Book> bookFromDb = bookRepository.findById(isbn);
			if (!bookFromDb.isPresent())
				throw new RuntimeException("There is no book with the given isbn.");

			Book book = bookFromDb.get();
			if (book.getNumberOfCopies() < 0 || !book.getAvailable())
				throw new RuntimeException("There are no available copies of this book.");

			book.setIsbn(isbn);
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

	public ResponseEntity<?> returnBook(String isbn) {
		try {
			Optional<BookLoan> bookLoanFromDb = bookLoanRepository.findByBook_Isbn(isbn);
			if (!bookLoanFromDb.isPresent())
				throw new RuntimeException("There is no book loan with the given isbn.");
			BookLoan bookLoan = bookLoanFromDb.get();

			User user = bookLoan.getUser();

			Book book = bookLoan.getBook();
			book.setIsbn(isbn);
			book.setNumberOfCopies(book.getNumberOfCopies() + 1);
			book.setAvailable(book.getNumberOfCopies() > 0);
			bookRepository.save(book);

			bookLoanRepository.deleteById(bookLoan.getBookLoandId());
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
