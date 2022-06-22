package org.foi.nwtis.tskobic.aplikacija_6.klijenti;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import com.google.gson.Gson;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
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
	 * @param konfig the konfig
	 */
	public KorisniciKlijent(PostavkeBazaPodataka konfig) {
		this.konfig = konfig;
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
}
