package org.foi.nwtis.tskobic.aplikacija_1;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Klasa za klijenta KorisnikGlavni.
 */
public class KorisnikGlavni {

	/** broj porta. */
	int port;

	/** maksimalno čekanje na odgovor poslužitelja. */
	int cekanje;

	/** komanda. */
	String komanda;

	/** adresa. */
	String adresa;

	/**
	 * Slanje komande poslužitelju.
	 *
	 * @param adresa  adresa
	 * @param port    broj porta
	 * @param cekanje maksimalno čekanje na odgovor poslužitelja
	 * @param komanda komanda
	 * @return the string
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
			ispis(e.getMessage());
		} finally {
			try {
				isr.close();
				osw.close();
			} catch (IOException e) {
				ispis(e.getMessage());
			}
		}

		return null;
	}

	/**
	 * Ispisivanje poruke na konzolu.
	 *
	 * @param message poruka
	 */
	private void ispis(String message) {
		System.out.println(message);
	}

	/**
	 * Glavna metoda
	 *
	 * @param args argumenti
	 */
	public static void main(String[] args) {

		KorisnikGlavni kg = new KorisnikGlavni();

		if (args.length == 1) {
			kg.komanda = args[0];
		} else if (args.length == 2) {
			kg.komanda = args[0] + " " + args[1];
		} else if (args.length == 3) {
			kg.komanda = args[0] + " " + args[1] + " " + args[2];
		}

		kg.adresa = "localhost";

		kg.port = 8003;
		kg.cekanje = 1800000;

		String odgovor = kg.posaljiKomandu(kg.adresa, kg.port, kg.cekanje, kg.komanda);
		kg.ispis(odgovor);
	}
}
