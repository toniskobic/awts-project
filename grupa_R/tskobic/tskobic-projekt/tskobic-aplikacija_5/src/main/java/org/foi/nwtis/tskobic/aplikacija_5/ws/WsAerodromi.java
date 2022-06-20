package org.foi.nwtis.tskobic.aplikacija_5.ws;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.tskobic.aplikacija_5.klijenti.AerodromiKlijent;
import org.foi.nwtis.tskobic.aplikacija_5.wsock.Info;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;

/**
 * Klasa WsAerodromi
 */
@WebService(serviceName = "aerodromi")
public class WsAerodromi {

	/** Kontekst web servisa. */
	@Resource
	private WebServiceContext wsContext;

	@WebMethod
	public List<AvionLeti> dajPolaskeDan(@WebParam(name = "korisnik") String korisnik,
			@WebParam(name = "zeton") String zeton, @WebParam(name = "icao") String icao,
			@WebParam(name = "danOd") String danOd, @WebParam(name = "danDo") String danDo) {
		PostavkeBazaPodataka konfig = dajPBP();
		List<AvionLeti> aerodromPolasci = null;

		AerodromiKlijent aerodromiKlijent = new AerodromiKlijent(konfig);
		aerodromPolasci = aerodromiKlijent.dajPolaskeAerodroma(korisnik, zeton, icao, danOd, danDo, 0);

		return aerodromPolasci;
	}

	@WebMethod
	public List<AvionLeti> dajPolaskeVrijeme(@WebParam(name = "korisnik") String korisnik,
			@WebParam(name = "zeton") String zeton, @WebParam(name = "icao") String icao,
			@WebParam(name = "vrijemeOd") String vrijemeOd, @WebParam(name = "vrijemeDo") String vrijemeDo) {
		PostavkeBazaPodataka konfig = dajPBP();
		List<AvionLeti> aerodromPolasci = null;

		AerodromiKlijent aerodromiKlijent = new AerodromiKlijent(konfig);
		aerodromPolasci = aerodromiKlijent.dajPolaskeAerodroma(korisnik, zeton, icao, vrijemeOd, vrijemeDo, 1);

		return aerodromPolasci;
	};

	@WebMethod
	public boolean dodajAerodromPreuzimanje(@WebParam(name = "korisnik") String korisnik,
			@WebParam(name = "zeton") String zeton, @WebParam(name = "icao") String icao) {
		PostavkeBazaPodataka konfig = dajPBP();

		AerodromiKlijent aerodromiKlijent = new AerodromiKlijent(konfig);
		boolean dodan = aerodromiKlijent.dodajAerodromPreuzimanje(korisnik, zeton, icao);
		if (dodan) {
			String vrijeme = trenutnoVrijeme("dd.MM.yyyy HH:mm:ss");
			List<Aerodrom> aerodromi = aerodromiKlijent.dajPraceneAerodrome(korisnik, zeton);
			
			Info.dajMeteo(vrijeme + ", " + aerodromi.size());
		}

		return dodan;
	}

	/**
	 * Vraća trenutno vrijeme u proslijeđenom formatu.
	 *
	 * @param format format
	 * @return the string
	 */
	private String trenutnoVrijeme(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String datum = sdf.format(new Date());

		return datum;
	}

	/**
	 * Daje postavke baze podataka iz konteksta servleta.
	 *
	 * @return postavke baza podataka
	 */
	private PostavkeBazaPodataka dajPBP() {
		ServletContext context = (ServletContext) wsContext.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");

		return pbp;
	}

}
