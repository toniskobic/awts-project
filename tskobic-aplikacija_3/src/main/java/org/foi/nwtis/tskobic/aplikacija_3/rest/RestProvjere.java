package org.foi.nwtis.tskobic.aplikacija_3.rest;

import org.foi.nwtis.tskobic.aplikacija_3.podaci.KorisniciDAO;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.Zeton;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.ZetonOdgovor;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.ZetoniDAO;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("provjere")
public class RestProvjere {

	@Context
	private ServletContext context;

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response autentifikacijaKorisnika(@HeaderParam("korisnik") String korisnik,
			@HeaderParam("lozinka") String lozinka) {
		Response odgovor = null;

		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
		KorisniciDAO korisniciDAO = new KorisniciDAO();
		boolean status = korisniciDAO.autentifikacijaKorisnika(korisnik, lozinka, konfig);

		if (!status) {
			odgovor = Response.status(Response.Status.UNAUTHORIZED)
					.entity("Autentifikacija nije uspješna, pogrešno korisničko ime ili lozinka.").build();
		} else {
			ZetoniDAO zetoniDAO = new ZetoniDAO();
			Zeton zeton = zetoniDAO.kreirajZeton(korisnik, konfig);

			odgovor = Response.status(Response.Status.OK)
					.entity(new ZetonOdgovor(zeton.getId(), zeton.getVrijemeIsteka())).build();
		}

		return odgovor;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{token}")
	public Response provjeraZetona(@PathParam("token") String token, @HeaderParam("korisnik") String korisnik,
			@HeaderParam("lozinka") String lozinka) {
		Response odgovor = null;

		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");

		ZetoniDAO zetoniDAO = new ZetoniDAO();
		boolean status = zetoniDAO.provjeriVlasnistvoZetona(token, korisnik, konfig);
		if (!status) {
			odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Žeton ne pripada korisniku.").build();
		} else if (!zetoniDAO.provjeriVrijemeZetona(token, konfig)) {
			odgovor = Response.status(Response.Status.REQUEST_TIMEOUT).entity("Vrijeme trajanja žetona je isteklo.")
					.build();
		} else if (zetoniDAO.provjeriAktivnostZetona(token, konfig)) {
			odgovor = Response.status(Response.Status.OK).entity("Žeton je aktivan.").build();

		}

		return odgovor;
	}

}
