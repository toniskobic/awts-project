package org.foi.nwtis.tskobic.aplikacija_5.klijenti;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import com.google.gson.Gson;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

/**
 * Klasa AerodromiKlijent koja preuzima podatke od REST APIa.
 */
public class AerodromiKlijent {

	/** Postavke baze podataka. */
	PostavkeBazaPodataka konfig;

	/**
	 * Konstruktor.
	 *
	 * @param konfig the konfig
	 */
	public AerodromiKlijent(PostavkeBazaPodataka konfig) {
		this.konfig = konfig;
	}

	/**
	 * Daje polaske aerodroma.
	 *
	 * @param korisnik korisnik
	 * @param zeton žeton
	 * @param icao icao
	 * @param vrijemeOd vrijeme od
	 * @param vrijemeDo vrijeme do
	 * @param vrsta vrsta
	 * @return lista polazaka
	 */
	public List<AvionLeti> dajPolaskeAerodroma(String korisnik, String zeton, String icao, String vrijemeOd,
			String vrijemeDo, int vrsta) {
		Client client = ClientBuilder.newClient();

		String adresa = konfig.dajPostavku("adresa.aplikacija_3");
		WebTarget webResource = client.target(adresa + "/aerodromi").path(icao).path("polasci")
				.queryParam("vrsta", vrsta).queryParam("od", vrijemeOd).queryParam("do", vrijemeDo);
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("zeton", zeton).get();

		List<AvionLeti> aerodromPolasci = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			aerodromPolasci = new ArrayList<>();
			aerodromPolasci.addAll(Arrays.asList(gson.fromJson(odgovor, AvionLeti[].class)));
		}

		return aerodromPolasci;
	}

	/**
	 * Dodaje aerodrom za preuzimanje.
	 *
	 * @param korisnik korisnik
	 * @param zeton žeton
	 * @param icao icao
	 * @return true, ako je uspješno
	 */
	public boolean dodajAerodromPreuzimanje(String korisnik, String zeton, String icao) {
		Client client = ClientBuilder.newClient();

		Aerodrom a = new Aerodrom();
		a.setIcao(icao);

		Gson gson = new Gson();

		String adresa = konfig.dajPostavku("adresa.aplikacija_3");
		WebTarget webResource = client.target(adresa + "/aerodromi");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("zeton", zeton).post(Entity.json(gson.toJson(a)));

		boolean dodan = false;
		if (restOdgovor.getStatus() == 200)
			dodan = true;

		return dodan;
	}

	/**
	 * Daje aerodrom.
	 *
	 * @param korisnik korisnik
	 * @param zeton žeton
	 * @param icao icao aerodroma
	 * @return aerodrom
	 */
	public Aerodrom dajAerodrom(String korisnik, int zeton, String icao) {
		Client client = ClientBuilder.newClient();

		String adresa = konfig.dajPostavku("adresa.aplikacija_3");
		WebTarget webResource = client.target(adresa + "/aerodromi").path(icao);
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("zeton", zeton).get();

		Aerodrom aerodrom = null;
		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			aerodrom = gson.fromJson(odgovor, Aerodrom.class);
		}

		return aerodrom;
	}

	/**
	 * Daje praćene aerodrome.
	 *
	 * @param korisnik korisnik
	 * @param zeton žeton
	 * @return lista praćenih aerodroma
	 */
	public List<Aerodrom> dajPraceneAerodrome(String korisnik, String zeton) {
		Client client = ClientBuilder.newClient();

		String adresa = konfig.dajPostavku("adresa.aplikacija_3");
		WebTarget webResource = client.target(adresa + "/aerodromi?preuzimanje");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("zeton", zeton).get();
		List<Aerodrom> aerodromi = null;

		if (restOdgovor.getStatus() == 200) {
			String odgovor = restOdgovor.readEntity(String.class);
			Gson gson = new Gson();
			aerodromi = new ArrayList<>();
			aerodromi.addAll(Arrays.asList(gson.fromJson(odgovor, Aerodrom[].class)));
		}

		return aerodromi;
	}
	
}
