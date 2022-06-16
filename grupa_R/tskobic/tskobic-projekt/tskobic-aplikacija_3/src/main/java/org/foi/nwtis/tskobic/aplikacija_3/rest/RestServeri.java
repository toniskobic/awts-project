package org.foi.nwtis.tskobic.aplikacija_3.rest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.Posluzitelj;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.Zeton;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.ZetoniDAO;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("serveri")
public class RestServeri {

	/** Kontekst servleta. */
	@Context
	private ServletContext context;

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dajStatus(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String token) {
		Response odgovor = null;

		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
		String adresa = konfig.dajPostavku("server.adresa");
		int port = Integer.valueOf(konfig.dajPostavku("server.port"));
		int cekanje = Integer.valueOf(konfig.dajPostavku("maks.cekanje"));

		ZetoniDAO zetoniDAO = new ZetoniDAO();
		Zeton zeton = zetoniDAO.dohvatiZeton(token, konfig);

		odgovor = provjeriZeton(zeton, korisnik);
		if (odgovor == null) {
			String komanda = "STATUS";
			String odgovorPosluzitelja = posaljiKomandu(adresa, port, cekanje, komanda);

			if (odgovorPosluzitelja.startsWith("OK")) {
				int udaljenost = Integer.valueOf(odgovorPosluzitelja.split(" ")[1]);
				odgovor = Response.status(udaljenost).entity(new Posluzitelj(adresa, port)).build();
			} else {
				odgovor = Response.status(Response.Status.BAD_REQUEST).entity(odgovorPosluzitelja).build();
			}
		}

		return odgovor;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{komanda}")
	public Response posaljiKomandu(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String token,
			@PathParam("komanda") String komanda) {
		Response odgovor = null;

		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
		String adresa = konfig.dajPostavku("server.adresa");
		int port = Integer.valueOf(konfig.dajPostavku("server.port"));
		int cekanje = Integer.valueOf(konfig.dajPostavku("maks.cekanje"));

		ZetoniDAO zetoniDAO = new ZetoniDAO();
		Zeton zeton = zetoniDAO.dohvatiZeton(token, konfig);

		odgovor = provjeriZeton(zeton, korisnik);
		if (odgovor == null) {
			if (komanda.equals("QUIT") || komanda.equals("INIT") || komanda.equals("CLEAR")) {
				String odgovorPosluzitelja = posaljiKomandu(adresa, port, cekanje, komanda);

				odgovor = odgovorPosluzitelja.equals("OK")
						? Response.status(Response.Status.OK).entity(odgovorPosluzitelja).build()
						: Response.status(Response.Status.BAD_REQUEST).entity(odgovorPosluzitelja).build();
			} else {
				odgovor = Response.status(Response.Status.BAD_REQUEST).entity("Neispravna komanda.").build();
			}
		}

		return odgovor;
	}

	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	@Path("LOAD")
	public Response ucitajAerodrome(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String token,
			String sadrzaj) {
		Response odgovor = null;

		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
		String adresa = konfig.dajPostavku("server.adresa");
		int port = Integer.valueOf(konfig.dajPostavku("server.port"));
		int cekanje = Integer.valueOf(konfig.dajPostavku("maks.cekanje"));

		ZetoniDAO zetoniDAO = new ZetoniDAO();
		Zeton zeton = zetoniDAO.dohvatiZeton(token, konfig);

		odgovor = provjeriZeton(zeton, korisnik);
		if (odgovor == null) {
			List<Aerodrom> aerodromi = pretvoriJSON(sadrzaj);

			if (aerodromi == null) {
				odgovor = Response.status(400).entity("Proslijeđeni JSON format nije ispravan.").build();
			} else {
				String komanda = "LOAD " + sadrzaj.trim();
				
				String odgovorPosluzitelja = posaljiKomandu(adresa, port, cekanje, komanda);
				if (odgovorPosluzitelja.startsWith("OK")) {
					int brojAerodroma = Integer.valueOf(odgovorPosluzitelja.split(" ")[1]);

					odgovor = brojAerodroma == aerodromi.size()
							? Response.status(Response.Status.OK).entity(odgovorPosluzitelja).build()
							: Response.status(Response.Status.CONFLICT).entity(odgovorPosluzitelja).build();
				} else {
					odgovor = Response.status(Response.Status.CONFLICT).entity(odgovorPosluzitelja).build();
				}
			}
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

	private List<Aerodrom> pretvoriJSON(String sadrzaj) {
		Gson gson = new Gson();
		JsonReader citac = new JsonReader(new StringReader(sadrzaj.trim()));
		citac.setLenient(true);

		List<Aerodrom> aerodromi;
		try {
			aerodromi = gson.fromJson(citac, new TypeToken<List<Aerodrom>>() {
			}.getType());
		} catch (JsonSyntaxException e) {
			aerodromi = null;
		}

		return aerodromi;
	}
}
