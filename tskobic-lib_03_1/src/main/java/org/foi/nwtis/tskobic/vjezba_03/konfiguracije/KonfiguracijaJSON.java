package org.foi.nwtis.tskobic.vjezba_03.konfiguracije;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import com.google.gson.Gson;

public class KonfiguracijaJSON extends KonfiguracijaApstraktna {

	public static final String TIP = "json";

	public KonfiguracijaJSON(String nazivDatoteke) {
		super(nazivDatoteke);
	}

	@Override
	public void ucitajKonfiguraciju(String nazivDatoteke) throws NeispravnaKonfiguracija {
		File datoteka = new File(nazivDatoteke);
		String tip = Konfiguracija.dajTipKonfiguracije(nazivDatoteke);
		if(tip == null || tip.compareTo(TIP) != 0) {
			throw new NeispravnaKonfiguracija("Datoteka: " + nazivDatoteke + " nema tip: " + TIP);
		} else if (! datoteka.isFile() || ! datoteka.exists() || ! datoteka.canRead()) {
			throw new NeispravnaKonfiguracija("Datoteka: " + nazivDatoteke + 
					" nije ispravnog tipa/ne postoji/ne moće se čitati");
		}
		try (BufferedReader br = new BufferedReader(new FileReader(datoteka))) {
			Gson gson = new Gson();
			this.postavke = gson.fromJson(br, Properties.class);
		} catch (IOException e) {
			throw new NeispravnaKonfiguracija(e.getMessage());
		}			
	}

	@Override
	public void spremiKonfiguraciju(String nazivDatoteke) throws NeispravnaKonfiguracija {
		File datoteka = new File(nazivDatoteke);
		String tip = Konfiguracija.dajTipKonfiguracije(nazivDatoteke);
		if(tip == null || tip.compareTo(TIP) != 0) {
			throw new NeispravnaKonfiguracija("Datoteka: " + nazivDatoteke + " nema tip: " + TIP);
		} else if (datoteka.exists() && ! datoteka.canWrite()) {
			throw new NeispravnaKonfiguracija("Datoteka: " + nazivDatoteke + 
					" nije ispravnog tipa/ne postoji/ne može se pisati u datoteku");
		}
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(datoteka))) {
			Gson gson = new Gson();
			gson.toJson(this.postavke, bw);
		} catch (IOException e) {
			throw new NeispravnaKonfiguracija(e.getMessage());
		}			
	}
	
}