package org.foi.nwtis.tskobic.aplikacija_3.rest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.AerodromiDAO;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.AerodromiDolasciDAO;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.AerodromiPolasciDAO;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.AerodromiPraceniDAO;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.Udaljenost;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.Zeton;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.ZetoniDAO;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Klasa RestAerodromi za URI putanju aerodromi.
 */
@Path("aerodromi")
public class RestAerodromi {

	/** Kontekst servleta. */
	@Context
	private ServletContext context;

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dajSveAerodrome(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String token) {
		Response odgovor = null;

		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
		List<Aerodrom> aerodromi = null;

		ZetoniDAO zetoniDAO = new ZetoniDAO();
		Zeton zeton = zetoniDAO.dohvatiZeton(token, konfig);

		odgovor = provjeriZeton(zeton, korisnik);
		if (odgovor == null) {
			AerodromiDAO aerodromiDAO = new AerodromiDAO();
			aerodromi = aerodromiDAO.dohvatiSveAerodrome(konfig);

			odgovor = aerodromi != null ? Response.status(Response.Status.OK).entity(aerodromi).build()
					: Response.status(Response.Status.NOT_FOUND).entity("Neuspješno dohvaćanje aerodroma.").build();
		}

		return odgovor;
	}

	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response dodajAerodromZaPratiti(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String token,
			String sadrzaj) {
		Response odgovor = null;

		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");

		ZetoniDAO zetoniDAO = new ZetoniDAO();
		Zeton zeton = zetoniDAO.dohvatiZeton(token, konfig);

		odgovor = provjeriZeton(zeton, korisnik);
		if (odgovor == null) {
			Aerodrom a = pretvoriJSON(sadrzaj);

			if (a == null) {
				odgovor = Response.status(400).entity("Proslijeđeni JSON format nije ispravan.").build();
			} else {
				AerodromiDAO aerodromiDAO = new AerodromiDAO();
				AerodromiPraceniDAO aerodromiPraceniDAO = new AerodromiPraceniDAO();
				Aerodrom dohvaceniAerodrom = aerodromiDAO.dohvatiAerodrom(a.getIcao(), konfig);

				if (dohvaceniAerodrom == null) {
					odgovor = Response.status(Response.Status.NOT_FOUND).entity("Uneseni aerodrom ne postoji.").build();
				} else {
					Aerodrom praceniAerodrom = aerodromiPraceniDAO.dohvatiPraceniAerodrom(a.getIcao(), konfig);
					if (praceniAerodrom != null) {
						odgovor = Response.status(500).entity("Uneseni aerodrom se već prati.").build();
					} else {
						boolean status = aerodromiPraceniDAO.dodajAerodromZaPracenje(a.getIcao(), konfig);
						odgovor = status
								? Response.status(Response.Status.OK).entity("Aerodrom za praćenje dodan").build()
								: Response.status(500).entity("Aerodrom za praćenje nije dodan.").build();
					}
				}
			}
		}

		return odgovor;
	}

