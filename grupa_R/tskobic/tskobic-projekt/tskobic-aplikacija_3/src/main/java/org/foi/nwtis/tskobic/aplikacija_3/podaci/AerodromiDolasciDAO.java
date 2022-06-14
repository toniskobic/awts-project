package org.foi.nwtis.tskobic.aplikacija_3.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

/**
 * Klasa AerodromiDolasciDAO za pristup bazi.
 */
public class AerodromiDolasciDAO {

	/**
	 * Dohvaća dolaske aviona na određeni aerodrom na određeni dan.
	 *
	 * @param icao icao aerodroma
	 * @param dan dan
	 * @param pbp postavke baze podataka
	 * @return the lista dolazaka
	 */
	public List<AvionLeti> dohvatiDolaskeNaDan(String icao, String dan, PostavkeBazaPodataka pbp) {
		String url = pbp.getServerDatabase() + pbp.getUserDatabase();
		String bpkorisnik = pbp.getUserUsername();
		String bplozinka = pbp.getUserPassword();
		String upit = "SELECT id, icao24, firstSeen, estDepartureAirport, lastSeen, estArrivalAirport, "
				+ "callsign, estDepartureAirportHorizDistance, "
				+ "estDepartureAirportVertDistance, estArrivalAirportHorizDistance, "
				+ "estArrivalAirportVertDistance, departureAirportCandidatesCount, arrivalAirportCandidatesCount, `stored` "
				+ "FROM AERODROMI_DOLASCI WHERE estArrivalAirport = ? AND lastSeen > ? AND lastSeen < ? "
				+ "ORDER BY lastSeen ASC;";

		try {
			Class.forName(pbp.getDriverDatabase(url));

			List<AvionLeti> aerodromPolasci = new ArrayList<>();

			try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
					PreparedStatement s = con.prepareStatement(upit);) {

				s.setString(1, icao);
				s.setString(2, String.valueOf(izvrsiDatumPretvaranje(dan)));
				s.setString(3, String.valueOf(izvrsiDatumPretvaranje(dan) + 86399));

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
				Logger.getLogger(AerodromiDolasciDAO.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(AerodromiDolasciDAO.class.getName()).log(Level.SEVERE, null, ex);
		}

		return null;
	}

	/**
	 * Izvršava pretvaranje datuma u sekunde.
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
		long sekunde = TimeUnit.MILLISECONDS.toSeconds(milisekunde);

		return sekunde;
	}
}
