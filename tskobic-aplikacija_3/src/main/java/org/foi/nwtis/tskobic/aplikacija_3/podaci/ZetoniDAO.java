package org.foi.nwtis.tskobic.aplikacija_3.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

public class ZetoniDAO {

	public Zeton kreirajZeton(String korisnik, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "INSERT INTO zeton (korisnik, status, vrijemeKreiranja, vrijemeIsteka) " + "VALUES(?, ?, ?, ?);";

		try {
			Class.forName(pbp.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit, Statement.RETURN_GENERATED_KEYS)) {

				int trajanjeZetonaSati = Integer.parseInt(pbp.dajPostavku("zeton.trajanje"));
				LocalDateTime vrijemeKreiranja = LocalDateTime.now();
				LocalDateTime vrijemeIsteka = vrijemeKreiranja.plusHours(trajanjeZetonaSati);

				s.setString(1, korisnik);
				s.setInt(2, 1);
				s.setString(3, vrijemeKreiranja.toString());
				s.setString(4, vrijemeIsteka.toString());

				s.executeUpdate();
				ResultSet rs = s.getGeneratedKeys();

				if (rs.next()) {
					return new Zeton(rs.getInt(1), korisnik, 1, vrijemeKreiranja.toString(), vrijemeIsteka.toString());
				}

				return null;

			} catch (Exception ex) {
				Logger.getLogger(ZetoniDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ZetoniDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public boolean provjeriVlasnistvoZetona(String zeton, String korisnik, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT korisnik FROM zeton WHERE id = ? and korisnik = ?;";

		try {
			Class.forName(pbp.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, zeton);
				s.setString(2, korisnik);

				ResultSet rs = s.executeQuery();

				if (rs.next()) {
					return true;
				} else {
					return false;
				}
			} catch (SQLException ex) {
				Logger.getLogger(ZetoniDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ZetoniDAO.class.getName()).log(Level.SEVERE, null, ex);
		}

		return false;
	}

	public boolean provjeriVrijemeZetona(String zeton, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT vrijemeIsteka FROM zeton WHERE id = ?;";

		try {
			Class.forName(pbp.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, zeton);

				ResultSet rs = s.executeQuery();

				if (rs.next()) {
					String vrijemeIsteka = rs.getString(1);
					long vrijeme = izvrsiDatumPretvaranje(vrijemeIsteka);
					long trenutnoVrijeme = new Date().getTime();

					if (trenutnoVrijeme <= vrijeme) {
						return true;
					} else {
						return false;
					}

				} else {
					return false;
				}
			} catch (SQLException ex) {
				Logger.getLogger(ZetoniDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ZetoniDAO.class.getName()).log(Level.SEVERE, null, ex);
		}

		return false;
	}
	
	public boolean provjeriAktivnostZetona(String zeton, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT status, vrijemeIsteka FROM zeton WHERE id = ?;";

		try {
			Class.forName(pbp.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, zeton);

				ResultSet rs = s.executeQuery();

				if (rs.next()) {
					int status = rs.getInt(1);
					String vrijemeIsteka = rs.getString(2);
					long vrijeme = izvrsiDatumPretvaranje(vrijemeIsteka);
					long trenutnoVrijeme = new Date().getTime();

					if (status == 1 && trenutnoVrijeme <= vrijeme) {
						return true;
					} else {
						return false;
					}

				} else {
					return false;
				}
			} catch (SQLException ex) {
				Logger.getLogger(ZetoniDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ZetoniDAO.class.getName()).log(Level.SEVERE, null, ex);
		}

		return false;
	}

	public long izvrsiDatumPretvaranje(String datum) {
	    long millisSinceEpoch = LocalDateTime.parse(datum, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
	            .atZone(ZoneId.systemDefault())
	            .toInstant()
	            .toEpochMilli();
	    
	    return millisSinceEpoch;
	}
}
