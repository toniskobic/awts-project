package org.foi.nwtis.tskobic.aplikacija_6.jpa.entiteti;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;


/**
 * The persistent class for the KORISNICI database table.
 * 
 */
@Entity
@NamedQuery(name="Korisnici.findAll", query="SELECT k FROM Korisnici k")
public class Korisnici implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String korisnik;

	private String email;

	private String ime;

	private String lozinka;

	private String prezime;

	//bi-directional many-to-many association to Grupe
	@ManyToMany
	@JoinTable(
		name="ULOGE"
		, joinColumns={
			@JoinColumn(name="KORISNIK")
			}
		, inverseJoinColumns={
			@JoinColumn(name="GRUPA")
			}
		)
	private List<Grupe> grupes;

	//bi-directional many-to-one association to Putovanja
	@OneToMany(mappedBy="korisnici")
	private List<Putovanja> putovanjas;

	public Korisnici() {
	}

	public String getKorisnik() {
		return this.korisnik;
	}

	public void setKorisnik(String korisnik) {
		this.korisnik = korisnik;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getIme() {
		return this.ime;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public String getLozinka() {
		return this.lozinka;
	}

	public void setLozinka(String lozinka) {
		this.lozinka = lozinka;
	}

	public String getPrezime() {
		return this.prezime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	public List<Grupe> getGrupes() {
		return this.grupes;
	}

	public void setGrupes(List<Grupe> grupes) {
		this.grupes = grupes;
	}

	public List<Putovanja> getPutovanjas() {
		return this.putovanjas;
	}

	public void setPutovanjas(List<Putovanja> putovanjas) {
		this.putovanjas = putovanjas;
	}

	public Putovanja addPutovanja(Putovanja putovanja) {
		getPutovanjas().add(putovanja);
		putovanja.setKorisnici(this);

		return putovanja;
	}

	public Putovanja removePutovanja(Putovanja putovanja) {
		getPutovanjas().remove(putovanja);
		putovanja.setKorisnici(null);

		return putovanja;
	}

}