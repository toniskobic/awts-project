package org.foi.nwtis.tskobic.aplikacija_3.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

/**
 * Klasa AerodromiDAO za pristup bazi.
 */
public class AerodromiDAO {

	/**
	 * Dohvaća sve aerodrome.
	 *
	 * @param pbp postavke baze podataka
	 * @return lista aerodroma
	 */
	public List<Aerodrom> dohvatiSveAerodrome(PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT iso_country AS country, ident AS icao, coordinates, name FROM airports;";

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
					lokacija.postavi(koordinate[1], koordinate[0]);
					String naziv = rs.getString("name");
					Aerodrom a = new Aerodrom(icao, naziv, drzava, lokacija);

					aerodromi.add(a);
				}
				return aerodromi;

			} catch (SQLException ex) {
				Logger.getLogger(AerodromiDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return null;
	}

	/**
	 * Dohvaća aerodrom za proslijeđeni ICAO aerodroma.
	 *
	 * @param unos icao aerodroma
	 * @param pbp postavke baze podataka
	 * @return aerodrom
	 */
	public Aerodrom dohvatiAerodrom(String unos, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT iso_country AS country, ident AS icao, coordinates, name FROM airports WHERE ident = ?;";

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
				Logger.getLogger(AerodromiDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return null;
	}
}