	/**
	 * Vraća aerodrom.
	 *
	 * @param icao icao aerodroma
	 * @return the odgovor
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao}")
	public Response dajAerodrom(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String token,
			@PathParam("icao") String icao) {
		Response odgovor = null;

		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");

		ZetoniDAO zetoniDAO = new ZetoniDAO();
		Zeton zeton = zetoniDAO.dohvatiZeton(token, konfig);

		odgovor = provjeriZeton(zeton, korisnik);
		if (odgovor == null) {
			AerodromiDAO aerodromiDAO = new AerodromiDAO();
			Aerodrom a = aerodromiDAO.dohvatiAerodrom(icao, konfig);

			odgovor = a != null ? Response.status(Response.Status.OK).entity(a).build()
					: Response.status(Response.Status.NOT_FOUND).entity("Nema aerodroma: " + icao).build();
		}

		return odgovor;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao1}/{icao2}")
	public Response dajUdaljenost(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String token,
			@PathParam("icao1") String icao1, @PathParam("icao2") String icao2) {
		Response odgovor = null;

		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
		String adresa = konfig.dajPostavku("server.adresa");
		int port = Integer.valueOf(konfig.dajPostavku("server.port"));
		int cekanje = Integer.valueOf(konfig.dajPostavku("maks.cekanje"));

		ZetoniDAO zetoniDAO = new ZetoniDAO();
		Zeton zeton = zetoniDAO.dohvatiZeton(token, konfig);

		odgovor = provjeriZeton(zeton, korisnik);
		if (odgovor == null) {
			String komanda = "DISTANCE " + icao1 + " " + icao2;
			String odgovorPosluzitelja = posaljiKomandu(adresa, port, cekanje, komanda);

			if (odgovorPosluzitelja.startsWith("OK")) {
				String udaljenost = odgovorPosluzitelja.split(" ")[1];
				odgovor = Response.status(Response.Status.OK).entity(new Udaljenost(Integer.valueOf(udaljenost)))
						.build();
			} else if (odgovorPosluzitelja.startsWith("ERROR")) {
				odgovor = Response.status(Response.Status.NOT_FOUND).entity(odgovorPosluzitelja).build();
			}
		}

		return odgovor;
	}


	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao}/polasci")
	public Response dajPolaskeAerodoma(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String token,
			@PathParam("icao") String icao, @QueryParam("vrsta") String vrsta, @QueryParam("od") String vrijemeOd,
			@QueryParam("do") String vrijemeDo) {
		Response odgovor = null;

		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
		List<AvionLeti> aerodromPolasci = null;
		
		ZetoniDAO zetoniDAO = new ZetoniDAO();

		Zeton zeton = zetoniDAO.dohvatiZeton(token, konfig);

		odgovor = provjeriZeton(zeton, korisnik);
		if (odgovor == null) {
			int odVrijeme = 0;
			int doVrijeme = 0;
			if(Integer.valueOf(vrsta) == 0) {
				odVrijeme = izvrsiDatumPretvaranje(vrijemeOd);
				doVrijeme = izvrsiDatumPretvaranje(vrijemeDo);
			}else if(Integer.valueOf(vrsta) == 1) {
				odVrijeme = Integer.valueOf(vrijemeOd);
				doVrijeme = Integer.valueOf(vrijemeDo);
			}
			
			AerodromiPolasciDAO aerodromiPolasciDAO = new AerodromiPolasciDAO();
			aerodromPolasci = aerodromiPolasciDAO.dohvatiPolaskeZaInterval(icao, odVrijeme, doVrijeme, konfig);
			
			odgovor = Response.status(Response.Status.OK).entity(aerodromPolasci).build();
		}

		return odgovor;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{icao}/dolasci")
	public Response dajDolaske(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String token,
			@PathParam("icao") String icao, @QueryParam("vrsta") String vrsta, @QueryParam("od") String vrijemeOd,
			@QueryParam("do") String vrijemeDo) {
		Response odgovor = null;

		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
		List<AvionLeti> aerodromPolasci = null;
		
		ZetoniDAO zetoniDAO = new ZetoniDAO();

		Zeton zeton = zetoniDAO.dohvatiZeton(token, konfig);

		odgovor = provjeriZeton(zeton, korisnik);
		if (odgovor == null) {
			int odVrijeme = 0;
			int doVrijeme = 0;
			if(Integer.valueOf(vrsta) == 0) {
				odVrijeme = izvrsiDatumPretvaranje(vrijemeOd);
				doVrijeme = izvrsiDatumPretvaranje(vrijemeDo);
			}else if(Integer.valueOf(vrsta) == 1) {
				odVrijeme = Integer.valueOf(vrijemeOd);
				doVrijeme = Integer.valueOf(vrijemeDo);
			}
			
			AerodromiDolasciDAO aerodromiPolasciDAO = new AerodromiDolasciDAO();
			aerodromPolasci = aerodromiPolasciDAO.dohvatiDolaskeZaInterval(icao, odVrijeme, doVrijeme, konfig);
			
			odgovor = Response.status(Response.Status.OK).entity(aerodromPolasci).build();
		}

		return odgovor;
	}


	private Response provjeriZeton(Zeton zeton, String korisnik) {
		Response odgovor = null;

		if (zeton == null) {
			odgovor = Response.status(Response.Status.NOT_FOUND).entity("Žeton ne postoji.").build();
		} else {
			boolean status = zeton.provjeriVlasnistvoZetona(korisnik);

			if (!status) {
				odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Žeton ne pripada korisniku.").build();
			} else if (!zeton.provjeriVrijemeZetona()) {
				odgovor = Response.status(Response.Status.REQUEST_TIMEOUT).entity("Vrijeme trajanja žetona je isteklo.")
						.build();
			} else {
				boolean aktivnost = zeton.provjeriAktivnostZetona();
				if (!aktivnost) {
					odgovor = Response.status(Response.Status.NOT_FOUND).entity("Status žetona nije važeći.").build();
				}
			}
		}

		return odgovor;
	}

	/**
	 * Slanje komande poslužitelju.
	 *
	 * @param adresa  adresa
	 * @param port    broj porta
	 * @param cekanje maksimalno čekanje na odgovor poslužitelja
	 * @param komanda komanda
	 * @return the string
	 */
	public String posaljiKomandu(String adresa, int port, int cekanje, String komanda) {
		InputStreamReader isr = null;
		OutputStreamWriter osw = null;

		try (Socket veza = new Socket()) {
			InetSocketAddress isa = new InetSocketAddress(adresa, port);
			veza.connect(isa, cekanje);
			isr = new InputStreamReader(veza.getInputStream(), Charset.forName("UTF-8"));
			osw = new OutputStreamWriter(veza.getOutputStream(), Charset.forName("UTF-8"));

			osw.write(komanda);
			osw.flush();
			veza.shutdownOutput();
			StringBuilder tekst = new StringBuilder();
			while (true) {
				int i = isr.read();
				if (i == -1) {
					break;
				}
				tekst.append((char) i);
			}
			veza.shutdownInput();
			veza.close();
			return tekst.toString();
		} catch (IOException e) {
			Logger.getLogger(RestAerodromi.class.getName()).log(Level.SEVERE, null, e);
		} finally {
			try {
				isr.close();
				osw.close();
			} catch (IOException e) {
				Logger.getLogger(RestAerodromi.class.getName()).log(Level.SEVERE, null, e);
			}
		}

		return null;
	}

	private Aerodrom pretvoriJSON(String sadrzaj) {
		Gson gson = new Gson();

		Aerodrom a;
		try {
			a = gson.fromJson(sadrzaj, Aerodrom.class);
		} catch (JsonSyntaxException e) {
			a = null;
		}

		return a;
	}

	/**
	 * Pretvara datum iz stringa u sekunde.
	 *
	 * @param datum datum
	 * @return the long
	 */
	public int izvrsiDatumPretvaranje(String datum) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Date date = null;
		try {
			date = sdf.parse(datum);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long milisekunde = date.getTime();
		int sekunde = (int) TimeUnit.MILLISECONDS.toSeconds(milisekunde);

		return sekunde;
	}
}
