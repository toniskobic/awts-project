package org.foi.nwtis.tskobic.aplikacija_6.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.foi.nwtis.tskobic.aplikacija_6.jpa.criteriaapi.GrupeJpa;
import org.foi.nwtis.tskobic.aplikacija_6.jpa.criteriaapi.KorisniciJpa;
import org.foi.nwtis.tskobic.aplikacija_6.jpa.entiteti.Grupe;
import org.foi.nwtis.tskobic.aplikacija_6.jpa.entiteti.Korisnici;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

//@ConversationScoped
@SessionScoped
@Named("korisniciZrnoCriteriaApi")
public class KorisniciZrnoCriteriaApi implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	KorisniciJpa korisniciJpa;

	@EJB
	GrupeJpa grupeJpa;

	List<Korisnici> korisnici = new ArrayList<>();
	Korisnici korisnik = new Korisnici();
	Grupe grupa = new Grupe();

	boolean traziGrupe = false;

	public List<Korisnici> getKorisnici() {
		if (!traziGrupe) {
			korisnici = this.dajSveKorisnike();
		}
		return korisnici;
	}

	public void setKorisnici(List<Korisnici> korisnici) {
		this.korisnici = korisnici;
	}

	public Korisnici getKorisnik() {
		return korisnik;
	}

	public void setKorisnik(Korisnici korisnik) {
		this.korisnik = korisnik;
	}

	public Grupe getGrupa() {
		return grupa;
	}

	public void setGrupa(Grupe grupa) {
		this.grupa = grupa;
	}

	public List<Korisnici> dajSveKorisnike() {
		List<Korisnici> lKorisnicii = (List<Korisnici>) korisniciJpa.findAll();

		return lKorisnicii;
	}

	public String odabirKorisnika(String korisnikId) {
		this.korisnik = korisniciJpa.find(korisnikId);
		return "";
	}

	public String odabirGrupe(String grupaId) {
		this.grupa = grupeJpa.find(grupaId);
		traziGrupe = true;
		this.korisnici = this.grupa.getKorisnicis();
		return "";
	}

}