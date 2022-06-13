package org.foi.nwtis.tskobic.aplikacija_3.podaci;

import lombok.Getter;
import lombok.Setter;
import lombok.NonNull;

import java.util.Date;

import lombok.AllArgsConstructor;

/**
*
* Klasa za aerodrom problem
*/
@AllArgsConstructor
public class AerodromProblem {
    @Getter
    @Setter 
    @NonNull 
    private String icao;
    
    @Getter
    @Setter 
    @NonNull
    private String opis;
    
    @Getter
    @Setter 
    @NonNull
    private Date vrijeme;
}
