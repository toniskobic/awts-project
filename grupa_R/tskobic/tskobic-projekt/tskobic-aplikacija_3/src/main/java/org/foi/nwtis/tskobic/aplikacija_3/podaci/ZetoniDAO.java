package org.foi.nwtis.tskobic.aplikacija_3.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

/**
 * Klasa ZetoniDAO za rad s bazom podataka.
 */
public class ZetoniDAO {

	/**
	 * Kreira žeton.
	 *
	 * @param korisnik korisnik
	 * @param pbp postavke baza podataka
	 * @return žeton
	 */
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
	
	/**
	 * Dohvaća aktivne žetone.
	 *
	 * @param unos proslijeđeni korisnik
	 * @param pbp postavke baza podataka
	 * @return lista žeton
	 */
	public List<Zeton> dohvatiAktivneZetone(String unos, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT * FROM zeton where korisnik = ? AND status = 1;";
		
		try {
			Class.forName(pbp.getDriverDatabase(url));
			
            List<Zeton> aktivniZetoni = new ArrayList<>();		

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, unos);

				ResultSet rs = s.executeQuery();

				while (rs.next()) {
					String vrijemeIsteka = rs.getString("vrijemeIsteka");
					long vrijeme = izvrsiDatumPretvaranje(vrijemeIsteka);
			    	long trenutnoVrijeme = new Date().getTime();
					
					if (trenutnoVrijeme <= vrijeme) {
						int id = rs.getInt("id");
						String korisnik = rs.getString("korisnik");
						int status = rs.getInt("status");
						String vrijemeKreiranja = rs.getString("vrijemeKreiranja");
						Zeton zeton = new Zeton(id, korisnik, status, vrijemeKreiranja, vrijemeIsteka);
						
						aktivniZetoni.add(zeton);
					}
				} 
				
				return aktivniZetoni;				
			} catch (SQLException ex) {
				Logger.getLogger(ZetoniDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ZetoniDAO.class.getName()).log(Level.SEVERE, null, ex);
		}

		return null;
	}

	/**
	 * Dohvaća žeton.
	 *
	 * @param unos unos
	 * @param pbp postavke baza podataka
	 * @return žeton
	 */
	public Zeton dohvatiZeton(String unos, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT * FROM zeton WHERE id = ?;";

		try {
			Class.forName(pbp.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, unos);

				ResultSet rs = s.executeQuery();

				if (!rs.next()) {
					return null;
				} else {
					int id = rs.getInt("id");
					String korisnik = rs.getString("korisnik");
					int status = rs.getInt("status");
					String vrijemeKreiranja = rs.getString("vrijemeKreiranja");
					String vrijemeIsteka = rs.getString("vrijemeIsteka");
					Zeton zeton = new Zeton(id, korisnik, status, vrijemeKreiranja, vrijemeIsteka);

					return zeton;
				}
			} catch (SQLException ex) {
				Logger.getLogger(ZetoniDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ZetoniDAO.class.getName()).log(Level.SEVERE, null, ex);
		}

		return null;
	}
	
	/**
	 * Mijenja status žetona.
	 *
	 * @param unos unos
	 * @param token žeton
	 * @param pbp postavke baza podataka
	 * @return true, ako je uspješno
	 */
	public boolean promijeniStatusZetona(int unos, String token, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "UPDATE zeton SET status = ? WHERE id = ?;";
		
		try {
			Class.forName(pbp.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setInt(1, unos);
				s.setString(2, token);

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
	
	/**
	 * Mijenja status žetona.
	 *
	 * @param unos unos
	 * @param token žeton
	 * @param pbp postavke baza podataka
	 * @param con veza na bazu podataka
	 * @return true, ako je uspješno
	 */
	public boolean promijeniStatusZetona(int unos, String token, PostavkeBazaPodataka pbp, Connection con) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String upit = "UPDATE zeton SET status = ? WHERE id = ?;";
		
		try {
			Class.forName(pbp.getDriverDatabase(url));

			try (PreparedStatement s = con.prepareStatement(upit)) {

				s.setInt(1, unos);
				s.setString(2, token);

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
	
	
	/**
	 * Izvršava pretvaranje datuma.
	 *
	 * @param datum datum
	 * @return milisekunde
	 */
	private long izvrsiDatumPretvaranje(String datum) {
	    long milisekunde = LocalDateTime.parse(datum, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
	            .atZone(ZoneId.systemDefault())
	            .toInstant()
	            .toEpochMilli();
	    
	    return milisekunde;
	}

}
