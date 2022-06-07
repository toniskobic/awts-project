package org.foi.nwtis.tskobic.vjezba_06.konfiguracije.bazaPodataka;

import java.util.Properties;
import org.foi.nwtis.tskobic.vjezba_03.konfiguracije.Konfiguracija;

/**
 * Sučelje za rad s konfiguracijskim podacima za bazu podataka
 */
public interface KonfiguracijaBP extends Konfiguracija {
    
    /**
     * Vraća naziv baze podataka za administratorski rad
     *
     * @return vraća naziv baze podataka
     */
    String getAdminDatabase();
    
    /**
     * Vraća lozinku za adinistratorski rad
     *
     * @return vraća lozinku
     */
    String getAdminPassword();
    
    /**
     * Vraća korisničko ime za adinistratorski rad
     *
     * @return korisničko ime
     */
    String getAdminUsername();
    
    /**
     * Vraća naziv upravljačkog programa za rad s bazom podataka
     *
     * @return naziv upravljačkog programa
     */
    String getDriverDatabase();
    
    /**
     * Vraća naziv upravljačkog programa za rad s bazom podataka iz argumenta
     *
     * @param urlBazePodataka URL baze podataka
     * @return naziv upravljačkog programa
     */
    String getDriverDatabase(String urlBazePodataka);
    
    /**
     * Vraća nazive svih upravljačkih programa 
     *
     * @return nazivi upravljačih programa
     */
    Properties getDriversDatabase();
    
    /**
     * Vraća naziv poslužitelja za bazu podataka
     *
     * @return naziv poslužitelja
     */
    String getServerDatabase();
    
    /**
     * Vraća naziv baze podataka za korisnički rad
     *
     * @return naziv baze podataka
     */
    String getUserDatabase();
    
    /**
     * Vraća lozinku za korisnički rad 
     *
     * @return korisnička lozinka
     */
    String getUserPassword();
    
    /**
     * Vraća korisničko ime za korisnički rad 
     *
     * @return korisničko ime
     */
    String getUserUsername();    
}
