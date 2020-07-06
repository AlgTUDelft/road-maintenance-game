/**
 * @file TTLTimelineWidgetCSS.java
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
 * @date         16 sep. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.resource.css;

import com.google.gwt.resources.client.CssResource;

/**
 * CSS for the TTLTimeline widget
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("javadoc")
public interface TTLTimelineWidgetCSS extends CssResource {
	@ClassName("mainpanel") String mainpanel( );
	@ClassName("legend-entry") String legend_entry( );
	@ClassName("legend-colour") String legend_colour( );
	@ClassName("legend-text") String legend_text( );
	@ClassName("legend-text-notdrawn") String legend_text_notdrawn( );
	@ClassName("xaxis-label") String xaxis_label( );
	@ClassName("xaxis-caption") String xaxis_caption( );
	@ClassName("yaxis-label") String yaxis_label( );
	@ClassName("yaxis-caption") String yaxis_caption( );
}
