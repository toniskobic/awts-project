package org.foi.nwtis.tskobic.aplikacija_1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.tskobic.vjezba_03.konfiguracije.Konfiguracija;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

/**
 * Klasa dretva DretvaZahtjeva.
 */
public class DretvaZahtjeva extends Thread {

	/** broj aktivnih dretvi. */
	private volatile static int brojAktivnihDretvi = 0;

	private volatile static int statusPosluzitelja = 0;

	static volatile List<Aerodrom> aerodromi = new ArrayList<>();

	/** naziv dretve. */
	String nazivDretve;

	ServerSocket ss = null;

	/** Objekt klase ServerGlavni. */
	ServerGlavni serverGlavni = null;

	/** Konfiguracijski podaci. */
	Konfiguracija konfig = null;

	/** veza. */
	Socket veza = null;

	String status = "^STATUS$";

	String prekid = "^QUIT$";

	String inicijalizacija = "^INIT$";

	String ucitavanje = "^LOAD(.*)$";

	String udaljenostIcao = "^DISTANCE ([A-Z]{4}) ([A-Z]{4})$";

	String ocisti = "^CLEAR$";

	/**
	 * Konstruktor klase
	 *
	 * @param serverGlavni objekt klase ServerGlavni
	 * @param konfig       konfiguracijski podaci
	 * @param veza         veza
	 */
	public DretvaZahtjeva(ServerGlavni serverGlavni, Konfiguracija konfig, Socket veza, ServerSocket ss) {
		super("tskobic_" + (brojAktivnihDretvi + 1));
		this.serverGlavni = serverGlavni;
		this.nazivDretve = super.getName();
		this.konfig = konfig;
		this.veza = veza;
		this.ss = ss;
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
	 * @param komanda komanda
	 */
	public void obradaZahtjeva(OutputStreamWriter osw, String komanda) {
		if (provjeraSintakseObrada(komanda, prekid)) {
			ispisPoruke(osw, "OK");
			interrupt();
		} else if (provjeraSintakseObrada(komanda, status)) {
			int status;
			synchronized (this) {
				status = statusPosluzitelja;
			}
			ispisPoruke(osw, "OK " + status);
		} else if (provjeraSintakseObrada(komanda, inicijalizacija)) {
			synchronized (this) {
				statusPosluzitelja = 1;
			}
		} else if (provjeraSintakseObrada(komanda, ucitavanje)) {
			String aero = komanda.substring(5);
			int aeroUneseni;

			Gson gson = new Gson();
			JsonReader citac = new JsonReader(new StringReader(aero.trim()));
			citac.setLenient(true);

			synchronized (aerodromi) {
				aerodromi.addAll(gson.fromJson(citac, new TypeToken<List<Aerodrom>>() {
				}.getType()));

				aeroUneseni = aerodromi.size();
			}
			ispisPoruke(osw, "OK " + aeroUneseni);
		} else if (provjeraSintakseObrada(komanda, udaljenostIcao)) {
			izvrsiUdaljenostIcao(osw, komanda);
		} else if (provjeraSintakseObrada(komanda, ocisti)) {
			synchronized (this) {
				aerodromi.clear();
				statusPosluzitelja = 0;
			}
		} else {
			ispisPoruke(osw, "ERROR 14 Sintaksa komande nije uredu.");
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
		synchronized (this) {
			serverGlavni.radi = false;
			try {
				this.ss.close();
			} catch (IOException e) {
			}
		}
		super.interrupt();
	}

	/**
	 * Izvršavanje naredbe udaljenost icao.
	 *
	 * @param osw     izlazni tok podataka
	 * @param komanda komanda
	 */
	private void izvrsiUdaljenostIcao(OutputStreamWriter osw, String komanda) {
		String p[] = komanda.split(" ");
		String icao1 = p[1];
		String icao2 = p[2];

		String odgovor = "";

		Aerodrom aerodrom1 = null;
		Aerodrom aerodrom2 = null;

		synchronized (aerodromi) {
			aerodrom1 = aerodromi.stream().filter(a -> a.getIcao().equals(icao1)).findAny().orElse(null);
			aerodrom2 = aerodromi.stream().filter(a -> a.getIcao().equals(icao2)).findAny().orElse(null);
		}
		
		if (aerodrom1 == null && aerodrom2 == null) {
			ispisPoruke(osw, "ERROR 13 Ne postoje aerodromi " + icao1 + " i " + icao2 + " u kolekciji.");
		} else if (aerodrom1 == null) {
			ispisPoruke(osw, "ERROR 11 Ne postoji aerodrom " + icao1 + " u kolekciji.");
		} else if (aerodrom2 == null) {
			ispisPoruke(osw, "ERROR 12 Ne postoji aerodrom " + icao2 + " u kolekciji.");
		} else {
			double udaljenost = (double) udaljenost(Float.valueOf(aerodrom1.getLokacija().getLatitude()),
					Float.valueOf(aerodrom1.getLokacija().getLongitude()),
					Float.valueOf(aerodrom2.getLokacija().getLatitude()),
					Float.valueOf(aerodrom2.getLokacija().getLongitude()));

			odgovor = "OK " + (int) Math.round(udaljenost);

			ispisPoruke(osw, odgovor);
		}

	}

	/**
	 * Izračunava udaljenost između dvije koordinate.
	 *
	 * @param gs1 geografska širina prve lokacije
	 * @param gd1 geografska duljina prve lokacije
	 * @param gs2 geografska širina druge lokacije
	 * @param gd2 geograska duljina druge lokacije
	 * @return udaljenost
	 */
	private float udaljenost(float gs1, float gd1, float gs2, float gd2) {
		double polumjerZemlje = 6371000;
		double dGs = Math.toRadians(gs2 - gs1);
		double dGd = Math.toRadians(gd2 - gd1);
		double a = Math.sin(dGs / 2) * Math.sin(dGs / 2)
				+ Math.cos(Math.toRadians(gs1)) * Math.cos(Math.toRadians(gs2)) * Math.sin(dGd / 2) * Math.sin(dGd / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		float udalj = (float) (polumjerZemlje * c);
		udalj = udalj / 1000;

		return udalj;
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
