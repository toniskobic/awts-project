package org.foi.nwtis.tskobic.aplikacija_5.slusaci;

import org.foi.nwtis.tskobic.aplikacija_5.dretve.Osvjezivac;
import org.foi.nwtis.tskobic.vjezba_03.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Klasa slu�a� SlusacAplikacije.
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {
	
	/** Dretva osvježivač. */
	Osvjezivac osvjezivac = null;
	
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

		osvjezivac = new Osvjezivac(context);
		osvjezivac.start();
		ServletContextListener.super.contextInitialized(sce);
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		context.removeAttribute("Postavke");
		
		if(osvjezivac != null) {
			osvjezivac.interrupt();
		}
		ServletContextListener.super.contextDestroyed(sce);
	}

}
