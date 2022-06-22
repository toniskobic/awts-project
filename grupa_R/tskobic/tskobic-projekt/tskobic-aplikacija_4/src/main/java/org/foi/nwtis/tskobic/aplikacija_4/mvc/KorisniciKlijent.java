package org.foi.nwtis.tskobic.aplikacija_4.mvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.tskobic.aplikacija_4.podaci.Grupa;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import com.google.gson.Gson;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

/**
 * Klasa KorisniciKlijent koja preuzima podatke od REST APIa.
 */
public class KorisniciKlijent {

	/** Postavke baze podataka. */
	PostavkeBazaPodataka konfig;

	/**
	 * Konstruktor.
	 *
	 * @param context kontekst servleta
	 */
	public KorisniciKlijent(ServletContext context) {
		this.konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
	}

	/**
	 * Dodaje korisnika.
	 *
	 * @param korisnik korisnik koji šalje zahthev
	 * @param zeton žeton
	 * @param k proslijeđeni korisnik
	 * @return odgovor REST APIa
	 */
	public String dodajKorisnika(String korisnik, int zeton, Korisnik k) {
		Client client = ClientBuilder.newClient();

		Gson gson = new Gson();

		String adresa = konfig.dajPostavku("adresa.aplikacija_3");
		WebTarget webResource = client.target(adresa + "/korisnici");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("zeton", zeton).post(Entity.json(gson.toJson(k)));
		String odgovor = restOdgovor.readEntity(String.class);

		return odgovor;
	}

	/**
	 * Daje sve korisnike.
	 *
	 * @param korisnik korisnik
	 * @param zeton žeton
	 * @return lista korisnika
	 */
	public List<Korisnik> dajSveKorisnike(String korisnik, int zeton) {
		Client client = ClientBuilder.newClient();

		String adresa = konfig.dajPostavku("adresa.aplikacija_3");
		WebTarget webResource = client.target(adresa + "/korisnici");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("zeton", zeton).get();
		List<Korisnik> korisnici = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			korisnici = new ArrayList<>();
			korisnici.addAll(Arrays.asList(gson.fromJson(odgovor, Korisnik[].class)));
		}
		return korisnici;
	}

	/**
	 * Daje grupe korisnika.
	 *
	 * @param korisnik korisnik
	 * @param zeton žeton
	 * @param korisnikGrupe korisnik za kojeg se traže grupe
	 * @return lista grupa
	 */
	public List<Grupa> dajGrupeKorisnika(String korisnik, int zeton, String korisnikGrupe) {
		Client client = ClientBuilder.newClient();

		String adresa = konfig.dajPostavku("adresa.aplikacija_3");
		WebTarget webResource = client.target(adresa + "/korisnici").path(korisnikGrupe).path("grupe");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("zeton", zeton).get();
		
		List<Grupa> grupe = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			grupe = new ArrayList<>();
			grupe.addAll(Arrays.asList(gson.fromJson(odgovor, Grupa[].class)));
		}
		
		return grupe;
	}
}
