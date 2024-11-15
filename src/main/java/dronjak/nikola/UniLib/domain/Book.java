package dronjak.nikola.UniLib.domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "Books")
public class Book {

	@Id
	@NotBlank(message = "The isbn of the book is required.")
	@Size(min = 13, max = 13, message = "The isbn of the book has to have exactly 13 characters.")
	@Column(length = 13, nullable = false, unique = true)
	private String isbn;

	@NotBlank(message = "The title of the book is required.")
	@Size(min = 2, message = "The title of the book has to have at least 2 characters.")
	private String title;

	@NotNull(message = "The genre of the book is required.")
	private BookGenre genre;

	@NotNull(message = "The number of pages for the book is required.")
	@Positive(message = "The number of pages for the book has to be a positive integer.")
	private Integer numberOfPages;

	@NotNull(message = "The number of copies of the book is required.")
	@Positive(message = "The number of copies of the book has to be a positive integer.")
	private Integer numberOfCopies;

	private Boolean available;

	@Size(min = 1, message = "The author/authors of the book is/are required.")
	@ManyToMany(mappedBy = "books", fetch = FetchType.EAGER)
	private Set<Author> authors;

	public Book() {
		authors = new HashSet<Author>();
	}

	public Book(String isbn, String title, BookGenre genre, Integer numberOfPages, Integer numberOfCopies,
			Boolean available, Set<Author> authors) {
		this.isbn = isbn;
		this.title = title;
		this.genre = genre;
		this.numberOfPages = numberOfPages;
		this.numberOfCopies = numberOfCopies;
		this.available = available;
		this.authors = (authors != null) ? authors : new HashSet<Author>();
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BookGenre getGenre() {
		return genre;
	}

	public void setGenre(BookGenre genre) {
		this.genre = genre;
	}

	public Integer getNumberOfPages() {
		return numberOfPages;
	}

	public void setNumberOfPages(Integer numberOfPages) {
		this.numberOfPages = numberOfPages;
	}

	public Integer getNumberOfCopies() {
		return numberOfCopies;
	}

	public void setNumberOfCopies(Integer numberOfCopies) {
		this.numberOfCopies = numberOfCopies;
	}

	public Boolean getAvailable() {
		return available;
	}

	public void setAvailable(Boolean available) {
		this.available = available;
	}

	public Set<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(Set<Author> authors) {
		this.authors = authors;
	}

	@Override
	public String toString() {
		String authorNames = authors.stream().map(Author::getName).collect(Collectors.joining(", "));
		return "Book [isbn=" + isbn + ", title=" + title + ", genre=" + genre + ", numberOfPages=" + numberOfPages
				+ ", numberOfCopies=" + numberOfCopies + ", available=" + available + ", authors=" + authorNames + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(available, genre, isbn, numberOfCopies, numberOfPages, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		return Objects.equals(available, other.available) && genre == other.genre && Objects.equals(isbn, other.isbn)
				&& Objects.equals(numberOfCopies, other.numberOfCopies)
				&& Objects.equals(numberOfPages, other.numberOfPages) && Objects.equals(title, other.title);
	}
}
