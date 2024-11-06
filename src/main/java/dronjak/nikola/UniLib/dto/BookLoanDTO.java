package dronjak.nikola.UniLib.dto;

import java.util.Objects;

import jakarta.validation.constraints.NotNull;

public class BookLoanDTO {

	@NotNull(message = "You must enter a user that is loaning the book.")
	private Integer userId;

	@NotNull(message = "You must enter the isbn book that is being loand.")
	private String bookISBN;

	public BookLoanDTO() {

	}

	public BookLoanDTO(Integer userId, String bookISBN) {
		super();
		this.userId = userId;
		this.bookISBN = bookISBN;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getBookISBN() {
		return bookISBN;
	}

	public void setBookISBN(String bookISBN) {
		this.bookISBN = bookISBN;
	}

	@Override
	public String toString() {
		return "BookLoanDTO [userId=" + userId + ", bookISBN=" + bookISBN + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(bookISBN, userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookLoanDTO other = (BookLoanDTO) obj;
		return Objects.equals(bookISBN, other.bookISBN) && Objects.equals(userId, other.userId);
	}
}
