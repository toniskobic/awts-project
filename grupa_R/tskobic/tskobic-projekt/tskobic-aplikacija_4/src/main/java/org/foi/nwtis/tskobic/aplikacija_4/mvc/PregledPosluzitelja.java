package org.foi.nwtis.tskobic.aplikacija_4.mvc;

import java.util.List;
import java.util.stream.Collectors;

import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.tskobic.aplikacija_4.podaci.Grupa;
import org.foi.nwtis.tskobic.aplikacija_4.podaci.ZetonOdgovor;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;

/**
 * Kontroler PregledPosluzitelja za putanju korisnici.
 */
@Controller
@Path("serveri")
@RequestScoped
public class PregledPosluzitelja {

	/** kontekst servleta. */
	@Context
	private ServletContext context;

	/** model. */
	@Inject
	private Models model;

	/**
	 * Upravljanje poslužiteljem.
	 */
	@GET
	@Path("upravljanjePosluziteljem")
	@View("upravljanjePosluziteljem.jsp")
	public void upravljanjePosluziteljem() {

		SocketServerKlijent socketServerKlijent = new SocketServerKlijent(context);
		String rezultat = socketServerKlijent.posaljiNaredbu("STATUS");

		model.put("status", rezultat);
	}

	/**
	 * Ispis poruke poslužitelja nakon slanja naredbe.
	 */
	@POST
	@Path("upravljanjePosluziteljem/rezultat")
	@View("upravljanjePosluziteljemRezultat.jsp")
	public void obradaUpravljanjaPosluziteljem(@FormParam("komanda") String komanda) {

		SocketServerKlijent socketServerKlijent = new SocketServerKlijent(context);
		String rezultat = "Slanje naredbe nije uspjelo.";

		if (komanda.equals("LOAD")) {
			if (PregledKorisnika.korIme != null && PregledKorisnika.zeton != null)
				rezultat = socketServerKlijent.ucitajAerodrome(PregledKorisnika.korIme,
						PregledKorisnika.zeton.getZeton());
		} else {
			rezultat = socketServerKlijent.posaljiNaredbu(komanda);
		}

		model.put("rezultat", rezultat);
	}

}
