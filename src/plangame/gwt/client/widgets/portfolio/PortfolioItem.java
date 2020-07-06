/**
 * @file PortfolioItem.java
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Joris Scharpff
 */
public class PortfolioItem extends Composite {
	protected interface PortfolioItemUIBinder extends UiBinder<Widget, PortfolioItem> { }
	private final PortfolioItemUIBinder PortfolioItemUI = GWT.create( PortfolioItemUIBinder.class );
	
	/** The task */
	protected Task task;
	
	/** The item even handler */
	protected PortfolioItemHandler handler;
	
	/** The HTML components */
	@UiField protected VerticalPanel pnlItem;
	@UiField protected FocusPanel fcpItem;
	@UiField protected Label lblTaskName;
	
	/**
	 * Creates a PortfolioItem
	 */
	public PortfolioItem( ) {
		super( );
		
		initWidget( PortfolioItemUI.createAndBindUi( this ) );
	}
	
	/**
	 * Set the task
	 * 
	 * @param task The task
	 */
	public void setTask( Task task ) {
		this.task = task;
		
		lblTaskName.setText( task.toString( ) );
	}
	
	/**
	 * Sets the event handler for the item
	 * 
	 * @param handler The event handler
	 */
	public void setHandler( PortfolioItemHandler handler ) {
		this.handler = handler;
	}
	
	/**
	 * Click handler for the item
	 * 
	 * @param event
	 */
	@UiHandler("fcpItem")
	protected void onClick( ClickEvent event ) {
		if( handler != null )
			handler.onSelect( task );
	}
	
}
