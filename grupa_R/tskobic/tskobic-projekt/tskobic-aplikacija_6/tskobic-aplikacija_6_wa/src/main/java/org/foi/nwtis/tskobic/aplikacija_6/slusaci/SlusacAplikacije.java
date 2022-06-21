package org.foi.nwtis.tskobic.aplikacija_6.slusaci;

import org.foi.nwtis.tskobic.aplikacija_6.zrna.Pokretanje;
import org.foi.nwtis.tskobic.vjezba_03.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.KonfiguracijaBP;
import org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka.PostavkeBazaPodataka;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Klasa slušač SlusacAplikacije.
 *
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

	/** Stvaranje anotacije tipa Pokretanje. */
	private static final AnnotationLiteral<Pokretanje> POKRETANJE_ANOTACIJA = new AnnotationLiteral<Pokretanje>() {
		private static final long serialVersionUID = 1L;
	};

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

		CDI.current().select(POKRETANJE_ANOTACIJA).forEach(bean -> bean.toString());
		ServletContextListener.super.contextInitialized(sce);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		context.removeAttribute("Postavke");

		ServletContextListener.super.contextDestroyed(sce);
	}

}
