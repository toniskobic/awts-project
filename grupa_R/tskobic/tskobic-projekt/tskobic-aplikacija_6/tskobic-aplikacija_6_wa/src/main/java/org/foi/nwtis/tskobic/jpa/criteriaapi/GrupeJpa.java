package org.foi.nwtis.tskobic.jpa.criteriaapi;

import java.util.List;

import org.foi.nwtis.tskobic.jpa.entiteti.Grupe;
import org.foi.nwtis.tskobic.jpa.entiteti.Grupe_;

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
public class GrupeJpa {
	@PersistenceContext(unitName = "NWTiS_tskobic-PU")
	private EntityManager em;
	private CriteriaBuilder cb;

	@PostConstruct
	private void init() {
		cb = em.getCriteriaBuilder();
	}

	public void create(Grupe grupe) {
		em.persist(grupe);
	}

	public void edit(Grupe grupe) {
		em.merge(grupe);
	}

	public void remove(Grupe grupe) {
		em.remove(em.merge(grupe));
	}

	public Grupe find(Object id) {
		return em.find(Grupe.class, id);
	}

	public List<Grupe> findAll() {
		CriteriaQuery<Grupe> cq = cb.createQuery(Grupe.class);
		cq.select(cq.from(Grupe.class));
		return em.createQuery(cq).getResultList();
	}

	public List<Grupe> findAll(String naziv) {
		CriteriaQuery<Grupe> cq = cb.createQuery(Grupe.class);
		Root<Grupe> grupe = cq.from(Grupe.class);
		Expression<String> zaNaziv = grupe.get(Grupe_.naziv);
		cq.where(cb.like(zaNaziv, naziv));
		TypedQuery<Grupe> q = em.createQuery(cq);
		return q.getResultList();
	}

	public List<Grupe> findRange(int odBroja, int broj) {
		CriteriaQuery<Grupe> cq = cb.createQuery(Grupe.class);
		cq.select(cq.from(Grupe.class));
		TypedQuery<Grupe> q = em.createQuery(cq);
		q.setMaxResults(broj);
		q.setFirstResult(odBroja);
		return q.getResultList();
	}

	public int count() {
		CriteriaQuery<Grupe> cq = cb.createQuery(Grupe.class);
		Root<Grupe> rt = cq.from(Grupe.class);
		cq.multiselect(cb.count(rt));
		Query q = em.createQuery(cq);
		return ((Long) q.getSingleResult()).intValue();
	}
}
