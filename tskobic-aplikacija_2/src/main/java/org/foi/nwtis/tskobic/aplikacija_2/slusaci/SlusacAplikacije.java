package org.foi.nwtis.tskobic.aplikacija_2.slusaci;

import org.foi.nwtis.tskobic.aplikacija_2.dretve.PreuzimanjeRasporedaAerodroma;
import org.foi.nwtis.tskobic.vjezba_03.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Klasa slušač SlusacAplikacije
 *
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {
	
	/** Dretva preuzimanje rasporeda aerodroma */
	PreuzimanjeRasporedaAerodroma pra = null;
	
    /**
     * Konstruktor. 
     */
    public SlusacAplikacije() {
    }

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		String nazivDatoteke = context.getInitParameter("konfiguracija");
		KonfiguracijaBP konfig = null;
		String putanja = context.getRealPath("/WEB-INF") + java.io.File.separator; 
		try {
			konfig = new PostavkeBazaPodataka(putanja + nazivDatoteke);
			konfig.ucitajKonfiguraciju();
			context.setAttribute("Postavke", konfig);
			System.out.println("Učitana konfiguracijska datoteka.");
		} catch (NeispravnaKonfiguracija e1) {
			e1.printStackTrace();
			return;
		}

		pra = new PreuzimanjeRasporedaAerodroma(context);
		pra.start();
		ServletContextListener.super.contextInitialized(sce);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		context.removeAttribute("Postavke");
		
		if(pra != null) {
			pra.interrupt();
		}
		ServletContextListener.super.contextDestroyed(sce);
	}

}
