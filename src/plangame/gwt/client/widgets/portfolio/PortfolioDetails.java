/**
 * @file PortfolioDetails.java
 * @brief [brief description]
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright � 2012 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         17 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.portfolio;

import plangame.model.tasks.Task;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Joris Scharpff
 */
public class PortfolioDetails extends Composite {
	protected interface PortfolioDetailsUIBinder extends UiBinder<Widget, PortfolioDetails> { }
	private final PortfolioDetailsUIBinder PortfolioDetailsUI = GWT.create( PortfolioDetailsUIBinder.class );
	
	/** The task */
	protected Task task;
	
	/** The HTML components */
	@UiField protected VerticalPanel pnlItem;
	@UiField protected Label lblTaskName;
	
	/**
	 * Creates a PortfolioDetails panel
	 */
	public PortfolioDetails( ) {
		initWidget( PortfolioDetailsUI.createAndBindUi( this ) );
	}
	
	/**
	 * Set the task
	 * 
	 * @param task The task
	 */
	public void setTask( Task task ) {
		this.task = task;
		
		lblTaskName.setText( task + "  TODO: rest of the details" );
	}	
}
