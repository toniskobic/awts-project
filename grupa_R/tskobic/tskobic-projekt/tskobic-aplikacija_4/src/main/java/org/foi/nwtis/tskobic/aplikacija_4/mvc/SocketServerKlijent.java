package org.foi.nwtis.tskobic.aplikacija_4.mvc;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

/**
 * Klasa SocketServerKlijent koja preuzima podatke sa poslužitelja na utičnici
 */
public class SocketServerKlijent {

	/** Postavke baze podataka. */
	PostavkeBazaPodataka konfig;

	/**
	 * Konstruktor.
	 *
	 * @param context kontekst servleta
	 */
	public SocketServerKlijent(ServletContext context) {
		this.konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
	}

	/**
	 * Šalje naredbu poslužitelju na utičnici.
	 *
	 * @param komanda komanda
	 * @return odgovor
	 */
	public String posaljiNaredbu(String komanda) {
		String adresa = konfig.dajPostavku("server.adresa");
		int port = Integer.valueOf(konfig.dajPostavku("server.port"));
		int cekanje = Integer.valueOf(konfig.dajPostavku("maks.cekanje"));

		String odgovorPosluzitelja = posaljiKomandu(adresa, port, cekanje, komanda);

		return odgovorPosluzitelja;
	}

	/**
	 * Učitava aerodrome u kolekciju aerodroma na poslužitelju na utičnici.
	 *
	 * @param korisnik korisnik
	 * @param zeton    žeton
	 * @return odgovor
	 */
	public String ucitajAerodrome(String korisnik, int zeton) {
		Client client = ClientBuilder.newClient();

		String adresa = konfig.dajPostavku("adresa.aplikacija_3");
		WebTarget webResource = client.target(adresa + "/serveri").path("LOAD");
		Response restOdgovor = webResource.request().header("Accept", "application/json").header("korisnik", korisnik)
				.header("zeton", zeton).post(null);

		String odgovor = restOdgovor.readEntity(String.class);

		return odgovor;
	}

	/**
	 * Slanje komande poslužitelju.
	 *
	 * @param adresa  adresa
	 * @param port    broj porta
	 * @param cekanje maksimalno čekanje na odgovor poslužitelja
	 * @param komanda komanda
	 * @return odgovor poslužitelja
	 */
	public String posaljiKomandu(String adresa, int port, int cekanje, String komanda) {
		InputStreamReader isr = null;
		OutputStreamWriter osw = null;

		try (Socket veza = new Socket()) {
			InetSocketAddress isa = new InetSocketAddress(adresa, port);
			veza.connect(isa, cekanje);
			isr = new InputStreamReader(veza.getInputStream(), Charset.forName("UTF-8"));
			osw = new OutputStreamWriter(veza.getOutputStream(), Charset.forName("UTF-8"));

			osw.write(komanda);
			osw.flush();
			veza.shutdownOutput();
			StringBuilder tekst = new StringBuilder();
			while (true) {
				int i = isr.read();
				if (i == -1) {
					break;
				}
				tekst.append((char) i);
			}
			veza.shutdownInput();
			veza.close();
			return tekst.toString();
		} catch (IOException e) {
			Logger.getLogger(SocketServerKlijent.class.getName()).log(Level.SEVERE, null, e);
		} finally {
			try {
				if (isr != null) {
					isr.close();
				}
				if (osw != null) {
					osw.close();
				}
			} catch (IOException e) {
				Logger.getLogger(SocketServerKlijent.class.getName()).log(Level.SEVERE, null, e);
			}
		}

		return null;
	}
}
