package org.foi.nwtis.tskobic.aplikacija_3.podaci;

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
}
