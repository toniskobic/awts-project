package org.foi.nwtis.tskobic.aplikacija_3.podaci;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Klasa Posluzitelj.
 *
 * @param adresa adresa
 * @param port port
 */
@AllArgsConstructor
public class Posluzitelj {

	@Getter
	@Setter
	@NotNull
	String adresa;

	@Getter
	@Setter
	int port;
}
