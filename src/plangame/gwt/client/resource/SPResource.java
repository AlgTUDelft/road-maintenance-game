/**
 * @file SPResource.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright © 2013 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         9 apr. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.resource;

import plangame.gwt.client.resource.css.SPCSS;

import com.google.gwt.core.shared.GWT;


/**
 * Service Provider specific resources
 *
 * @author Joris Scharpff
 */
public interface SPResource extends ClientResource {
	/** The client bundle instance */
	public static final SPResource INSTANCE = GWT.create( SPResource.class );
	
	/** @return The SP CSS rules */
	@Source("css/ServiceProvider.css")
	SPCSS spcss( );
}
