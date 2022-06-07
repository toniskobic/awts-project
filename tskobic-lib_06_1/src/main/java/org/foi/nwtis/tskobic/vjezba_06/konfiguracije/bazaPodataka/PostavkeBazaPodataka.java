package org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka;

import java.util.Properties;

import org.foi.nwtis.tskobic.vjezba_03.konfiguracije.Konfiguracija;
import org.foi.nwtis.tskobic.vjezba_03.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.tskobic.vjezba_03.konfiguracije.NeispravnaKonfiguracija;

public class PostavkeBazaPodataka extends KonfiguracijaApstraktna implements KonfiguracijaBP {

	public PostavkeBazaPodataka(String nazivDatoteke) {
		super(nazivDatoteke);
	}

	@Override
	public String getAdminDatabase() {
		return this.dajPostavku("admin.database");
	}

	@Override
	public String getAdminPassword() {
		return this.dajPostavku("admin.password");
	}

	@Override
	public String getAdminUsername() {
		return this.dajPostavku("admin.username");
	}

	@Override
	public String getDriverDatabase() {
		return this.getDriverDatabase(this.getServerDatabase());
	}

	@Override
	public String getDriverDatabase(String urlBazePodataka) {
		String protokol = null;
		String s[] = urlBazePodataka.split("//");
		protokol = s[0].substring(0, s[0].length() - 1);
		protokol = protokol.replace(":", ".");
		String driver = this.dajPostavku(protokol);
		return driver;
	}

	@Override
	public Properties getDriversDatabase() {
		Properties odgovor = new Properties();
		Properties props = this.dajSvePostavke();
		for(Object o: props.keySet()) {
			String k = (String) o;
			if(k.startsWith("jdbc.")) {
				String v = this.dajPostavku(k);
				odgovor.setProperty(k, v);
			}
		}
		
		return odgovor;
	}

	@Override
	public String getServerDatabase() {
		return this.dajPostavku("server.database");
	}

	@Override
	public String getUserDatabase() {
		return this.dajPostavku("user.database");
	}

	@Override
	public String getUserPassword() {
		return this.dajPostavku("user.password");
	}

	@Override
	public String getUserUsername() {
		return this.dajPostavku("user.username");
	}

	@Override
	public void ucitajKonfiguraciju(String nazivDatoteke) throws NeispravnaKonfiguracija {
		Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
		this.postavke = konfig.dajSvePostavke();
	}

	@Override
	public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija {
		Konfiguracija konfig = KonfiguracijaApstraktna.dajKonfiguraciju(datoteka);
		Properties props = this.dajSvePostavke();
		for(Object o: props.keySet()) {
			String k = (String) o;
			String v = this.dajPostavku(k);
			konfig.spremiPostavku(k, v);
		}
		konfig.spremiKonfiguraciju();
	}

}
