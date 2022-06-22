package org.foi.nwtis.tskobic.aplikacija_6.jpa.criteriaapi;

import java.util.List;

import org.foi.nwtis.tskobic.aplikacija_6.jpa.entiteti.Grupe;
import org.foi.nwtis.tskobic.aplikacija_6.jpa.entiteti.Grupe_;

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
 * Klasa GrupeJpa za rad s bazom podataka.
 */
@Stateless
public class GrupeJpa {
	
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
	 * Kreira zapis u bazi.
	 *
	 * @param grupe grupe
	 */
	public void create(Grupe grupe) {
		em.persist(grupe);
	}

	/**
	 * Uređuje zapis.
	 *
	 * @param grupe grupe
	 */
	public void edit(Grupe grupe) {
		em.merge(grupe);
	}

	/**
	 * Briše zapis.
	 *
	 * @param grupe grupe
	 */
	public void remove(Grupe grupe) {
		em.remove(em.merge(grupe));
	}

	/**
	 * Pronalazi zapis po IDu.
	 *
	 * @param id id
	 * @return grupe
	 */
	public Grupe find(Object id) {
		return em.find(Grupe.class, id);
	}

	/**
	 * Vraća sve zapise iz baze.
	 *
	 * @return lista
	 */
	public List<Grupe> findAll() {
		CriteriaQuery<Grupe> cq = cb.createQuery(Grupe.class);
		cq.select(cq.from(Grupe.class));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * Pronalazi sve zapise na temelju proslijeđenog naziva.
	 *
	 * @param naziv naziv
	 * @return lista
	 */
	public List<Grupe> findAll(String naziv) {
		CriteriaQuery<Grupe> cq = cb.createQuery(Grupe.class);
		Root<Grupe> grupe = cq.from(Grupe.class);
		Expression<String> zaNaziv = grupe.get(Grupe_.naziv);
		cq.where(cb.like(zaNaziv, naziv));
		TypedQuery<Grupe> q = em.createQuery(cq);
		return q.getResultList();
	}

	/**
	 * Vraća zapise u rasponu.
	 *
	 * @param odBroja od broja
	 * @param broj broj
	 * @return lista
	 */
	public List<Grupe> findRange(int odBroja, int broj) {
		CriteriaQuery<Grupe> cq = cb.createQuery(Grupe.class);
		cq.select(cq.from(Grupe.class));
		TypedQuery<Grupe> q = em.createQuery(cq);
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
		CriteriaQuery<Grupe> cq = cb.createQuery(Grupe.class);
		Root<Grupe> rt = cq.from(Grupe.class);
		cq.multiselect(cb.count(rt));
		Query q = em.createQuery(cq);
		return ((Long) q.getSingleResult()).intValue();
	}
}
