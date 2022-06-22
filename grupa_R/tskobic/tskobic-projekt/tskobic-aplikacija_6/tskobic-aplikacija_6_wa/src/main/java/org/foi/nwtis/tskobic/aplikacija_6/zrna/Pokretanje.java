package org.foi.nwtis.tskobic.aplikacija_6.zrna;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.inject.Qualifier;

/**
 * Sučelje Pokretanje
 */
@Qualifier
@Retention(RUNTIME)
@Target(TYPE)
public @interface Pokretanje {
}