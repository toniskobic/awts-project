package org.foi.nwtis.tskobic.aplikacija_3.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

/**
 * Klasa AerodromiPraceniDAO za pristup bazi podataka.
 */
public class AerodromiPraceniDAO {

	/**
	 * Dohvaća praćene aerodrome.
	 *
	 * @param pbp postavke baze podataka
	 * @return lista praćenih aerodroma
	 */
	public List<Aerodrom> dohvatiPraceneAerodrome(PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT APR.ident AS icao, A.coordinates AS coordinates, A.iso_country AS country, "
				+ "A.name AS name FROM AERODROMI_PRACENI APR, airports A WHERE APR.ident = A.ident;";

		try {
			Class.forName(pbp.getDriverDatabase(url));

			List<Aerodrom> aerodromi = new ArrayList<>();

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					Statement s = con.createStatement();
					ResultSet rs = s.executeQuery(upit)) {

				while (rs.next()) {
					String drzava = rs.getString("country");
					String icao = rs.getString("icao");
					String koordinate[] = rs.getString("coordinates").split(", ");
					Lokacija lokacija = new Lokacija();
					lokacija.postavi(koordinate[0], koordinate[1]);
					String naziv = rs.getString("name");
					Aerodrom a = new Aerodrom(icao, naziv, drzava, lokacija);

					aerodromi.add(a);
				}
				return aerodromi;

			} catch (SQLException ex) {
				Logger.getLogger(AerodromiPraceniDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiPraceniDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
	
	/**
	 * Dohvaća praćeni aerodrom za proslijeđeni ICAO aerodroma.
	 *
	 * @param unos icao aerodroma
	 * @param pbp postavke baze podataka
	 * @return the aerodrom
	 */
	public Aerodrom dohvatiPraceniAerodrom(String unos, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT APR.ident AS icao, A.coordinates AS coordinates, A.iso_country AS country, "
				+ "A.name AS name FROM AERODROMI_PRACENI APR, airports A WHERE APR.ident = A.ident and APR.ident = ?;";

		try {
			Class.forName(pbp.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, unos);

				ResultSet rs = s.executeQuery();
				
				if(!rs.next()) {
					return null;
				} else {
					String drzava = rs.getString("country");
					String icao = rs.getString("icao");
					String koordinate[] = rs.getString("coordinates").split(", ");
					Lokacija lokacija = new Lokacija();
					lokacija.postavi(koordinate[1], koordinate[0]);
					String naziv = rs.getString("name");
					Aerodrom a = new Aerodrom(icao, naziv, drzava, lokacija);

					return a;
				}
			} catch (SQLException ex) {
				Logger.getLogger(AerodromiPraceniDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiPraceniDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return null;
	}
	

	/**
	 * Dodaje aerodrom za praćenje.
	 *
	 * @param icao icao
	 * @param pbp postavke baze podataka
	 * @return true, ako je uspješno dodavanje
	 */
	public boolean dodajAerodromZaPracenje(String icao, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "INSERT INTO AERODROMI_PRACENI (ident, `stored`) VALUES(?, ?);";

		try {
			Class.forName(pbp.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {
				
				Date datum = new Date();

				s.setString(1, icao);
				s.setTimestamp(2, new Timestamp(datum.getTime()));

				int brojAzuriranja = s.executeUpdate();

				return brojAzuriranja == 1;

			} catch (Exception ex) {
				Logger.getLogger(AerodromiPraceniDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiPraceniDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}
}
