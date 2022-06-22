package org.foi.nwtis.tskobic.aplikacija_3.rest;

import java.util.List;

import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.Grupa;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.GrupeDAO;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.KorisniciDAO;
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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Klasa RestKorisnici za URI putanju korisnici.
 */
@Path("korisnici")
public class RestKorisnici {

	/** Kontekst servleta. */
	@Context
	private ServletContext context;

	/**
	 * Daje sve korisnike.
	 *
	 * @param korisnik korisnik
	 * @param token žeton
	 * @return odgovor
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response dajSveKorisnike(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String token) {
		Response odgovor = null;

		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
		List<Korisnik> korisnici = null;

		ZetoniDAO zetoniDAO = new ZetoniDAO();
		Zeton zeton = zetoniDAO.dohvatiZeton(token, konfig);

		odgovor = provjeriZeton(zeton, korisnik);
		if (odgovor == null) {
			KorisniciDAO korisniciDAO = new KorisniciDAO();
			korisnici = korisniciDAO.dohvatiSveKorisnike(konfig);

			odgovor = korisnici != null ? Response.status(Response.Status.OK).entity(korisnici).build()
					: Response.status(Response.Status.NOT_FOUND).entity("Neuspješno dohvaćanje korisnika.").build();
		}

		return odgovor;
	}

	/**
	 * Dodaje korisnika u bazu podataka.
	 *
	 * @param korisnik korisnik
	 * @param token žeton
	 * @param sadrzaj tijelo POST zahtjeva
	 * @return odgovor
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response dodajKorisnika(@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String token,
			String sadrzaj) {
		Response odgovor = null;

		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");

		ZetoniDAO zetoniDAO = new ZetoniDAO();
		Zeton zeton = zetoniDAO.dohvatiZeton(token, konfig);

		odgovor = provjeriZeton(zeton, korisnik);
		if (odgovor == null) {
			Korisnik k = pretvoriJSON(sadrzaj);
			if (k == null) {
				odgovor = Response.status(Response.Status.BAD_REQUEST).entity("Proslijeđeni JSON format nije ispravan.")
						.build();
			} else {
				KorisniciDAO korisniciDAO = new KorisniciDAO();

				boolean status = korisniciDAO.dodajKorisnika(k, konfig);
				odgovor = status ? Response.status(Response.Status.OK).entity("Korisnik dodan.").build()
						: Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Korisnik nije dodan.").build();
			}
		}
		
		return odgovor;
	}

	/**
	 * Daje korisnika.
	 *
	 * @param trazeniKorisnik traženi korisnik
	 * @param korisnik korisnik koji je poslao HTTP zahtjev
	 * @param token žeton
	 * @return odgovor
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{korisnik}")
	public Response dajKorisnika(@PathParam("korisnik") String trazeniKorisnik,
			@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String token) {
		Response odgovor = null;

		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");

		ZetoniDAO zetoniDAO = new ZetoniDAO();
		Zeton zeton = zetoniDAO.dohvatiZeton(token, konfig);

		odgovor = provjeriZeton(zeton, korisnik);
		if (odgovor == null) {
			KorisniciDAO korisniciDAO = new KorisniciDAO();
			Korisnik k = korisniciDAO.dohvatiKorisnika(trazeniKorisnik, konfig);

			odgovor = k != null ? Response.status(Response.Status.OK).entity(k).build()
					: Response.status(Response.Status.NOT_FOUND).entity("Traženi korisnik ne postoji.").build();
		}

		return odgovor;
	}

	/**
	 * Daje grupe korisnika.
	 *
	 * @param proslijedjeniKorisnik proslijeđeni korisnik
	 * @param korisnik korisnik koji je poslao HTTP zahtjev
	 * @param token žeton
	 * @return odgovor
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{korisnik}/grupe")
	public Response dajGrupeKorisnika(@PathParam("korisnik") String proslijedjeniKorisnik,
			@HeaderParam("korisnik") String korisnik, @HeaderParam("zeton") String token) {
		Response odgovor = null;

		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
		List<Grupa> grupe = null;

		ZetoniDAO zetoniDAO = new ZetoniDAO();
		Zeton zeton = zetoniDAO.dohvatiZeton(token, konfig);

		odgovor = provjeriZeton(zeton, korisnik);
		if (odgovor == null) {
			GrupeDAO grupeDAO = new GrupeDAO();
			grupe = grupeDAO.dohvatiGrupeKorisnika(proslijedjeniKorisnik, konfig);

			odgovor = grupe != null ? Response.status(Response.Status.OK).entity(grupe).build()
					: Response.status(Response.Status.NOT_FOUND)
							.entity("Dohvaćanje grupa za proslijeđenog korisnika nije usjpelo.").build();
		}

		return odgovor;
	}

	/**
	 * Provjerav žeton.
	 *
	 * @param zeton žeton
	 * @param korisnik korisnik
	 * @return odgovor
	 */
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
	 * Pretvara string u JSON formatu u objekt klase Korisnik.
	 *
	 * @param sadrzaj JSON format u stringu
	 * @return korisnik
	 */
	private Korisnik pretvoriJSON(String sadrzaj) {
		Gson gson = new Gson();

		Korisnik k;
		try {
			k = gson.fromJson(sadrzaj, Korisnik.class);
		} catch (JsonSyntaxException e) {
			k = null;
		}

		return k;
	}
}
