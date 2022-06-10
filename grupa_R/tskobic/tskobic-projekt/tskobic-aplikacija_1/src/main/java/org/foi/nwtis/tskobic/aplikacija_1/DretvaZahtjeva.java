package org.foi.nwtis.tskobic.aplikacija_1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.foi.nwtis.tskobic.vjezba_03.konfiguracije.Konfiguracija;

/**
 * Klasa dretva DretvaZahtjeva.
 */
public class DretvaZahtjeva extends Thread {

	/** broj aktivnih dretvi. */
	private volatile static int brojAktivnihDretvi = 0;

	private volatile static int statusPosluzitelja = 0;

	/** naziv dretve. */
	String nazivDretve;

	/** Objekt klase ServerGlavni. */
	ServerGlavni serverGlavni = null;

	/** Konfiguracijski podaci. */
	Konfiguracija konfig = null;

	/** veza. */
	Socket veza = null;

	String status = "^STATUS$";

	String prekid = "^QUIT$";

	String inicijalizacija = "^INIT$";

	String ucitavanje = "^LOAD$";

	String udaljenostIcao = "^DISTANCE ([A-Z]{4}) ([A-Z]{4})$";

	String ocisti = "^CLEAR$";

	/**
	 * Konstruktor klase
	 *
	 * @param serverGlavni objekt klase ServerGlavni
	 * @param konfig       konfiguracijski podaci
	 * @param veza         veza
	 */
	public DretvaZahtjeva(ServerGlavni serverGlavni, Konfiguracija konfig, Socket veza) {
		super("tskobic_" + (brojAktivnihDretvi + 1));
		this.serverGlavni = serverGlavni;
		this.nazivDretve = super.getName();
		this.konfig = konfig;
		this.veza = veza;
		synchronized (this) {
			brojAktivnihDretvi++;
		}
	}

	/**
	 * Metoda za pokretanje dretve
	 */
	@Override
	public synchronized void start() {
		super.start();
	}

	/**
	 * Glavna metoda za rad dretve
	 */
	@Override
	public void run() {
		try (InputStreamReader isr = new InputStreamReader(this.veza.getInputStream(), Charset.forName("UTF-8"));
				OutputStreamWriter osw = new OutputStreamWriter(this.veza.getOutputStream(),
						Charset.forName("UTF-8"));) {

			StringBuilder tekst = new StringBuilder();
			while (true) {
				int i = isr.read();
				if (i == -1) {
					break;
				}
				tekst.append((char) i);
			}
			this.veza.shutdownInput();

			obradaZahtjeva(osw, tekst.toString());

		} catch (Exception e) {
			ispis(e.getMessage());
		}
		synchronized (this) {
			brojAktivnihDretvi--;
		}
	}

	/**
	 * Obrada zahtjeva.
	 *
	 * @param osw     izlazni tok podataka
	 * @param komanda omanda
	 */
	public void obradaZahtjeva(OutputStreamWriter osw, String komanda) {
		if (provjeraSintakseObrada(komanda, status)) {
			ispisPoruke(osw, "SVE U REDU");
		} else if (provjeraSintakseObrada(komanda, ocisti)) {
			ispisPoruke(osw, "SVE U REDU");
		}  else {
			ispisPoruke(osw, "ERROR 40 Sintaksa komande nije uredu.");
		}
	}

	/**
	 * Provjera sintakse dozvoljenog izraza.
	 *
	 * @param komanda        komanda
	 * @param regularniIzraz dozvoljeni izraz
	 * @return true, if successful
	 */
	public boolean provjeraSintakseObrada(String komanda, String regularniIzraz) {
		Pattern izraz = Pattern.compile(regularniIzraz);
		Matcher rezultatUsporedbe = izraz.matcher(komanda);

		return rezultatUsporedbe.matches();
	}

	/**
	 * Ispis poruke klijentu.
	 *
	 * @param osw     izlazni tok podataka
	 * @param odgovor odgovor
	 */
	public void ispisPoruke(OutputStreamWriter osw, String odgovor) {
		try {
			osw.write(odgovor);
			osw.flush();
			osw.close();
		} catch (IOException e) {
			ispis(e.getMessage());
		}
	}

	/**
	 * Metoda za prekidanje rada dretve.
	 */
	@Override
	public void interrupt() {
		super.interrupt();
	}

	/**
	 * Ispis poruke na konzolu.
	 *
	 * @param message poruka
	 */
	public void ispis(String message) {
		System.out.println(message);
	}
}
