package org.foi.nwtis.tskobic.aplikacija_5.podaci;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Klasa ZetonOdgovor.
 *
 * @param zeton zeton
 * @param vrijeme vrijeme
 */
@AllArgsConstructor
public class ZetonOdgovor {
    @Getter
    @Setter
    private int zeton;
    
    @Getter
    @Setter
    @NonNull
    private String vrijeme;
}
