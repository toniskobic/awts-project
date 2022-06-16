package org.foi.nwtis.tskobic.aplikacija_3.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

/**
 * Klasa AerodromiPolasciDAO za pristup bazi.
 */
public class AerodromiPolasciDAO {

	/**
	 * Dohvaća polaske sa određenog aerodroma na određeni dan.
	 *
	 * @param icao icao aerodroma
	 * @param dan dan
	 * @param pbp postavke baze podataka
	 * @return the list
	 */
	public List<AvionLeti> dohvatiPolaskeZaInterval(String icao, int vrijemeOd, int vrijemeDo, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT id, icao24, firstSeen, estDepartureAirport, lastSeen, estArrivalAirport, "
				+ "callsign, estDepartureAirportHorizDistance, "
				+ "estDepartureAirportVertDistance, estArrivalAirportHorizDistance, "
				+ "estArrivalAirportVertDistance, departureAirportCandidatesCount, arrivalAirportCandidatesCount, `stored` "
				+ "FROM AERODROMI_POLASCI WHERE estDepartureAirport = ? AND firstSeen > ? AND firstSeen < ? "
				+ "ORDER BY firstSeen ASC;";

		try {
			Class.forName(pbp.getDriverDatabase(url));

			List<AvionLeti> aerodromPolasci = new ArrayList<>();

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit);) {

				s.setString(1, icao);
				s.setInt(2, vrijemeOd);
				s.setInt(3, vrijemeDo);

				ResultSet rs = s.executeQuery();

				while (rs.next()) {
					String icao24 = rs.getString("icao24");
					int firstSeen = rs.getInt("firstSeen");
					String estDepartureAirport = rs.getString("estDepartureAirport");
					int lastSeen = rs.getInt("lastSeen");
					String estArrivalAirport = rs.getString("estArrivalAirport");
					String callsign = rs.getString("callsign");
					int estDepartureAirportHorizDistance = rs.getInt("estDepartureAirportHorizDistance");
					int estDepartureAirportVertDistance = rs.getInt("estDepartureAirportVertDistance");
					int estArrivalAirportHorizDistance = rs.getInt("estArrivalAirportHorizDistance");
					int estArrivalAirportVertDistance = rs.getInt("estArrivalAirportVertDistance");
					int departureAirportCandidatesCount = rs.getInt("departureAirportCandidatesCount");
					int arrivalAirportCandidatesCount = rs.getInt("arrivalAirportCandidatesCount");

					AvionLeti avionLeti = new AvionLeti(icao24, firstSeen, estDepartureAirport, lastSeen,
							estArrivalAirport, callsign, estDepartureAirportHorizDistance,
							estDepartureAirportVertDistance, estArrivalAirportHorizDistance,
							estArrivalAirportVertDistance, departureAirportCandidatesCount,
							arrivalAirportCandidatesCount);

					aerodromPolasci.add(avionLeti);
				}
				return aerodromPolasci;

			} catch (Exception ex) {
				Logger.getLogger(AerodromiPolasciDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiPolasciDAO.class.getName()).log(Level.SEVERE, null, ex);
		}

		return null;
	}
}
