package org.foi.nwtis.tskobic.aplikacija_1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;

import org.foi.nwtis.tskobic.vjezba_03.konfiguracije.Konfiguracija;
import org.foi.nwtis.tskobic.vjezba_03.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.tskobic.vjezba_03.konfiguracije.NeispravnaKonfiguracija;

/**
 * Glavna klasa poslužitelja ServerGlavni.
 */
public class ServerGlavni {

	/** broj porta. */
	int port;

	/** maksimalni broj čekača. */
	int maksCekaca;

	/** maksimalni broj čekanja. */
	int maksCekanje;

	/** veza. */
	Socket veza = null;

	boolean radi = true;

	/** konfiguracijski podaci. */
	public Konfiguracija konfig = null;

	/** iso format za datum. */
	static SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	/**
	 * Konstruktor klase.
	 *
	 * @param port       broj porta
	 * @param maksCekaca maksimalan broj čekača
	 */
	public ServerGlavni(int port, int maksCekaca, int maksCekanje) {
		super();
		this.port = port;
		this.maksCekaca = maksCekaca;
		this.maksCekanje = maksCekanje;
	}

	/**
	 * Učitavanje konfiguracijskih podataka.
	 *
	 * @param nazivDatoteke naziv datoteke konfiguracijskih podataka
	 */
	public void ucitavanjePodataka(String nazivDatoteke) {
		try {
			this.konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
		} catch (NeispravnaKonfiguracija e) {
			e.printStackTrace();
		}
	}

	/**
	 * Obrada zahtjeva.
	 */
	public void obradaZahtjeva() {

		try (ServerSocket ss = new ServerSocket(this.port, this.maksCekaca)) {
			ss.setSoTimeout(maksCekanje);
			while (radi) {
				if (!radi) {
					break;
				}
				this.veza = ss.accept();

				DretvaZahtjeva dretvaZahtjeva = new DretvaZahtjeva(this, konfig, veza, ss);
				dretvaZahtjeva.start();
			}
		} catch (IOException ex) {
		}
	}

	/**
	 * Glavna metoda.
	 *
	 * @param args argumenti
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Broj argumenata nije 1.");
			return;
		}
		ServerGlavni sg = new ServerGlavni(0, 0, 0);

		sg.ucitavanjePodataka(args[0]);

		if (sg.konfig == null) {
			System.out.println("Problem s konfiguracijom.");
			return;
		}

		int port = Integer.parseInt(sg.konfig.dajPostavku("port"));
		int maksCekaca = Integer.parseInt(sg.konfig.dajPostavku("maks.cekaca"));
		int maksCekanje = Integer.parseInt(sg.konfig.dajPostavku("maks.cekanje"));

		sg.port = port;
		sg.maksCekaca = maksCekaca;
		sg.maksCekanje = maksCekanje;

		sg.obradaZahtjeva();
	}
}
