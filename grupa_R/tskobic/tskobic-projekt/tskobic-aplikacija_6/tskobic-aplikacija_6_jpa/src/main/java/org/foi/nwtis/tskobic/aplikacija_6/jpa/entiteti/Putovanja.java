package org.foi.nwtis.tskobic.aplikacija_6.jpa.entiteti;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


/**
 * The persistent class for the PUTOVANJA database table.
 * 
 */
@Entity
@Table(name="PUTOVANJA")
@NamedQuery(name="Putovanja.findAll", query="SELECT p FROM Putovanja p")
public class Putovanja implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique=true, nullable=false)
	private int id;

	@Column(nullable=false, length=10)
	private String aerodrompocetni;

	@Column(nullable=false, length=10)
	private String aerodromzavrsni;

	@Column(nullable=false)
	private int vrijemeprvogleta;

	//bi-directional many-to-one association to Korisnici
	@ManyToOne
	@JoinColumn(name="KORISNIK", nullable=false)
	private Korisnici korisnici;

	//bi-directional many-to-one association to PutovanjaLetovi
	@OneToMany(mappedBy="putovanja")
	private List<PutovanjaLetovi> putovanjaLetovis;

	public Putovanja() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAerodrompocetni() {
		return this.aerodrompocetni;
	}

	public void setAerodrompocetni(String aerodrompocetni) {
		this.aerodrompocetni = aerodrompocetni;
	}

	public String getAerodromzavrsni() {
		return this.aerodromzavrsni;
	}

	public void setAerodromzavrsni(String aerodromzavrsni) {
		this.aerodromzavrsni = aerodromzavrsni;
	}

	public int getVrijemeprvogleta() {
		return this.vrijemeprvogleta;
	}

	public void setVrijemeprvogleta(int vrijemeprvogleta) {
		this.vrijemeprvogleta = vrijemeprvogleta;
	}

	public Korisnici getKorisnici() {
		return this.korisnici;
	}

	public void setKorisnici(Korisnici korisnici) {
		this.korisnici = korisnici;
	}

	public List<PutovanjaLetovi> getPutovanjaLetovis() {
		return this.putovanjaLetovis;
	}

	public void setPutovanjaLetovis(List<PutovanjaLetovi> putovanjaLetovis) {
		this.putovanjaLetovis = putovanjaLetovis;
	}

	public PutovanjaLetovi addPutovanjaLetovi(PutovanjaLetovi putovanjaLetovi) {
		getPutovanjaLetovis().add(putovanjaLetovi);
		putovanjaLetovi.setPutovanja(this);

		return putovanjaLetovi;
	}

	public PutovanjaLetovi removePutovanjaLetovi(PutovanjaLetovi putovanjaLetovi) {
		getPutovanjaLetovis().remove(putovanjaLetovi);
		putovanjaLetovi.setPutovanja(null);

		return putovanjaLetovi;
	}

}