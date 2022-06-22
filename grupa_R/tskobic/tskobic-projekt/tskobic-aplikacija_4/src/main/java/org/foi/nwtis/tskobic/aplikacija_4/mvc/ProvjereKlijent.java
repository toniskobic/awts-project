package org.foi.nwtis.tskobic.aplikacija_4.mvc;

import org.foi.nwtis.tskobic.aplikacija_4.podaci.ZetonOdgovor;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import com.google.gson.Gson;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

/**
 * Klasa ProvjereKlijent koja preuzima podatke od REST APIa.
 */
public class ProvjereKlijent {

	/** Postavke baze podataka. */
	PostavkeBazaPodataka konfig;

	/**
	 * Konstruktor.
	 *
	 * @param context kontekst servleta
	 */
	public ProvjereKlijent(ServletContext context) {
		this.konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
	}

	/**
	 * Autentificiraj korisnika.
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

	/**
	 * Deaktivira žetone proslijeđenog korisnika.
	 *
	 * @param korisnik korisnik koji je poslao zahtjev
	 * @param lozinka lozinka
	 * @param deaktivacijaKorisnik korisnik čiji se žetoni deaktiviraju
	 * @return odgovor
	 */
	public String deaktivirajZetone(String korisnik, String lozinka, String deaktivacijaKorisnik) {
		Client client = ClientBuilder.newClient();

		String adresa = konfig.dajPostavku("adresa.aplikacija_3");
		WebTarget webResource = client.target(adresa + "/provjere").path("korisnik").path(deaktivacijaKorisnik);
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("lozinka", lozinka).delete();
		String odgovor = restOdgovor.readEntity(String.class);
		
		return odgovor;
	}
	
	/**
	 * Deaktiviraj žeton.
	 *
	 * @param korisnik korisnik
	 * @param lozinka lozinka
	 * @param zeton žeton
	 * @return odgovor
	 */
	public String deaktivirajZeton(String korisnik, String lozinka, String zeton) {
		Client client = ClientBuilder.newClient();

		String adresa = konfig.dajPostavku("adresa.aplikacija_3");
		WebTarget webResource = client.target(adresa + "/provjere").path(zeton);
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("lozinka", lozinka).delete();
		String odgovor = restOdgovor.readEntity(String.class);
		
		return odgovor;
	}

}
