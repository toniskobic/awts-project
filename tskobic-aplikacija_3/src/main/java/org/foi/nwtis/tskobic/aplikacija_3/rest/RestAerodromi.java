//package org.foi.nwtis.tskobic.aplikacija_3.rest;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
//import org.foi.nwtis.podaci.Aerodrom;
//import org.foi.nwtis.rest.podaci.AvionLeti;
//import org.foi.nwtis.tskobic.aplikacija_3.podaci.AerodromiDAO;
//import org.foi.nwtis.tskobic.aplikacija_3.podaci.AerodromiDolasciDAO;
//import org.foi.nwtis.tskobic.aplikacija_3.podaci.AerodromiPolasciDAO;
//import org.foi.nwtis.tskobic.aplikacija_3.podaci.AerodromiPraceniDAO;
//import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
//
//import com.google.gson.Gson;
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//
//import jakarta.servlet.ServletContext;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.ws.rs.Consumes;
//import jakarta.ws.rs.GET;
//import jakarta.ws.rs.POST;
//import jakarta.ws.rs.Path;
//import jakarta.ws.rs.PathParam;
//import jakarta.ws.rs.Produces;
//import jakarta.ws.rs.QueryParam;
//import jakarta.ws.rs.core.Context;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//
///**
// * Klasa RestAerodromi za URI putanju aerodromi.
// */
//@Path("aerodromi")
//public class RestAerodromi {
//
//	/** Kontekst servleta. */
//	@Context
//	private ServletContext context;
//
//	/**
//	 * Daje sve aerodrome.
//	 *
//	 * @param request http servlet zahtjev
//	 * @return the odgovor
//	 */
//	@GET
//	@Produces({ MediaType.APPLICATION_JSON })
//	public Response dajSveAerodrome(@Context HttpServletRequest request) {
//		boolean parametar = request.getParameterMap().containsKey("preuzimanje");
//		Response odgovor = null;
//		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
//		List<Aerodrom> aerodromi = null;
//		if (parametar) {
//			AerodromiPraceniDAO aerodromiPraceniDAO = new AerodromiPraceniDAO();
//			aerodromi = aerodromiPraceniDAO.dohvatiPraceneAerodrome(konfig);
//		} else {
//			AerodromiDAO aerodromiDAO = new AerodromiDAO();
//			aerodromi = aerodromiDAO.dohvatiSveAerodrome(konfig);
//		}
//
//		if (aerodromi != null) {
//			odgovor = Response.status(Response.Status.OK).entity(aerodromi).build();
//		} else {
//			odgovor = Response.status(Response.Status.NOT_FOUND).entity("Nema aerodroma.").build();
//		}
//
//		return odgovor;
//	}
//
//	/**
//	 * Dodaje aerodrom za praćenje.
//	 *
//	 * @param sadrzaj icao aerodroma koji se dodaje
//	 * @return the odgovor
//	 */
//	@POST
//	@Produces({ MediaType.APPLICATION_JSON })
//	@Consumes({ MediaType.APPLICATION_JSON })
//	public Response dodajAerodromZaPratiti(String sadrzaj) {
//		Response odgovor = null;
//
//		Gson gson = new Gson();
//		JsonElement element = gson.fromJson(sadrzaj, JsonElement.class);
//		JsonObject jsonObj = element.getAsJsonObject();
//		String icao = jsonObj.get("icao").getAsString();
//
//		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
//		AerodromiDAO aerodromiDAO = new AerodromiDAO();
//		AerodromiPraceniDAO aerodromiPraceniDAO = new AerodromiPraceniDAO();
//		List<Aerodrom> aerodromi = aerodromiDAO.dohvatiSveAerodrome(konfig);
//		List<Aerodrom> praceniAerodromi = aerodromiPraceniDAO.dohvatiPraceneAerodrome(konfig);
//
//		boolean status;
//		String poruka = "";
//
//		List<Aerodrom> fAerodromi = aerodromi.stream().filter(x -> x.getIcao().equals(icao))
//				.collect(Collectors.toList());
//		if (fAerodromi.isEmpty()) {
//			status = false;
//			poruka = "Uneseni aerodrom ne postoji.";
//		} else {
//			List<Aerodrom> fPraceniAerodromi = praceniAerodromi.stream().filter(x -> x.getIcao().equals(icao))
//					.collect(Collectors.toList());
//			if (!fPraceniAerodromi.isEmpty()) {
//				status = false;
//				poruka = "Uneseni aerodrom se već prati.";
//			} else {
//				status = aerodromiPraceniDAO.dodajAerodromZaPracenje(icao, konfig);
//				poruka = status ? "Aerodrom za praćenje dodan." : "Aerodrom za praćenje nije uspješno dodan.";
//			}
//		}
//
//		if (status) {
//			odgovor = Response.status(201).entity(poruka).build();
//		} else {
//			odgovor = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(poruka)
//					.build();
//		}
//
//		return odgovor;
//	}
//
//	/**
//	 * Vraća aerodrom.
//	 *
//	 * @param icao icao aerodroma
//	 * @return the odgovor
//	 */
//	@GET
//	@Produces({ MediaType.APPLICATION_JSON })
//	@Path("{icao}")
//	public Response dajAerodrom(@PathParam("icao") String icao) {
//		Response odgovor = null;
//
//		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
//		AerodromiDAO aerodromiDAO = new AerodromiDAO();
//		List<Aerodrom> aerodromi = aerodromiDAO.dohvatiSveAerodrome(konfig);
//
//		Aerodrom aerodrom = null;
//		for (Aerodrom a : aerodromi) {
//			if (a.getIcao().compareTo(icao) == 0) {
//				aerodrom = a;
//				break;
//			}
//		}
//
//		if (aerodrom != null) {
//			odgovor = Response.status(Response.Status.OK).entity(aerodrom).build();
//		} else {
//			odgovor = Response.status(Response.Status.NOT_FOUND).entity("Nema aerodroma: " + icao).build();
//		}
//
//		return odgovor;
//	}
//
//	/**
//	 * Vraća polaske aerodoma na određeni dan.
//	 *
//	 * @param icao icao aerodroma
//	 * @param dan dan
//	 * @return the odgovor
//	 */
//	@GET
//	@Produces({ MediaType.APPLICATION_JSON })
//	@Path("{icao}/polasci")
//	public Response dajPolaskeAerodoma(@PathParam("icao") String icao, @QueryParam("dan") String dan) {
//		Response odgovor = null;
//
//		long datum = izvrsiDatumPretvaranje(dan);
//		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
//		AerodromiPolasciDAO aerodromiPolasciDAO = new AerodromiPolasciDAO();
//		List<AvionLeti> aerodromiPolasci = aerodromiPolasciDAO.dohvatiSvePolaske(konfig);
//
//		Predicate<AvionLeti> lambda1 = x -> x.getEstDepartureAirport().equals(icao);
//		Predicate<AvionLeti> lambda2 = x -> x.getFirstSeen() > datum;
//		Predicate<AvionLeti> lambda3 = x -> x.getFirstSeen() < datum + 86399;
//
//		List<AvionLeti> fAvioniDolasci = aerodromiPolasci.stream()
//				.filter(lambda1.and(lambda2).and(lambda3)).collect(Collectors.toList());
//
//		if (!fAvioniDolasci.isEmpty()) {
//			odgovor = Response.status(Response.Status.OK).entity(fAvioniDolasci).build();
//		} else {
//			odgovor = Response.status(Response.Status.NOT_FOUND).entity("Nema polazaka na taj dan za aerodrom: " + icao)
//					.build();
//		}
//
//		return odgovor;
//	}
//
//	/**
//	 * Vraća dolaske aerodoma na određeni dan.
//	 *
//	 * @param icao icao aerodroma
//	 * @param dan dan
//	 * @return the odgovor
//	 */
//	@GET
//	@Produces({ MediaType.APPLICATION_JSON })
//	@Path("{icao}/dolasci")
//	public Response dajDolaskeAerodoma(@PathParam("icao") String icao, @QueryParam("dan") String dan) {
//		Response odgovor = null;
//
//		long datum = izvrsiDatumPretvaranje(dan);
//		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
//		AerodromiDolasciDAO aerodromiDolasciDAO = new AerodromiDolasciDAO();
//		List<AvionLeti> aerodromiPolasci = aerodromiDolasciDAO.dohvatiSveDolaske(konfig);
//
//		Predicate<AvionLeti> lambda1 = x -> x.getEstArrivalAirport().equals(icao);
//		Predicate<AvionLeti> lambda2 = x -> x.getLastSeen() > datum;
//		Predicate<AvionLeti> lambda3 = x -> x.getLastSeen() < datum + 86399;
//
//		List<AvionLeti> fAvioniDolasci = aerodromiPolasci.stream()
//				.filter(lambda1.and(lambda2).and(lambda3)).collect(Collectors.toList());
//
//		if (!fAvioniDolasci.isEmpty()) {
//			odgovor = Response.status(Response.Status.OK).entity(fAvioniDolasci).build();
//		} else {
//			odgovor = Response.status(Response.Status.NOT_FOUND).entity("Nema dolazaka na taj dan za aerodrom: " + icao)
//					.build();
//		}
//
//		return odgovor;
//	}
//
//	/**
//	 * Pretvara datum iz stringa u sekunde.
//	 *
//	 * @param datum datum
//	 * @return the long
//	 */
//	public long izvrsiDatumPretvaranje(String datum) {
//		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
//		Date date = null;
//		try {
//			date = sdf.parse(datum);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		long milisekunde = date.getTime();
//		long sekunde = TimeUnit.MILLISECONDS.toSeconds(milisekunde);
//
//		return sekunde;
//	}
//}
