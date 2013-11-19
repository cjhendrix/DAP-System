package org.ocha.dap.persistence.dao.currateddata;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.ocha.dap.persistence.entity.ImportFromCKAN;
import org.ocha.dap.persistence.entity.curateddata.Entity;
import org.ocha.dap.persistence.entity.curateddata.Indicator;
import org.ocha.dap.persistence.entity.curateddata.Indicator.Periodicity;
import org.ocha.dap.persistence.entity.curateddata.IndicatorType;
import org.ocha.dap.persistence.entity.curateddata.Source;
import org.springframework.transaction.annotation.Transactional;

public class IndicatorDAOImpl implements IndicatorDAO {

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<Indicator> listLastIndicators(final int limit) {
		final TypedQuery<Indicator> query = em.createQuery("SELECT i FROM Indicator i ORDER BY i.id", Indicator.class).setMaxResults(limit);
		return query.getResultList();
	}

	@Override
	@Transactional
	public void addIndicator(final Source source, final Entity entity, final IndicatorType type, final Date start, final Date end, final Periodicity periodicity, final boolean numeric,
			final String value, final String initialValue, final ImportFromCKAN importFromCKAN) {

		final Indicator indicator = new Indicator();
		indicator.setSource(source);
		indicator.setEntity(entity);
		indicator.setType(type);
		indicator.setStart(start);
		indicator.setEnd(end);
		indicator.setPeriodicity(periodicity);
		indicator.setNumeric(numeric);
		indicator.setValue(value);
		indicator.setInitialValue(initialValue);
		indicator.setImportFromCKAN(importFromCKAN);
		em.persist(indicator);

	}

	@Override
	@Transactional
	public void deleteAllIndicators() {
		em.createQuery("DELETE FROM Indicator").executeUpdate();
	}

}