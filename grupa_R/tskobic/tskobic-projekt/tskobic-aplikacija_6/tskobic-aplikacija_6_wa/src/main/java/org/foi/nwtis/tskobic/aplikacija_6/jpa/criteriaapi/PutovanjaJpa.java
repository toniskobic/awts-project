package org.foi.nwtis.tskobic.aplikacija_6.jpa.criteriaapi;

import java.util.List;

import org.foi.nwtis.tskobic.aplikacija_6.jpa.entiteti.Putovanja;
import org.foi.nwtis.tskobic.aplikacija_6.jpa.entiteti.Putovanja_;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;

/**
 * Klasa PutovanjaJpa za rad s bazom podataka.
 */
@Stateless
public class PutovanjaJpa {
	
	/** Entity manager. */
	@PersistenceContext(unitName = "NWTiS_tskobic-PU")
	private EntityManager em;
	
	/** Criteria builder. */
	private CriteriaBuilder cb;

	/**
	 * Izvršava inicijalizaciju.
	 */
	@PostConstruct
	private void init() {
		cb = em.getCriteriaBuilder();
	}

	/**
	 * Stvara zapis u bazi.
	 *
	 * @param putovanja putovanja
	 */
	public void create(Putovanja putovanja) {
		em.persist(putovanja);
	}

	/**
	 * Uređuje zapis.
	 *
	 * @param putovanja putovanja
	 */
	public void edit(Putovanja putovanja) {
		em.merge(putovanja);
	}

	/**
	 * Briše zapis iz baze.
	 *
	 * @param putovanja putovanja
	 */
	public void remove(Putovanja putovanja) {
		em.remove(em.merge(putovanja));
	}

	/**
	 * Pronalazi zapis na temelju IDa.
	 *
	 * @param id id
	 * @return putovanja
	 */
	public Putovanja find(Object id) {
		return em.find(Putovanja.class, id);
	}

	/**
	 * Vraća sve zapise iz baze.
	 *
	 * @return lista
	 */
	public List<Putovanja> findAll() {
		CriteriaQuery<Putovanja> cq = cb.createQuery(Putovanja.class);
		cq.select(cq.from(Putovanja.class));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * Vraća sve polaske.
	 *
	 * @param icao icao
	 * @return lista
	 */
	public List<Putovanja> findAllDeparture(String icao) {
		CriteriaQuery<Putovanja> cq = cb.createQuery(Putovanja.class);
		Root<Putovanja> putovanja = cq.from(Putovanja.class);
		Expression<String> zaAerodrom = putovanja.get(Putovanja_.aerodrompocetni);
		cq.where(cb.like(zaAerodrom, icao));
		TypedQuery<Putovanja> q = em.createQuery(cq);
		return q.getResultList();
	}

	/**
	 * Vraća sve dolaske.
	 *
	 * @param icao icao
	 * @return lista
	 */
	public List<Putovanja> findAllArrival(String icao) {
		CriteriaQuery<Putovanja> cq = cb.createQuery(Putovanja.class);
		Root<Putovanja> putovanja = cq.from(Putovanja.class);
		Expression<String> zaAerodrom = putovanja.get(Putovanja_.aerodromzavrsni);
		cq.where(cb.like(zaAerodrom, icao));
		TypedQuery<Putovanja> q = em.createQuery(cq);
		return q.getResultList();
	}

	/**
	 * Vraća zapise u rasponu.
	 *
	 * @param odBroja od broja
	 * @param broj broj
	 * @return lista
	 */
	public List<Putovanja> findRange(int odBroja, int broj) {
		CriteriaQuery<Putovanja> cq = cb.createQuery(Putovanja.class);
		cq.select(cq.from(Putovanja.class));
		TypedQuery<Putovanja> q = em.createQuery(cq);
		q.setMaxResults(broj);
		q.setFirstResult(odBroja);
		return q.getResultList();
	}

	/**
	 * Vraća broj zapisa.
	 *
	 * @return broj
	 */
	public int count() {
		CriteriaQuery<Putovanja> cq = cb.createQuery(Putovanja.class);
		Root<Putovanja> rt = cq.from(Putovanja.class);
		cq.multiselect(cb.count(rt));
		Query q = em.createQuery(cq);
		return ((Long) q.getSingleResult()).intValue();
	}
}
