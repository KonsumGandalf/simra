package com.simra.konsumgandalf.valhalla.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValhallaEdge {

	@JsonProperty("way_id")
	Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
