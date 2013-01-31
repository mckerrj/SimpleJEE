package object;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

@Entity
@Table(name="user", schema="simpleapp")
public class User implements Serializable {

	@Id 
	@Column(name="username", nullable=false, length=100)
	//@GeneratedValue(generator="assigned")
	private String userName = null;
	
	@Column(name="firstName", nullable=false, length=100)
	private String firstName = null;
	
	@Column(name="lastName", nullable=false, length=100)
	private String lastName = null;
	
	@Column(name="password", nullable=false, length=100)
	private String password = null;
	
	@ManyToMany
  @JoinTable(name = "user_role", joinColumns = { @JoinColumn(name = "user_username") }, inverseJoinColumns = { @JoinColumn(name = "role_role") })
	private Set<Role> roles = new HashSet<Role>(0);

	public User() {
	}
	
	public User(String userName, String firstName, String lastName,
			String password) {
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
	}
	
	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Role> getRoles() {
		return this.roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
}
