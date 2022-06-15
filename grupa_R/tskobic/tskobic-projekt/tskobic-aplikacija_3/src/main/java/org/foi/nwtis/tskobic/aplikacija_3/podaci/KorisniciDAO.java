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

import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

public class KorisniciDAO {

	public Korisnik dohvatiKorisnika(String unos, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT * FROM korisnici WHERE korisnik = ?";
		
		try {
			Class.forName(pbp.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, unos);

				ResultSet rs = s.executeQuery();
				
				if(!rs.next()) {
					return null;
				} else {
					String korisnickoIme = rs.getString("korisnik");
					String ime = rs.getString("ime");
					String prezime = rs.getString("prezime");
					String lozinka = rs.getString("lozinka");
					String email = rs.getString("email");
					Korisnik k = new Korisnik(korisnickoIme, ime, prezime, lozinka, email);

					return k;
				}
			} catch (SQLException ex) {
				Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return null;
	}
	
	public List<Korisnik> dohvatiSveKorisnike(PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT * FROM korisnici;";

		try {
			Class.forName(pbp.getDriverDatabase(url));

			List<Korisnik> korisnici = new ArrayList<>();

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					Statement s = con.createStatement();
					ResultSet rs = s.executeQuery(upit)) {

				while (rs.next()) {
					String korisnickoIme = rs.getString("korisnik");
					String ime = rs.getString("ime");
					String prezime = rs.getString("prezime");
					String lozinka = rs.getString("lozinka");
					String email = rs.getString("email");
					Korisnik k = new Korisnik(korisnickoIme, ime, prezime, lozinka, email);

					korisnici.add(k);
				}
				return korisnici;

			} catch (SQLException ex) {
				Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
		}

		return null;
	}

	public boolean dodajKorisnika(Korisnik korisnik, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "INSERT INTO korisnici (korisnik, ime, prezime, lozinka, email) VALUES(?, ?, ?, ?, ?);";

		try {
			Class.forName(pbp.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, korisnik.getKorIme());
				s.setString(2, korisnik.getIme());
				s.setString(3, korisnik.getPrezime());
				s.setString(4, korisnik.getLozinka());
				s.setString(5, korisnik.getEmail());

				int brojAzuriranja = s.executeUpdate();

				return brojAzuriranja == 1;

			} catch (Exception ex) {
				Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		return false;
	}

	public boolean autentifikacijaKorisnika(String korisnik, String lozinka, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT * FROM korisnici where korisnik = ? and lozinka = ?";

		try {
			Class.forName(pbp.getDriverDatabase(url));

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, korisnik);
				s.setString(2, lozinka);

				ResultSet rs = s.executeQuery();

				if (rs.next()) {
					return true;
				} else {
					return false;
				}
			} catch (SQLException ex) {
				Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(KorisniciDAO.class.getName()).log(Level.SEVERE, null, ex);
		}

		return false;
	}

}
