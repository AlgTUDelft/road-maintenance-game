/**
 * @file ClientResource.java
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

import plangame.gwt.client.resource.css.ClientCSS;
import plangame.gwt.client.resource.css.ClientManagerCSS;
import plangame.gwt.client.resource.css.EndGameDialogCSS;
import plangame.gwt.client.resource.css.FinanceWidgetCSS;
import plangame.gwt.client.resource.css.HelpWidgetCSS;
import plangame.gwt.client.resource.css.MechanismWidgetCSS;
import plangame.gwt.client.resource.css.MethodDialogCSS;
import plangame.gwt.client.resource.css.PlanWidgetCSS;
import plangame.gwt.client.resource.css.RequestSuggestionDialogCSS;
import plangame.gwt.client.resource.css.ServerManagerCSS;
import plangame.gwt.client.resource.css.StartGameDialogCSS;
import plangame.gwt.client.resource.css.TTLTimelineWidgetCSS;
import plangame.gwt.client.resource.css.TTLTableWidgetCSS;
import plangame.gwt.client.resource.css.TaskInfoWidgetCss;

import com.google.gwt.resources.client.ClientBundle;

/**
 * Base resources shared by all clients
 *
 * @author Joris Scharpff
 */
public interface ClientResource extends ClientBundle {
	/**
	 * @return The client CSS rules
	 */
	@Source("css/Client.css")
	ClientCSS clientcss( );
	
	/**
	 * @return planwidget CSS rules
	 */
	@Source("css/PlanWidget.css")
	PlanWidgetCSS plancss( );
	
	/**
	 * @return Help widget CSS
	 */
	@Source("css/HelpWidget.css")
	HelpWidgetCSS helpcss( );
	
	/**
	 * @return RequestSuggestionDialog CSS
	 */
	@Source("css/RequestSuggestionDialog.css")
	RequestSuggestionDialogCSS requestdialogcss( );
	
	/**
	 * @return Task info widget CSS
	 */
	@Source("css/TaskInfoWidget.css")
	TaskInfoWidgetCss taskinfocss( );

	/**
	 * @return Client manager widget CSS
	 */
	@Source("css/ClientManagerWidget.css")
	ClientManagerCSS clientmanagercss( );
	
	/**
	 * @return Server manager widget CSS
	 */
	@Source("css/ServerManagerWidget.css")
	ServerManagerCSS servermanagercss( );
	
	/**
	 * @return Method dialog
	 */
	@Source("css/MethodDialog.css")
	MethodDialogCSS methoddialogcss( );
	
	/** @return The start game dialog CSS */
	@Source("css/StartDialog.css")
	StartGameDialogCSS startdialogcss( );

	/** @return The finance widget */
	@Source("css/FinanceWidget.css")
	FinanceWidgetCSS financecss( );
	
	/** @return The TTL widget */
	@Source("css/TTLWidget.css")
	TTLTableWidgetCSS ttlcss( );

	/** @return The mechanism payment widget */
	@Source("css/MechanismWidget.css")
	MechanismWidgetCSS mechcss( );
	
	/** @return The TTL timeline widget CSS */
	@Source("css/TTLTimelineWidget.css")
	TTLTimelineWidgetCSS plotcss( );
	
	/** @return The end game dialog CSS */
	@Source("css/EndGameDialog.css")
	EndGameDialogCSS endgamedialogcss( );
}
