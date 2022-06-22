package org.foi.nwtis.tskobic.aplikacija_2.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

/**
 * Klasa AerodromiProblemiDAO za pristup bazi podataka.
 */
public class AerodromiProblemiDAO {

	/**
	 * Dohvaća sve problema aerodroma.
	 *
	 * @param pbp postavke baze podataka
	 * @return lista problema aerodroma
	 */
	public List<AerodromProblem> dohvatiSveProbleme(PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT ident AS icao, description, `stored` FROM AERODROMI_PROBLEMI;";

		try {
			Class.forName(pbp.getDriverDatabase(url));

			List<AerodromProblem> aerodromiProblemi = new ArrayList<>();

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					Statement s = con.createStatement();
					ResultSet rs = s.executeQuery(upit)) {

				while (rs.next()) {
					String icao = rs.getString("icao");
					String opis = rs.getString("description");
					String vrijeme = rs.getString("stored");
					Date datum = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(vrijeme);
					AerodromProblem aerodromProblem = new AerodromProblem(icao, opis, datum);

					aerodromiProblemi.add(aerodromProblem);
				}
				return aerodromiProblemi;

			} catch (SQLException | ParseException ex) {
				Logger.getLogger(AerodromiProblemiDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiProblemiDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * Dodavanje problema za aerodrom.
	 *
	 * @param icao icao
	 * @param opis opis problema
	 * @param pbp postavke baze podataka
	 * @return true, ako je uspješno dodavanje
	 */
	public boolean dodajProblem(String icao, String opis, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "INSERT INTO AERODROMI_PROBLEMI (ident, description, `stored`) " + "VALUES(?, ?, ?);";

		try {
			Class.forName(pbp.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				Date datum = new Date();

				s.setString(1, icao);
				s.setString(2, opis);
				s.setTimestamp(3, new Timestamp(datum.getTime()));

				int brojAzuriranja = s.executeUpdate();

				return brojAzuriranja == 1;

			} catch (Exception ex) {
				Logger.getLogger(AerodromiProblemiDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiProblemiDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}
	
	/**
	 * Briše probleme za određeni aerodrom.
	 *
	 * @param icao icao
	 * @param pbp postavke baze podataka
	 * @return true, ako je uspješno brisanje
	 */
	public boolean izbrisiProblem(String icao, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "DELETE FROM AERODROMI_PROBLEMI WHERE ident=?;";

		try {
			Class.forName(pbp.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, icao);

				int brojAzuriranja = s.executeUpdate();

				return brojAzuriranja > 0;

			} catch (Exception ex) {
				Logger.getLogger(AerodromiProblemiDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiProblemiDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}
}
