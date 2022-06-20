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

@Stateless
public class KorisniciJpa {
	@PersistenceContext(unitName = "NWTiS_tskobic-PU")
	private EntityManager em;
	private CriteriaBuilder cb;

	@PostConstruct
	public void init() {
		cb = em.getCriteriaBuilder();
	}

	public void create(Korisnici korisnici) {
		em.persist(korisnici);
	}

	public void edit(Korisnici korisnici) {
		em.merge(korisnici);
	}

	public void remove(Korisnici korisnici) {
		em.remove(em.merge(korisnici));
	}

	public Korisnici find(Object id) {
		return em.find(Korisnici.class, id);
	}

	public List<Korisnici> findAll() {
		CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
		cq.select(cq.from(Korisnici.class));
		return em.createQuery(cq).getResultList();
	}

	public List<Korisnici> findAll(String prezime, String ime) {
		CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
		Root<Korisnici> korisnici = cq.from(Korisnici.class);
		Expression<String> zaPrezime = korisnici.get(Korisnici_.prezime);
		Expression<String> zaIme = korisnici.get(Korisnici_.ime);
		cq.where(cb.and(cb.like(zaPrezime, prezime), cb.like(zaIme, ime)));
		TypedQuery<Korisnici> q = em.createQuery(cq);
		return q.getResultList();
	}

	public List<Korisnici> findRange(int odBroja, int broj) {
		CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
		cq.select(cq.from(Korisnici.class));
		TypedQuery<Korisnici> q = em.createQuery(cq);
		q.setMaxResults(broj);
		q.setFirstResult(odBroja);
		return q.getResultList();
	}

	public int count() {
		CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
		Root<Korisnici> rt = cq.from(Korisnici.class);
		cq.multiselect(cb.count(rt));
		Query q = em.createQuery(cq);
		return ((Long) q.getSingleResult()).intValue();
	}
}
