package org.foi.nwtis.tskobic.aplikacija_3.podaci;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Klasa Zeton.
 *
 * @param id id
 * @param korisnik korisnik
 * @param status status
 * @param vrijemeKreiranja vrijeme kreiranja
 * @param vrijemeIsteka vrijeme isteka
 */
@AllArgsConstructor
public class Zeton {

    @Getter
    @Setter
    private int id;
	
    @Getter
    @Setter
    @NonNull
    private String korisnik;
    
    @Getter
    @Setter
    private int status; 

    @Getter
    @Setter
    @NonNull
    private String vrijemeKreiranja;
    
    @Getter
    @Setter
    @NonNull
    private String vrijemeIsteka;
    
    /**
     * Provjerava vlasništvo žetona.
     *
     * @param uneseniKorisnik uneseni korisnik
     * @return true, ako žeton pripada unesenom korisniku
     */
    public boolean provjeriVlasnistvoZetona(String uneseniKorisnik) {
    	if(korisnik.equals(uneseniKorisnik)) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    /**
     * Provjerava vrijeme žetona.
     *
     * @return true, ako žeton nije istekao
     */
    public boolean provjeriVrijemeZetona() {
    	long vrijeme = izvrsiDatumPretvaranje(this.vrijemeIsteka);
    	long trenutnoVrijeme = new Date().getTime();
    	
		if (trenutnoVrijeme <= vrijeme) {
			return true;
		} else {
			return false;
		}
    }
    
    /**
     * Provjerava aktivnost žeton.
     *
     * @return true, ako je žeton aktivan
     */
    public boolean provjeriAktivnostZetona() {
    	if(status == 1 && provjeriVrijemeZetona()) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    
	/**
	 * Izvršava pretvaranja datum u broj milisekundi.
	 *
	 * @param datum datum
	 * @return broj milisekundi
	 */
	private long izvrsiDatumPretvaranje(String datum) {
	    long milisekunde = LocalDateTime.parse(datum, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
	            .atZone(ZoneId.systemDefault())
	            .toInstant()
	            .toEpochMilli();
	    
	    return milisekunde;
	}
}
