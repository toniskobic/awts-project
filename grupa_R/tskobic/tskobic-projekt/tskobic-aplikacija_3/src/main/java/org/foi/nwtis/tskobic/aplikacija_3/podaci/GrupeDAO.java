package org.foi.nwtis.tskobic.aplikacija_3.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

public class GrupeDAO {

	public List<Grupa> dohvatiGrupeKorisnika(String unos, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT g.grupa, g.naziv FROM grupe g, uloge u WHERE g.grupa  = u.grupa AND u.korisnik = ?;";

		try {
			Class.forName(pbp.getDriverDatabase(url));

			List<Grupa> grupe = new ArrayList<>();

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit)) {

				s.setString(1, unos);

				ResultSet rs = s.executeQuery();

				while (rs.next()) {
					String grupa = rs.getString("grupa");
					String naziv = rs.getString("naziv");
				
					Grupa g = new Grupa(grupa, naziv);

					grupe.add(g);

				}

				return grupe;
			} catch (SQLException ex) {
				Logger.getLogger(ZetoniDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ZetoniDAO.class.getName()).log(Level.SEVERE, null, ex);
		}

		return null;
	}
}
