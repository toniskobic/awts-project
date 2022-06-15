package org.foi.nwtis.tskobic.aplikacija_3.podaci;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

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
    
    
    public boolean provjeriVlasnistvoZetona(String uneseniKorisnik) {
    	if(korisnik.equals(uneseniKorisnik)) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    public boolean provjeriVrijemeZetona() {
    	long vrijeme = izvrsiDatumPretvaranje(this.vrijemeIsteka);
    	long trenutnoVrijeme = new Date().getTime();
    	
		if (trenutnoVrijeme <= vrijeme) {
			return true;
		} else {
			return false;
		}
    }
    
    public boolean provjeriAktivnostZetona() {
    	if(status == 1 && provjeriVrijemeZetona()) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    
	private long izvrsiDatumPretvaranje(String datum) {
	    long millisSinceEpoch = LocalDateTime.parse(datum, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
	            .atZone(ZoneId.systemDefault())
	            .toInstant()
	            .toEpochMilli();
	    
	    return millisSinceEpoch;
	}
}
