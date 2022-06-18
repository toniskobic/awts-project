package org.foi.nwtis.tskobic.aplikacija_4.podaci;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

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
