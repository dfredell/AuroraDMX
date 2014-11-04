/*
 * Copyright 2012 Corentin Azelart.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.azelart.artnetstack.domain.controller;

import java.util.Map;

import fr.azelart.artnetstack.domain.artnet.ArtNetObject;

/**
 * General controler.
 * @author Corentin Azelart.
 */
public class Controller extends ArtNetObject {

	/** Port mapping. */
	private Map<Integer, ControllerPortType> portTypeMap;

	/** GoodInput mapping. */
	private Map<Integer, ControllerGoodInput> goodInputMapping;

	/** GoodOutput mapping. */
	private Map<Integer, ControllerGoodOutput> goodOutputMapping;

	/**
	 * Set to false when video display is showing local data.
	 * Set to true when video is showing ethernet data.
	 */
	private Boolean screen;

	/**
	 * Network.
	 */
	private String network;

	/**
	 * Subnetwork.
	 */
	private String subNetwork;

	/**
	 * @return the goodInputMapping
	 */
	public final Map<Integer, ControllerGoodInput> getGoodInputMapping() {
		return goodInputMapping;
	}

	/**
	 * @param goodInputMapping the goodInputMapping to set
	 */
	public final void setGoodInputMapping(final Map<Integer, ControllerGoodInput> goodInputMapping) {
		this.goodInputMapping = goodInputMapping;
	}

	/**
	 * Controler.
	 */
	public Controller() {
		super();
	}

	/**
	 * @return the portTypeMap
	 */
	public final Map<Integer, ControllerPortType> getPortTypeMap() {
		return portTypeMap;
	}

	/**
	 * @param portTypeMap the portTypeMap to set
	 */
	public final void setPortTypeMap(final Map<Integer, ControllerPortType> portTypeMap) {
		this.portTypeMap = portTypeMap;
	}

	/**
	 * @return the goodOutputMapping
	 */
	public final Map<Integer, ControllerGoodOutput> getGoodOutputMapping() {
		return goodOutputMapping;
	}

	/**
	 * @param goodOutputMapping the goodOutputMapping to set
	 */
	public final void setGoodOutputMapping(final Map<Integer, ControllerGoodOutput> goodOutputMapping) {
		this.goodOutputMapping = goodOutputMapping;
	}

	/**
	 * @return the screen
	 */
	public final Boolean getScreen() {
		return screen;
	}

	/**
	 * Set to false when video display is showing local data.
	 * Set to true when video is showing ethernet data.
	 * @param screen the screen to set
	 */
	public final void setScreen(final Boolean screen) {
		this.screen = screen;
	}

	/**
	 * @return the network
	 */
	public String getNetwork() {
		return network;
	}

	/**
	 * @param network the network to set
	 */
	public void setNetwork(final String network) {
		this.network = network;
	}

	/**
	 * @return the subNetwork
	 */
	public String getSubNetwork() {
		return subNetwork;
	}

	/**
	 * @param subNetwork the subNetwork to set
	 */
	public void setSubNetwork(final String subNetwork) {
		this.subNetwork = subNetwork;
	}
}