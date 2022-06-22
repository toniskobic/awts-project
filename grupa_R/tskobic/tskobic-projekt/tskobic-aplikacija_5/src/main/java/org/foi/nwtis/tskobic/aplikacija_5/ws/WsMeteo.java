package org.foi.nwtis.tskobic.aplikacija_5.ws;

import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.MeteoPodaci;
import org.foi.nwtis.tskobic.aplikacija_5.klijenti.AerodromiKlijent;
import org.foi.nwtis.tskobic.aplikacija_5.klijenti.ProvjereKlijent;
import org.foi.nwtis.tskobic.aplikacija_5.podaci.ZetonOdgovor;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;

/**
 * Klasa WsMeteo.
 */
@WebService(serviceName = "meteo")
public class WsMeteo {
	
	/** Kontekst web servisa. */
	@Resource
	private WebServiceContext wsContext;

	/**
	 * Daje meteorološke podatke za određeni aeodrom.
	 *
	 * @param icao icao aerodroma
	 * @return meteo podaci
	 */
	@WebMethod
	public MeteoPodaci dajMeteo(@WebParam(name = "icao") String icao) {
		PostavkeBazaPodataka konfig = dajPBP();
		
		String sustavKorisnik = konfig.dajPostavku("sustav.korisnik");
		String sustavLozinka = konfig.dajPostavku("sustav.lozinka");
		
		ProvjereKlijent provjereKlijent = new ProvjereKlijent(konfig);
		ZetonOdgovor zeton = provjereKlijent.autentificirajKorisnika(sustavKorisnik, sustavLozinka);
		
		MeteoPodaci meteoPodaci = null;
		
		if(zeton != null) {
			AerodromiKlijent aerodromiKlijent = new AerodromiKlijent(konfig);
			Aerodrom a = aerodromiKlijent.dajAerodrom(sustavKorisnik, zeton.getZeton(), icao);
			if(a != null) {
				String apiKey = konfig.dajPostavku("OpenWeatherMap.apikey");
				
				OWMKlijent owmKlijent = new OWMKlijent(apiKey);
				
				try {
					meteoPodaci = owmKlijent.getRealTimeWeather(a.getLokacija().getLatitude(), a.getLokacija().getLongitude());
				} catch (NwtisRestIznimka e) {
					e.printStackTrace();
				}
			}
		}
		
		return meteoPodaci;
	}

	/**
	 * Daje postavke baze podataka.
	 *
	 * @return postavke baza podataka
	 */
	private PostavkeBazaPodataka dajPBP() {
		ServletContext context = (ServletContext) wsContext.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
		PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");

		return pbp;
	}

}
