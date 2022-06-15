package org.foi.nwtis.tskobic.aplikacija_3.rest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.tskobic.aplikacija_3.podaci.KorisniciDAO;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.UlogeDAO;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.Zeton;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.ZetonOdgovor;
import org.foi.nwtis.tskobic.aplikacija_3.podaci.ZetoniDAO;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.servlet.ServletContext;
import jakarta.ws.rs.DELETE;
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
		Zeton zeton = zetoniDAO.dohvatiZeton(token, konfig);
		
		if(zeton == null) {
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
				if (aktivnost) {
					odgovor = Response.status(Response.Status.OK).entity("Žeton je aktivan.").build();
				} else {
					odgovor = Response.status(Response.Status.NOT_FOUND).entity("Status žetona nije važeći.").build();
				}
			}
		}

		return odgovor;
	}

	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{token}")
	public Response deaktivirajZeton(@PathParam("token") String token, @HeaderParam("korisnik") String korisnik,
			@HeaderParam("lozinka") String lozinka) {
		Response odgovor = null;

		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");

		ZetoniDAO zetoniDAO = new ZetoniDAO();
		Zeton zeton = zetoniDAO.dohvatiZeton(token, konfig);
		
		if(zeton == null) {
			odgovor = Response.status(Response.Status.NOT_FOUND).entity("Žeton ne postoji.").build();
		} else {
			boolean status = zeton.provjeriVlasnistvoZetona(korisnik);
			if (!status) {
				odgovor = Response.status(Response.Status.UNAUTHORIZED).entity("Žeton ne pripada korisniku.").build();
			} else if (!zeton.provjeriVrijemeZetona()) {
				odgovor = Response.status(Response.Status.REQUEST_TIMEOUT).entity("Vrijeme trajanja žetona je isteklo.")
						.build();
			} else if (zeton.provjeriAktivnostZetona()) {
				if (zetoniDAO.promijeniStatusZetona(0, token, konfig)) {
					odgovor = Response.status(Response.Status.OK).entity("Žeton je deaktiviran.").build();
				}
			} else {
				odgovor = Response.status(Response.Status.NOT_FOUND).entity("Žeton je već deaktiviran.").build();
			}
		}

		return odgovor;
	}

	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("korisnik/{korisnik}")
	public Response deaktivirajZetone(@PathParam("korisnik") String deaktivacijaKorisnik,
			@HeaderParam("korisnik") String korisnik, @HeaderParam("lozinka") String lozinka) {
		Response odgovor = null;

		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");

		String adminGrupa = konfig.dajPostavku("sustav.administratori");
		KorisniciDAO korisniciDAO = new KorisniciDAO();
		UlogeDAO ulogeDAO = new UlogeDAO();

		boolean login = korisniciDAO.autentifikacijaKorisnika(korisnik, lozinka, konfig);
		if (!login) {
			odgovor = Response.status(Response.Status.UNAUTHORIZED)
					.entity("Autentifikacija nije uspješna, pogrešno korisničko ime ili lozinka.").build();
		} else {
			boolean status = ulogeDAO.autorizacijaKorisnika(korisnik, adminGrupa, konfig);
			if (!status) {
				odgovor = Response.status(Response.Status.UNAUTHORIZED)
						.entity("Korisnik nema ovlaštenje za brisanje žetona.").build();
			} else {
				ZetoniDAO zetoniDAO = new ZetoniDAO();
				List<Zeton> aktivniZetoni = zetoniDAO.dohvatiAktivneZetone(deaktivacijaKorisnik, konfig);

				if (aktivniZetoni.isEmpty()) {
					odgovor = Response.status(Response.Status.NOT_FOUND).entity("Korisnik nema nijedan aktivni žeton.")
							.build();
				} else {
					try (Connection con = otvoriVezuBP()) {
						for (Zeton zeton : aktivniZetoni) {
							zetoniDAO.promijeniStatusZetona(0, String.valueOf(zeton.getId()), konfig, con);
						}
					} catch (Exception ex) {
						Logger.getLogger(RestProvjere.class.getName()).log(Level.SEVERE, null, ex);
					}

					odgovor = Response.status(Response.Status.OK).entity("Korisnikovi žetoni su deaktivirani.").build();
				}
			}
		}

		return odgovor;
	}

	public Connection otvoriVezuBP() throws SQLException {
		PostavkeBazaPodataka konfig = (PostavkeBazaPodataka) context.getAttribute("Postavke");
		String url = konfig.getServerDatabase() + konfig.getUserDatabase();
		String bpkorisnik = konfig.getUserUsername();
		String bplozinka = konfig.getUserPassword();

		Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);

		return con;
	}
}
