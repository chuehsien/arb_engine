package com.dapp.opti.domain;

import org.optaplanner.core.api.domain.lookup.PlanningId;

public abstract class EntityWithId {

	protected Long id;

	@PlanningId
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {

		if (o == this)
			return true;
		if (!(o instanceof EntityWithId)) {
			return false;
		}

		EntityWithId d = (EntityWithId) o;

		return d.getId().equals(this.getId());
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + getId().hashCode();
		return result;
	}
}
