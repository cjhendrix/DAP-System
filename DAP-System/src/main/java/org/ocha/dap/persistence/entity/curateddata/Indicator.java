package org.ocha.dap.persistence.entity.curateddata;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;

/**
 * 
 * @author Samuel Eustachi
 * @author David Megginson
 * 
 *         a table listing specific values for indicators, with references to the {@link org.ocha.dap.persistence.entity.curateddata.Entity}
 *         table for the associated entity, to the {@link Source} table for the data source, and to the {@link IndicatorType} table for the
 *         specific indicator in question. This table will hold most of the data in the repository, and is expected to grow eventually into
 *         the millions of rows (or more).
 * 
 */
@Entity
@Table(name = "indicator", uniqueConstraints=
@UniqueConstraint(columnNames = {"source_id", "entity_id", "type_id", "start"}))
public class Indicator {

	public enum Periodicity {
		NONE, DAY, WEEK, MONTH, QUARTER, YEAR, FIVE_YEARS;
	}

	@Id
	private long id;

	@ManyToOne
	@ForeignKey(name = "fk_indicator_to_source")
	@JoinColumn(name = "source_id")
	private Source source;

	@ManyToOne
	@ForeignKey(name = "fk_indicator_to_entity")
	@JoinColumn(name = "entity_id")
	private org.ocha.dap.persistence.entity.curateddata.Entity entity;

	@ManyToOne
	@ForeignKey(name = "fk_indicator_to_type")
	@JoinColumn(name = "type_id")
	private IndicatorType type;

	@Column(name = "start", columnDefinition = "timestamp", nullable = false, updatable = false)
	private Date start;

	@Column(name = "end", columnDefinition = "timestamp", nullable = true, updatable = false)
	private Date end;

	@Column(name = "periodicity", nullable = false, updatable = true)
	@Enumerated(EnumType.STRING)
	private Periodicity periodicity;

	@Column(name = "numeric", nullable = false, updatable = true)
	private boolean numeric;

	@Column(name = "value", nullable = false, updatable = true)
	private String value;

	// will be used for soft delete
	// private int version;

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(final Source source) {
		this.source = source;
	}

	public org.ocha.dap.persistence.entity.curateddata.Entity getEntity() {
		return entity;
	}

	public void setEntity(final org.ocha.dap.persistence.entity.curateddata.Entity entity) {
		this.entity = entity;
	}

	public IndicatorType getType() {
		return type;
	}

	public void setType(final IndicatorType type) {
		this.type = type;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(final Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(final Date end) {
		this.end = end;
	}

	public Periodicity getPeriodicity() {
		return periodicity;
	}

	public void setPeriodicity(final Periodicity periodicity) {
		this.periodicity = periodicity;
	}

	public boolean isNumeric() {
		return numeric;
	}

	public void setNumeric(final boolean numeric) {
		this.numeric = numeric;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

}