package org.foi.nwtis.tskobic.aplikacija_6.jpa.criteriaapi;

import java.util.List;

import org.foi.nwtis.tskobic.aplikacija_6.jpa.entiteti.Korisnici;
import org.foi.nwtis.tskobic.aplikacija_6.jpa.entiteti.Korisnici_;

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
 * Klasa KorisniciJpa za rad s bazom podataka.
 */
@Stateless
public class KorisniciJpa {
	
	/** Entity manager. */
	@PersistenceContext(unitName = "NWTiS_tskobic-PU")
	private EntityManager em;
	
	/** Criteria builder. */
	private CriteriaBuilder cb;

	/**
	 * Izvršava inicijalizaciju.
	 */
	@PostConstruct
	public void init() {
		cb = em.getCriteriaBuilder();
	}

	/**
	 * Kreira zapis u bazi.
	 *
	 * @param korisnici korisnici
	 */
	public void create(Korisnici korisnici) {
		em.persist(korisnici);
	}

	/**
	 * Uređuje zapis.
	 *
	 * @param korisnici korisnici
	 */
	public void edit(Korisnici korisnici) {
		em.merge(korisnici);
	}

	/**
	 * Briše zapis.
	 *
	 * @param korisnici korisnici
	 */
	public void remove(Korisnici korisnici) {
		em.remove(em.merge(korisnici));
	}

	/**
	 * Pronalazi zapis po IDu.
	 *
	 * @param id id
	 * @return korisnici
	 */
	public Korisnici find(Object id) {
		return em.find(Korisnici.class, id);
	}

	/**
	 * Vraća sve zapise u bazi.
	 *
	 * @return lista
	 */
	public List<Korisnici> findAll() {
		CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
		cq.select(cq.from(Korisnici.class));
		return em.createQuery(cq).getResultList();
	}

	/**
	 * Pronalazi sve zapise na temelju imena i prezimena.
	 *
	 * @param prezime prezime
	 * @param ime ime
	 * @return lista
	 */
	public List<Korisnici> findAll(String prezime, String ime) {
		CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
		Root<Korisnici> korisnici = cq.from(Korisnici.class);
		Expression<String> zaPrezime = korisnici.get(Korisnici_.prezime);
		Expression<String> zaIme = korisnici.get(Korisnici_.ime);
		cq.where(cb.and(cb.like(zaPrezime, prezime), cb.like(zaIme, ime)));
		TypedQuery<Korisnici> q = em.createQuery(cq);
		return q.getResultList();
	}

	/**
	 * Vraća zapise u rasponu.
	 *
	 * @param odBroja od broja
	 * @param broj broj
	 * @return lista
	 */
	public List<Korisnici> findRange(int odBroja, int broj) {
		CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
		cq.select(cq.from(Korisnici.class));
		TypedQuery<Korisnici> q = em.createQuery(cq);
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
		CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
		Root<Korisnici> rt = cq.from(Korisnici.class);
		cq.multiselect(cb.count(rt));
		Query q = em.createQuery(cq);
		return ((Long) q.getSingleResult()).intValue();
	}
}
