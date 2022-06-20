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

@Stateless
public class PutovanjaLetoviJpa {
	@PersistenceContext(unitName = "NWTiS_tskobic-PU")
	private EntityManager em;
	private CriteriaBuilder cb;

	@PostConstruct
	private void init() {
		cb = em.getCriteriaBuilder();
	}

	public void create(PutovanjaLetovi putovanjaLetovi) {
		em.persist(putovanjaLetovi);
	}

	public void edit(PutovanjaLetovi putovanjaLetovi) {
		em.merge(putovanjaLetovi);
	}

	public void remove(PutovanjaLetovi putovanjaLetovi) {
		em.remove(em.merge(putovanjaLetovi));
	}

	public PutovanjaLetovi find(Object id) {
		return em.find(PutovanjaLetovi.class, id);
	}

	public List<PutovanjaLetovi> findAll() {
		CriteriaQuery<PutovanjaLetovi> cq = cb.createQuery(PutovanjaLetovi.class);
		cq.select(cq.from(PutovanjaLetovi.class));
		return em.createQuery(cq).getResultList();
	}

	public List<PutovanjaLetovi> findAllAirplane(String icao24) {
		CriteriaQuery<PutovanjaLetovi> cq = cb.createQuery(PutovanjaLetovi.class);
		Root<PutovanjaLetovi> putovanjaLetovi = cq.from(PutovanjaLetovi.class);
		Expression<String> zaAvion = putovanjaLetovi.get(PutovanjaLetovi_.avion);
		cq.where(cb.like(zaAvion, icao24));
		TypedQuery<PutovanjaLetovi> q = em.createQuery(cq);
		return q.getResultList();
	}

	public List<PutovanjaLetovi> findRange(int odBroja, int broj) {
		CriteriaQuery<PutovanjaLetovi> cq = cb.createQuery(PutovanjaLetovi.class);
		cq.select(cq.from(PutovanjaLetovi.class));
		TypedQuery<PutovanjaLetovi> q = em.createQuery(cq);
		q.setMaxResults(broj);
		q.setFirstResult(odBroja);
		return q.getResultList();
	}

	public int count() {
		CriteriaQuery<PutovanjaLetovi> cq = cb.createQuery(PutovanjaLetovi.class);
		Root<PutovanjaLetovi> rt = cq.from(PutovanjaLetovi.class);
		cq.multiselect(cb.count(rt));
		Query q = em.createQuery(cq);
		return ((Long) q.getSingleResult()).intValue();
	}
}
