package dronjak.nikola.UniLib.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import dronjak.nikola.UniLib.domain.Author;
import dronjak.nikola.UniLib.domain.Book;
import dronjak.nikola.UniLib.dto.BookDTO;
import dronjak.nikola.UniLib.repository.AuthorRepository;
import dronjak.nikola.UniLib.repository.BookRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@Service
public class BookService {

	@Autowired
	private AuthorRepository authorRepository;

	@Autowired
	private BookRepository bookRepository;

	private Validator validator;

	public BookService() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		this.validator = factory.getValidator();
	}

	public ResponseEntity<?> getAll() {
		try {
			List<Book> books = bookRepository.findAll();
			List<BookDTO> bookDTOs = books.stream().map(this::convertToDTO).collect(Collectors.toList());
			return ResponseEntity.ok(bookDTOs);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	public ResponseEntity<?> getByISBN(String isbn) {
		try {
			Optional<Book> bookFromDb = bookRepository.findById(isbn);
			if (!bookFromDb.isPresent())
				throw new RuntimeException("There is no book with the given isbn.");

			BookDTO bookDTO = convertToDTO(bookFromDb.get());
			return ResponseEntity.ok(bookDTO);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	public ResponseEntity<?> add(BookDTO bookDTO) {
		try {
			Book book = convertFromDTO(bookDTO);
			Set<ConstraintViolation<Book>> violations = validator.validate(book);
			if (!violations.isEmpty()) {
				Map<String, String> errors = new HashMap<>();
				for (ConstraintViolation<Book> violation : violations) {
					errors.put(violation.getPropertyPath().toString(), violation.getMessage());
				}
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
			}

			if (bookRepository.findById(bookDTO.getIsbn()).isPresent())
				throw new RuntimeException("This book already exists.");

			book.setAvailable(bookDTO.getNumberOfCopies() > 0);
			Book newBook = bookRepository.save(book);
			for (Integer authorId : bookDTO.getAuthorIds()) {
				Optional<Author> authorFromDb = authorRepository.findById(authorId);
				Author author = authorFromDb.get();
				author.getBooks().add(newBook);
				authorRepository.save(author);
			}
			BookDTO newBookDTO = convertToDTO(newBook);
			return ResponseEntity.ok(newBookDTO);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	public ResponseEntity<?> update(String isbn, BookDTO bookDTO) {
		try {
			if (!isbn.equals(bookDTO.getIsbn()))
				throw new RuntimeException("You cant change the isbn of the book.");

			Optional<Book> bookFromDb = bookRepository.findById(isbn);
			if (!bookFromDb.isPresent())
				throw new RuntimeException("There is no book with the given isbn.");

			Book book = convertFromDTO(bookDTO);
			Set<ConstraintViolation<Book>> violations = validator.validate(book);
			if (!violations.isEmpty()) {
				Map<String, String> errors = new HashMap<>();
				for (ConstraintViolation<Book> violation : violations) {
					errors.put(violation.getPropertyPath().toString(), violation.getMessage());
				}
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
			}

			book.setAvailable(bookDTO.getNumberOfCopies() > 0);
			Book updatedBook = bookRepository.save(book);
			for (Integer authorId : bookDTO.getAuthorIds()) {
				Optional<Author> authorFromDb = authorRepository.findById(authorId);
				Author author = authorFromDb.get();
				author.getBooks().add(updatedBook);
				authorRepository.save(author);
			}
			BookDTO updatedBookDTO = convertToDTO(updatedBook);
			return ResponseEntity.ok(updatedBookDTO);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	public ResponseEntity<?> delete(String isbn) {
		try {
			Optional<Book> bookFromDb = bookRepository.findById(isbn);
			if (!bookFromDb.isPresent())
				throw new RuntimeException("There is no book with the given isbn.");

			Book book = bookFromDb.get();
			for (Author author : book.getAuthors()) {
				author.getBooks().remove(book);
				authorRepository.save(author);
			}

			bookRepository.deleteById(isbn);
			BookDTO deletedBookDTO = convertToDTO(bookFromDb.get());
			return ResponseEntity.ok(deletedBookDTO);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	private BookDTO convertToDTO(Book book) {
		BookDTO bookDTO = new BookDTO();
		bookDTO.setIsbn(book.getIsbn());
		bookDTO.setTitle(book.getTitle());
		bookDTO.setGenre(book.getGenre());
		bookDTO.setNumberOfPages(book.getNumberOfPages());
		bookDTO.setNumberOfCopies(book.getNumberOfCopies());

		Set<Integer> authorIds = book.getAuthors().stream().map(Author::getAuthorId).collect(Collectors.toSet());
		bookDTO.setAuthorIds(authorIds);

		return bookDTO;
	}

	private Book convertFromDTO(BookDTO bookDTO) {
		Book book = new Book();
		book.setIsbn(bookDTO.getIsbn());
		book.setTitle(bookDTO.getTitle());
		book.setGenre(bookDTO.getGenre());
		book.setNumberOfPages(bookDTO.getNumberOfPages());
		book.setNumberOfCopies(bookDTO.getNumberOfCopies());

		Set<Author> authors = bookDTO.getAuthorIds().stream()
				.map(authorId -> authorRepository.findById(authorId)
						.orElseThrow(() -> new RuntimeException("There is no author with the given authorId.")))
				.collect(Collectors.toSet());
		book.setAuthors(authors);

		return book;
	}
}
