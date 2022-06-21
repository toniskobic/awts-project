package org.foi.nwtis.tskobic.aplikacija_2.dretve;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.tskobic.aplikacija_2.podaci.AerodromiDolasciDAO;
import org.foi.nwtis.tskobic.aplikacija_2.podaci.AerodromiPolasciDAO;
import org.foi.nwtis.tskobic.aplikacija_2.podaci.AerodromiPraceniDAO;
import org.foi.nwtis.tskobic.aplikacija_2.podaci.AerodromiProblemiDAO;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.servlet.ServletContext;

/**
 * Dretva PreuzimanjeRasporedaAerodroma koja dobavlja podatke o polijetanju i
 * slijetanju aviona.
 */
public class PreuzimanjeRasporedaAerodroma extends Thread {

	boolean kraj = false;

	int preuzimanjeOdmak;

	int preuzimanjePauza;

	/** datum od kojeg se počinje preuzimanje. */
	long preuzimanjeOd;

	/** datum do kojeg se preuzimaju podaci. */
	long preuzimanjeDo;
	
	/** vrijeme obrade. */
	long vrijemeObrade;

	/** vrijeme za koje se preuzimaju podaci u jednom ciklusu */
	int preuzimanjeVrijeme;

	long trenutniDatum;

	long trenutniDatumKorekcija;
	
	int ciklusVrijeme;

	int ciklusKorekcija;

	/** aerodromi praceni DAO. */
	AerodromiPraceniDAO aerodromiPraceniDAO;

	/** aerodromi polasci DAO. */
	AerodromiPolasciDAO aerodromiPolasciDAO;

	/** aerodromi dolasci DAO. */
	AerodromiDolasciDAO aerodromiDolasciDAO;

	/** aerodromi problemi DAO. */
	AerodromiProblemiDAO aerodromiProblemiDAO;

	/** korisnik. */
	String korisnik;

	/** lozinka. */
	String lozinka;

	/** OpenSkyNewtork klijent. */
	OSKlijent oSKlijent;

	/** kontekst servleta. */
	ServletContext context;

	/** postavke baze podataka. */
	PostavkeBazaPodataka konfig;

	/**
	 * Konstruktor dretve
	 *
	 * @param context kontekst servleta
	 */
	public PreuzimanjeRasporedaAerodroma(ServletContext context) {
		this.context = context;
		this.konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
	}

	/**
	 * Metoda za pokretanje dretve
	 */
	@Override
	public synchronized void start() {
		preuzimanjeOd = izvrsiDatumPretvaranje(konfig.dajPostavku("preuzimanje.od"));
		preuzimanjeDo = izvrsiDatumPretvaranje(konfig.dajPostavku("preuzimanje.do"));
		preuzimanjeOdmak = Integer.parseInt(konfig.dajPostavku("preuzimanje.odmak"));
		preuzimanjeVrijeme = Integer.parseInt(konfig.dajPostavku("preuzimanje.vrijeme"));
		preuzimanjePauza = Integer.parseInt(konfig.dajPostavku("preuzimanje.pauza"));
		ciklusVrijeme = Integer.parseInt(konfig.dajPostavku("ciklus.vrijeme")) * 1000;
		ciklusKorekcija = Integer.parseInt(konfig.dajPostavku("ciklus.korekcija"));
		vrijemeObrade = preuzimanjeOd;

		aerodromiPraceniDAO = new AerodromiPraceniDAO();
		aerodromiPolasciDAO = new AerodromiPolasciDAO();
		aerodromiDolasciDAO = new AerodromiDolasciDAO();
		aerodromiProblemiDAO = new AerodromiProblemiDAO();

		korisnik = konfig.dajPostavku("OpenSkyNetwork.korisnik");
		lozinka = konfig.dajPostavku("OpenSkyNetwork.lozinka");

		oSKlijent = new OSKlijent(korisnik, lozinka);

		super.start();
	}

