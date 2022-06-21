package org.foi.nwtis.tskobic.aplikacija_6.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.tskobic.aplikacija_6.jpa.criteriaapi.KorisniciJpa;
import org.foi.nwtis.tskobic.aplikacija_6.jpa.entiteti.Korisnici;
import org.foi.nwtis.tskobic.aplikacija_6.klijenti.KorisniciKlijent;
import org.foi.nwtis.tskobic.aplikacija_6.klijenti.ProvjereKlijent;
import org.foi.nwtis.tskobic.aplikacija_6.podaci.ZetonOdgovor;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.event.ActionListener;
import jakarta.inject.Named;

//@ConversationScoped
@SessionScoped
@Named("korisniciZrno")
public class KorisniciZrno implements ActionListener, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
	KorisniciJpa korisniciJpa;

	String greska;

	String korIme;

	String lozinka;

	ZetonOdgovor zeton;

	List<Korisnik> korisnici = new ArrayList<>();

	List<Korisnici> lokalniKorisnici = new ArrayList<>();

	public String getGreska() {
		return greska;
	}

	public void setGreska(String greska) {
		this.greska = greska;
	}

	public List<Korisnici> getLokalniKorisnici() {
		return lokalniKorisnici;
	}

	public void setLokalniKorisnici(List<Korisnici> lokalniKorisnici) {
		this.lokalniKorisnici = lokalniKorisnici;
	}

	public List<Korisnik> getKorisnici() {
		korisnici = this.dajSveKorisnike();
		return korisnici;
	}

	public void setKorisnici(List<Korisnik> korisnici) {
		this.korisnici = korisnici;
	}

	public ZetonOdgovor getZeton() {
		return zeton;
	}

	public void setZeton(ZetonOdgovor zeton) {
		this.zeton = zeton;
	}

	public String getKorIme() {
		return korIme;
	}

	public void setKorIme(String korIme) {
		this.korIme = korIme;
	}

	public String getLozinka() {
		return lozinka;
	}

	public void setLozinka(String lozinka) {
		this.lozinka = lozinka;
	}

	public List<Korisnik> dajSveKorisnike() {
		List<Korisnik> korisnici = new ArrayList<>();
		if (zeton != null && korIme != null) {
			PostavkeBazaPodataka pbp = Aplikacija.konfig;
			KorisniciKlijent korisniciKlijent = new KorisniciKlijent(pbp);
			korisnici = korisniciKlijent.dajSveKorisnike(korIme, zeton.getZeton());

			this.lokalniKorisnici = (List<Korisnici>) korisniciJpa.findAll();
		}

		return korisnici;
	}

	public Object prijaviKorisnika() {
		Korisnici korisnik = null;
		korisnik = korisniciJpa.find(korIme);
		if (korisnik != null) {
			if (korisnik.getLozinka().equals(this.lozinka)) {
				PostavkeBazaPodataka pbp = Aplikacija.konfig;
				ProvjereKlijent provjereKlijent = new ProvjereKlijent(pbp);
				zeton = provjereKlijent.autentificirajKorisnika(korIme, lozinka);
				return "OK";
			} else {
				this.greska = "Greška: Pogrešno unesena lozinka.";
				return "NOT_OK";
			}
		} else {
			this.greska = "Greška: Korisnik ne postoji u lokalnoj bazi.";
			return "NOT_OK";
		}
	}

	public Object odjaviKorisnika() {
		if (zeton != null && korIme != null) {
			PostavkeBazaPodataka pbp = Aplikacija.konfig;
			ProvjereKlijent provjereKlijent = new ProvjereKlijent(pbp);
			String rezultat = provjereKlijent.deaktivirajZeton(korIme, lozinka, Integer.toString(zeton.getZeton()));

			if (rezultat == null) {
				this.greska = "Greška: Žeton nije ispravan.";
				return "NOT_OK";
			} else {
				zeton = null;
				korIme = null;
				lozinka = null;
				return "OK";
			}
		} else {
			this.greska = "Greška: Korisnik nije prijavljen.";
			return "NOT_OK";
		}
	}

	public String provjeriStatusPrijave() {
		if (zeton == null && korIme == null) {
			this.greska = "Greška: Korisnik nije prijavljen.";
			return "greska.xhtml";
		} else {
			return "OK";
		}
	}

	public boolean provjeriKorisnika(String korIme) {
		List<Korisnici> fLokalniKorisnici = this.lokalniKorisnici.stream().filter(x -> x.getKorisnik().equals(korIme))
				.collect(Collectors.toList());

		boolean rezultat = fLokalniKorisnici.isEmpty() ? true : false;

		return rezultat;
	}

	public void sinkroniziraj() {
		List<Korisnik> fKorisnici = this.korisnici.stream()
				.filter(x -> this.lokalniKorisnici.stream().noneMatch(k -> k.getKorisnik().equals(x.getKorIme())))
				.collect(Collectors.toList());

		for (Korisnik k : fKorisnici) {
			Korisnici noviKorisnik = new Korisnici();
			noviKorisnik.setKorisnik(k.getKorIme());
			noviKorisnik.setIme(k.getIme());
			noviKorisnik.setPrezime(k.getPrezime());
			noviKorisnik.setLozinka(k.getLozinka());
			noviKorisnik.setEmail(k.getEmail());

			this.korisniciJpa.create(noviKorisnik);
		}
	}

	@Override
	public void processAction(ActionEvent event) throws AbortProcessingException {
	}

}