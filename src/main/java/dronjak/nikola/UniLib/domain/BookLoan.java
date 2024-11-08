package dronjak.nikola.UniLib.domain;

import java.util.GregorianCalendar;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class BookLoan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int bookLoanId;

	private GregorianCalendar loanDate;

	@Column(nullable = true)
	private GregorianCalendar returnDate;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "isbn")
	private Book book;

	public BookLoan() {

	}

	public BookLoan(int bookLoanId, GregorianCalendar loanDate, GregorianCalendar returnDate, User user, Book book) {
		this.bookLoanId = bookLoanId;
		this.loanDate = loanDate;
		this.returnDate = returnDate;
		this.user = user;
		this.book = book;
	}

	public int getBookLoandId() {
		return bookLoanId;
	}

	public void setBookLoandId(int bookLoandId) {
		this.bookLoanId = bookLoandId;
	}

	public GregorianCalendar getLoanDate() {
		return loanDate;
	}

	public void setLoanDate(GregorianCalendar loanDate) {
		this.loanDate = loanDate;
	}

	public GregorianCalendar getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(GregorianCalendar returnDate) {
		this.returnDate = returnDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	@Override
	public String toString() {
		return "BookLoan [loanDate=" + loanDate.getTime() + ", returnDate=" + returnDate.getTime() + ", user=" + user
				+ ", book=" + book + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(book, bookLoanId, loanDate, returnDate, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookLoan other = (BookLoan) obj;
		return Objects.equals(book, other.book) && bookLoanId == other.bookLoanId
				&& Objects.equals(loanDate, other.loanDate) && Objects.equals(returnDate, other.returnDate)
				&& Objects.equals(user, other.user);
	}
}
