package org.foi.nwtis.tskobic.aplikacija_6.jpa.criteriaapi;

import java.util.List;

import org.foi.nwtis.tskobic.aplikacija_6.jpa.entiteti.PutovanjaLetovi;
import org.foi.nwtis.tskobic.aplikacija_6.jpa.entiteti.PutovanjaLetovi_;

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
 * Klasa PutovanjaLetoviJpa za rad s bazom podataka.
 */
@Stateless
public class PutovanjaLetoviJpa {
	
	/** Entity manager. */
	@PersistenceContext(unitName = "NWTiS_tskobic-PU")
	private EntityManager em;
	
	/** Criteria Builder. */
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
	 * @param putovanjaLetovi putovanja letovi
	 */
	public void create(PutovanjaLetovi putovanjaLetovi) {
		em.persist(putovanjaLetovi);
	}

	/**
	 * Uređuje zapis.
	 *
	 * @param putovanjaLetovi putovanja letovi
	 */
	public void edit(PutovanjaLetovi putovanjaLetovi) {
		em.merge(putovanjaLetovi);
	}

	/**
	 * Briše zapis iz baze.
	 *
	 * @param putovanjaLetovi putovanja letovi
	 */
	public void remove(PutovanjaLetovi putovanjaLetovi) {
		em.remove(em.merge(putovanjaLetovi));
	}

	/**
	 * Pronalazi zapis po IDu.
	 *
	 * @param id id
	 * @return putovanja letovi
	 */
	public PutovanjaLetovi find(Object id) {
		return em.find(PutovanjaLetovi.class, id);
	}

	/**
	 * Pronalazi sve zapise.
	 *
	 * @return lista
	 */
	public List<PutovanjaLetovi> findAll() {
		CriteriaQuery<PutovanjaLetovi> cq = cb.createQuery(PutovanjaLetovi.class);
		cq.select(cq.from(PutovanjaLetovi.class));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * Vraća sve avione.
	 *
	 * @param icao24 icao
	 * @return lista
	 */
	public List<PutovanjaLetovi> findAllAirplane(String icao24) {
		CriteriaQuery<PutovanjaLetovi> cq = cb.createQuery(PutovanjaLetovi.class);
		Root<PutovanjaLetovi> putovanjaLetovi = cq.from(PutovanjaLetovi.class);
		Expression<String> zaAvion = putovanjaLetovi.get(PutovanjaLetovi_.avion);
		cq.where(cb.like(zaAvion, icao24));
		TypedQuery<PutovanjaLetovi> q = em.createQuery(cq);
		return q.getResultList();
	}

	/**
	 * Vraća zapise u rasponu.
	 *
	 * @param odBroja od broja
	 * @param broj broj
	 * @return lista
	 */
	public List<PutovanjaLetovi> findRange(int odBroja, int broj) {
		CriteriaQuery<PutovanjaLetovi> cq = cb.createQuery(PutovanjaLetovi.class);
		cq.select(cq.from(PutovanjaLetovi.class));
		TypedQuery<PutovanjaLetovi> q = em.createQuery(cq);
		q.setMaxResults(broj);
		q.setFirstResult(odBroja);
		return q.getResultList();
	}

	/**
	 * Vraća broj zapisa.
	 *
	 * @return int
	 */
	public int count() {
		CriteriaQuery<PutovanjaLetovi> cq = cb.createQuery(PutovanjaLetovi.class);
		Root<PutovanjaLetovi> rt = cq.from(PutovanjaLetovi.class);
		cq.multiselect(cb.count(rt));
		Query q = em.createQuery(cq);
		return ((Long) q.getSingleResult()).intValue();
	}
}
