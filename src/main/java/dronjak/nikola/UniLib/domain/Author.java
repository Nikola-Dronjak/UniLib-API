package dronjak.nikola.UniLib.domain;

import java.util.GregorianCalendar;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "Authors")
public class Author {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int authorId;
	
	@NotBlank(message = "The name of the author is required.")
	@Size(min = 5, message = "The name of the author has to have at least 5 characters.")
	private String name;
	
	@NotNull(message = "The author's date of birth is required.")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private GregorianCalendar dateOfBirth;
	
	@NotNull(message = "The author's date of death is required.")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private GregorianCalendar dateOfDeath;
	
	public Author() {
		
	}
	
	public Author(int authorId, String name, GregorianCalendar dateOfBirth, GregorianCalendar dateOfDeath) {
		this.authorId = authorId;
		this.name = name;
		this.dateOfBirth = dateOfBirth;
		this.dateOfDeath = dateOfDeath;
	}

	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public GregorianCalendar getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(GregorianCalendar dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public GregorianCalendar getDateOfDeath() {
		return dateOfDeath;
	}

	public void setDateOfDeath(GregorianCalendar dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}
	
	@Override
	public String toString() {
		return "Author [authorId=" + authorId + ", name=" + name + ", dateOfBirth=" + dateOfBirth + ", dateOfDeath="
				+ dateOfDeath + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(authorId, dateOfBirth, dateOfDeath, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Author other = (Author) obj;
		return authorId == other.authorId && Objects.equals(dateOfBirth, other.dateOfBirth)
				&& Objects.equals(dateOfDeath, other.dateOfDeath) && Objects.equals(name, other.name);
	}
}
