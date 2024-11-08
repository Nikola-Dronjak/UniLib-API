package dronjak.nikola.UniLib.dto;

import java.util.GregorianCalendar;
import java.util.Objects;

public class AuthorDTO {

	private int authorId;

	private String name;

	private GregorianCalendar dateOfBirth;

	private GregorianCalendar dateOfDeath;

	public AuthorDTO() {

	}

	public AuthorDTO(int authorId, String name, GregorianCalendar dateOfBirth, GregorianCalendar dateOfDeath) {
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
		return "AuthorDTO [authorId=" + authorId + ", name=" + name + ", dateOfBirth=" + dateOfBirth + ", dateOfDeath="
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
		AuthorDTO other = (AuthorDTO) obj;
		return authorId == other.authorId && Objects.equals(dateOfBirth, other.dateOfBirth)
				&& Objects.equals(dateOfDeath, other.dateOfDeath) && Objects.equals(name, other.name);
	}
}
