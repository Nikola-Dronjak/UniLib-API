package dronjak.nikola.UniLib.dto;

import java.util.Objects;
import java.util.Set;

import dronjak.nikola.UniLib.domain.BookGenre;

public class BookDTO {

	private String isbn;

	private String title;

	private BookGenre genre;

	private Integer numberOfPages;

	private Integer numberOfCopies;

	private Set<Integer> authorIds;

	public BookDTO() {
	}

	public BookDTO(String isbn, String title, BookGenre genre, Integer numberOfPages, Integer numberOfCopies,
			Set<Integer> authorIds) {
		super();
		this.isbn = isbn;
		this.title = title;
		this.genre = genre;
		this.numberOfPages = numberOfPages;
		this.numberOfCopies = numberOfCopies;
		this.authorIds = authorIds;
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

	public Set<Integer> getAuthorIds() {
		return authorIds;
	}

	public void setAuthorIds(Set<Integer> authorIds) {
		this.authorIds = authorIds;
	}

	@Override
	public String toString() {
		return "BookDTO [isbn=" + isbn + ", title=" + title + ", genre=" + genre + ", numberOfPages=" + numberOfPages
				+ ", numberOfCopies=" + numberOfCopies + ", authorIds=" + authorIds + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(authorIds, genre, isbn, numberOfCopies, numberOfPages, title);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookDTO other = (BookDTO) obj;
		return Objects.equals(authorIds, other.authorIds) && genre == other.genre && Objects.equals(isbn, other.isbn)
				&& Objects.equals(numberOfCopies, other.numberOfCopies)
				&& Objects.equals(numberOfPages, other.numberOfPages) && Objects.equals(title, other.title);
	}
}
