package org.foi.nwtis.tskobic.aplikacija_3.podaci;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Klasa Grupa.
 *
 * @param grupa grupa
 * @param naziv naziv
 */
@AllArgsConstructor
public class Grupa {

    @Getter
    @Setter
    @NonNull
    private String grupa;
    
    @Getter
    @Setter
    @NonNull
    private String naziv;
}
