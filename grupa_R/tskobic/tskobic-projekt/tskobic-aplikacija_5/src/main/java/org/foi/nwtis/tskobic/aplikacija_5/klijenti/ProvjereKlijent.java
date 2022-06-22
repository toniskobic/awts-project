package org.foi.nwtis.tskobic.aplikacija_5.klijenti;

import org.foi.nwtis.tskobic.aplikacija_5.podaci.ZetonOdgovor;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import com.google.gson.Gson;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

// TODO: Auto-generated Javadoc
/**
 * Klasa ProvjereKlijent koja preuzima podatke od REST APIa.
 */
public class ProvjereKlijent {

	/** Postavke baze podataka. */
	PostavkeBazaPodataka konfig;

	/**
	 * Konstruktor.
	 *
	 * @param konfig postavke baza podataka
	 */
	public ProvjereKlijent(PostavkeBazaPodataka konfig) {
		this.konfig = konfig;
	}

	/**
	 * Autentificira korisnika.
	 *
	 * @param korisnik korisnik
	 * @param lozinka lozinka
	 * @return žeton
	 */
	public ZetonOdgovor autentificirajKorisnika(String korisnik, String lozinka) {
		Client client = ClientBuilder.newClient();

		String adresa = konfig.dajPostavku("adresa.aplikacija_3");
		WebTarget webResource = client.target(adresa + "/provjere");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("lozinka", lozinka).get();
		
		ZetonOdgovor zeton = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			zeton = gson.fromJson(odgovor, ZetonOdgovor.class);
		}

		return zeton;
	}
}
