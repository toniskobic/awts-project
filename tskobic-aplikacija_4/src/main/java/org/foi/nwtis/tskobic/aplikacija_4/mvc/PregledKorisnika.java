package org.foi.nwtis.tskobic.aplikacija_4.mvc;

import java.util.List;
import java.util.stream.Collectors;

import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.tskobic.aplikacija_4.podaci.Grupa;
import org.foi.nwtis.tskobic.aplikacija_4.podaci.ZetonOdgovor;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;

/**
 * Kontroler PregledKorisnika za putanju korisnici.
 */
@Controller
@Path("korisnici")
@RequestScoped
public class PregledKorisnika {

	/** Žeton. */
	public static ZetonOdgovor zeton;
	
	/** Korisničko ime. */
	public static String korIme;
	
	/** Lozinka. */
	public static String lozinka;
	
	/** Provjerava je li korisnik administrator. */
	public static boolean admin;

	/** Kontekst servleta. */
	@Context
	private ServletContext context;

	/** Model. */
	@Inject
	private Models model;

	/**
	 * Početna stranica.
	 */
	@GET
	@Path("pocetak")
	@View("index.jsp")
	public void pocetak() {
	}

	/**
	 * Registracija.
	 */
	@GET
	@Path("registracija")
	@View("registracija.jsp")
	public void registracija() {
	}

	/**
	 * Obrada registracije.
	 *
	 * @param korIme korisničko ime
	 * @param ime ime
	 * @param prezime prezime
	 * @param lozinka lozinka
	 * @param email email
	 */
	@POST
	@Path("registracija/rezultat")
	@View("registracijaRezultat.jsp")
	public void obradaRegistracije(@FormParam("korIme") String korIme, @FormParam("ime") String ime,
			@FormParam("prezime") String prezime, @FormParam("lozinka") String lozinka,
			@FormParam("email") String email) {

		String sustavKorisnik = ((PostavkeBazaPodataka) context.getAttribute("Postavke"))
				.dajPostavku("sustav.korisnik");
		String sustavLozinka = ((PostavkeBazaPodataka) context.getAttribute("Postavke")).dajPostavku("sustav.lozinka");

		ProvjereKlijent provjereKlijent = new ProvjereKlijent(context);
		ZetonOdgovor zeton = provjereKlijent.autentificirajKorisnika(sustavKorisnik, sustavLozinka);
		String rezultat = "Registracija nije uspješna";
		if (zeton != null) {
			KorisniciKlijent korisniciKlijent = new KorisniciKlijent(context);
			Korisnik k = new Korisnik(korIme, ime, prezime, lozinka, email);

			rezultat = korisniciKlijent.dodajKorisnika(sustavKorisnik, zeton.getZeton(), k);
		}

		model.put("registracija", rezultat);
	}

	/**
	 * Prijava.
	 */
	@GET
	@Path("prijava")
	@View("prijava.jsp")
	public void prijava() {
	}

	/**
	 * Obrada prijave.
	 *
	 * @param korIme korisničko ime
	 * @param lozinka lozinka
	 */
	@POST
	@Path("prijava/rezultat")
	@View("prijavaRezultat.jsp")
	public void obradaPrijave(@FormParam("korIme") String korIme, @FormParam("lozinka") String lozinka) {
		String sustavAdmin = ((PostavkeBazaPodataka) context.getAttribute("Postavke"))
				.dajPostavku("sustav.administratori");

		ProvjereKlijent provjereKlijent = new ProvjereKlijent(context);
		ZetonOdgovor token = provjereKlijent.autentificirajKorisnika(korIme, lozinka);

		String rezultat = "Prijava nije uspješna";
		if (token != null) {
			rezultat = "Prijava je uspješna";
			PregledKorisnika.zeton = token;
			PregledKorisnika.korIme = korIme;
			PregledKorisnika.lozinka = lozinka;

			KorisniciKlijent korisniciKlijent = new KorisniciKlijent(context);

			List<Grupa> grupa = korisniciKlijent.dajGrupeKorisnika(korIme, zeton.getZeton(), korIme);
			List<Grupa> fGrupa = grupa.stream().filter(x -> x.getGrupa().equals(sustavAdmin))
					.collect(Collectors.toList());

			PregledKorisnika.admin = !fGrupa.isEmpty() ? true : false;
		}

		model.put("prijava", rezultat);
	}

	/**
	 * Pregled svih korisnika.
	 */
	@GET
	@Path("pregledKorisnika")
	@View("pregledKorisnika.jsp")
	public void pregledKorisnika() {
		List<Korisnik> korisnici = null;
		if (PregledKorisnika.korIme != null && PregledKorisnika.zeton != null) {
			KorisniciKlijent korisniciKlijent = new KorisniciKlijent(context);
			korisnici = korisniciKlijent.dajSveKorisnike(PregledKorisnika.korIme, PregledKorisnika.zeton.getZeton());
		}

		model.put("korisnici", korisnici);
		model.put("admin", PregledKorisnika.admin);
	}

	/**
	 * Brisanje žetona određenog korisnika.
	 *
	 * @param korisnik korisnik
	 */
	@POST
	@Path("brisanjeZetona/{korisnik}")
	@View("brisanjeZetona.jsp")
	public void brisanjeZetona(@PathParam("korisnik") String korisnik) {
		ProvjereKlijent provjereKlijent = new ProvjereKlijent(context);
		String rezultat = provjereKlijent.deaktivirajZetone(PregledKorisnika.korIme, PregledKorisnika.lozinka,
				korisnik);

		model.put("brisanjeZetona", rezultat);
	}

	/**
	 * Brisanje žetona određenog korisnika.
	 */
	@GET
	@Path("brisanjeZetona")
	@View("brisanjeZetona.jsp")
	public void brisanjeTrenutnogZetona() {
		String rezultat = "Brisanje žetona nije moguće, niste prijavljeni.";
		
		if (PregledKorisnika.korIme != null && PregledKorisnika.zeton != null) {
			ProvjereKlijent provjereKlijent = new ProvjereKlijent(context);
			rezultat = provjereKlijent.deaktivirajZeton(PregledKorisnika.korIme, PregledKorisnika.lozinka,
					Integer.toString(PregledKorisnika.zeton.getZeton()));
		}

		model.put("brisanjeZetona", rezultat);
	}

}
