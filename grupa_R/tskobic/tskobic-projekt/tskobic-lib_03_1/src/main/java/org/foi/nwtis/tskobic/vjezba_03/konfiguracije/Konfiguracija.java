package org.foi.nwtis.tskobic.vjezba_03.konfiguracije;

import java.util.Properties;

/*
 * Sučelje za rad s postavkama iz konfiguracijske datoteke
 */
public interface Konfiguracija {

	/**
	 * Učitava konfiguraciju.
	 *
	 * @throws NeispravnaKonfiguracija ako se javi problem kod spremanja datoteke
	 *                                 konfiguracije
	 */
	void ucitajKonfiguraciju() throws NeispravnaKonfiguracija;

	/**
	 * Učitava konfiguraciju pod danim nazivom datoteke.
	 *
	 * @param nazivDatoteke naziv datoteke
	 * @throws NeispravnaKonfiguracija ako tip nije podržan ili ne postoji datoteka
	 *                                 konfiguracije
	 */
	void ucitajKonfiguraciju(String nazivDatoteke) throws NeispravnaKonfiguracija;

	/**
	 * Sprema konfiguraciju.
	 *
	 * @throws NeispravnaKonfiguracija ako se javi problem kod spremanja datoteke
	 *                                 konfiguracije
	 */
	void spremiKonfiguraciju() throws NeispravnaKonfiguracija;

	/**
	 * Sprema konfiguraciju pod danim nazivom datoteke.
	 *
	 * @param datoteka naziv datoteke konfiguracije
	 * @throws NeispravnaKonfiguracija ako tip nije podržan ili se javi problem kod
	 *                                 spremanja datoteke konfiguracije
	 */
	void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija;

	/**
	 * Vraća sve postavke.
	 *
	 * @return sve postavke
	 */
	Properties dajSvePostavke();

	/**
	 * Briše sve postavke.
	 *
	 * @return true, ako postavke nisu prazne, inače vraća false
	 */
	boolean obrisiSvePostavke();

	/**
	 * Vraća vrijednost postavke na temelju ključa.
	 *
	 * @param kljuc ključ postavke
	 * @return ako postoji postavka s ključem vraća njenu vrijednost, inače vraća
	 *         null
	 */
	String dajPostavku(String kljuc);

	/**
	 * Sprema postavku.
	 *
	 * @param kljuc      ključ postavke
	 * @param vrijednost vrijednost postavke
	 * @return true, ako ne postoji postavka s ključem i uspješno je dodana, inače
	 *         vraća false
	 */
	boolean spremiPostavku(String kljuc, String vrijednost);

	/**
	 * Ažurira postavku.
	 *
	 * @param kljuc      ključ postavke
	 * @param vrijednost vrijednost postavke
	 * @return true, ako postoji postavka s ključem i uspješno je ažurirana, inače
	 *         vraća false
	 */
	boolean azurirajPostavku(String kljuc, String vrijednost);

	/**
	 * Provjerava postoji li postavka.
	 *
	 * @param kljuc ključ postavke
	 * @return true, ako je postoji postavka s ključem, inače vraća false
	 */
	boolean postojiPostavka(String kljuc);

	/**
	 * Briše postavku.
	 *
	 * @param kljuc ključ postavke
	 * @return true, ako postoji postavka s ključem i uspješno je obrisana
	 */
	boolean obrisiPostavku(String kljuc);

	/**
	 * Vraća tip konfiguracije.
	 *
	 * @param nazivDatoteke naziv datoteke
	 * @return ako postoji tip konfiguracije (znak . i barem jedan znak nakon
	 *         njega), inače vraća null
	 */
	static String dajTipKonfiguracije(String nazivDatoteke) {
		int poz = nazivDatoteke.lastIndexOf(".");
		if (poz == -1) {
			return null;
		}
		String tip = nazivDatoteke.substring(poz + 1).toLowerCase();
		if (tip.length() == 0) {
			return null;
		}
		return tip;
	}
}