	/**
	 * Glavna metoda za rad dretve
	 */
	@Override
	public void run() {
		long vrijemeObradeDo = 0;
		long efektivnoVrijeme = 0;
		int ciklus = 0;
		int virtualniCiklus = 0;
		long pocetak;
		int ukupniRadISpavanje = 0;
		long vrijemeSpavanja = 0;
		int broj = 0;
		
		trenutniDatum = System.currentTimeMillis();
		trenutniDatumKorekcija = trenutniDatum - (86400 * 1000 * preuzimanjeOdmak);

		while (!kraj && vrijemeObrade < preuzimanjeDo) {
			List<Aerodrom> aerodromi = aerodromiPraceniDAO.dohvatiPraceneAerodrome(konfig);

			ciklus++;
			pocetak = System.currentTimeMillis();

			vrijemeObradeDo = vrijemeObrade + (preuzimanjeVrijeme * 3600 * 1000);
			if (vrijemeObradeDo > trenutniDatumKorekcija) {
				trenutniDatumKorekcija += 86400 * 1000;
				virtualniCiklus += (86400 * 1000) / ciklusVrijeme;
				vrijemeSpavanja = 86400 * 1000;
			} else {
				for (Aerodrom aerodrom : aerodromi) {
					List<AvionLeti> avioniPolasci;

					try {
						avioniPolasci = oSKlijent.getDepartures(aerodrom.getIcao(), vrijemeObrade / 1000,
								vrijemeObradeDo / 1000);
						if (avioniPolasci != null) {
							System.out.println("Broj letova: " + avioniPolasci.size());

							try (Connection con = otvoriVezuBP()) {

								for (AvionLeti a : avioniPolasci) {
									System.out.println(
											"Avion: " + a.getIcao24() + " Odredište: " + a.getEstArrivalAirport());
									if (a.getEstArrivalAirport() != null) {
										aerodromiPolasciDAO.dodajAerodromPolasci(a, konfig, con);
									}
								}

							} catch (Exception ex) {
								Logger.getLogger(AerodromiPolasciDAO.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
					} catch (NwtisRestIznimka e) {
						aerodromiProblemiDAO.dodajProblem(aerodrom.getIcao(), "Polasci: " + e.getMessage(), konfig);
						e.printStackTrace();
					}

					System.out.println("Dolasci na aerodrom: " + aerodrom.getIcao());

					List<AvionLeti> avioniDolasci;

					try {
						avioniDolasci = oSKlijent.getArrivals(aerodrom.getIcao(), vrijemeObrade / 1000,
								vrijemeObradeDo / 1000);

						if (avioniDolasci != null) {
							System.out.println("Broj letova: " + avioniDolasci.size());

							try (Connection con = otvoriVezuBP()) {

								for (AvionLeti a : avioniDolasci) {
									System.out.println(
											"Avion: " + a.getIcao24() + " Polazište: " + a.getEstDepartureAirport());
									if (a.getEstDepartureAirport() != null) {
										aerodromiDolasciDAO.dodajAerodromDolasci(a, konfig, con);
									}
								}

							} catch (Exception ex) {
								Logger.getLogger(AerodromiDolasciDAO.class.getName()).log(Level.SEVERE, null, ex);
							}
						}
					} catch (NwtisRestIznimka e) {
						aerodromiProblemiDAO.dodajProblem(aerodrom.getIcao(), "Dolasci: " + e.getMessage(), konfig);
						e.printStackTrace();
					}
					try {
						Thread.sleep(preuzimanjePauza);
					} catch (IllegalArgumentException | InterruptedException e) {
					}
				}

				efektivnoVrijeme = System.currentTimeMillis() - pocetak;
				ukupniRadISpavanje += efektivnoVrijeme;
				broj = izracunajCiklus(efektivnoVrijeme);

				virtualniCiklus += broj;

				vrijemeSpavanja = ciklus != 0 && ciklus % ciklusKorekcija == 0
						? virtualniCiklus * ciklusVrijeme - ukupniRadISpavanje
						: broj * ciklusVrijeme - efektivnoVrijeme;

			}

			if (kraj) {
				break;
			}

			try {
				Thread.sleep(vrijemeSpavanja);
			} catch (IllegalArgumentException | InterruptedException e) {
			}

			this.vrijemeObrade = vrijemeObradeDo;
		}
	}

	/**
	 * Metoda za prekidanje rada dretve.
	 */
	@Override
	public void interrupt() {
		kraj = true;
		super.interrupt();
	}

	/**
	 * Pretvara datum iz stringa u sekunde
	 *
	 * @param datum datum
	 * @return the long
	 */
	public long izvrsiDatumPretvaranje(String datum) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Date date = null;
		try {
			date = sdf.parse(datum);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long milisekunde = date.getTime();

		return milisekunde;
	}

	public int izracunajCiklus(long efektivnoVrijeme) {
		int i = 1;

		while (efektivnoVrijeme > (ciklusVrijeme * i)) {
			i++;
		}

		return i;
	}

	public Connection otvoriVezuBP() throws SQLException {
		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpkorisnik = konfig.getUserUsername();
		String bplozinka = konfig.getUserPassword();

		Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);

		return con;
	}
}
