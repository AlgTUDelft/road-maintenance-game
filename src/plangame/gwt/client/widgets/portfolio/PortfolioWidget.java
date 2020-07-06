/**
 * @file PortfolioWidget.java
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
 * @date         13 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.portfolio;

import plangame.model.tasks.Portfolio;
import plangame.model.tasks.Task;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

// FIXME replace by Cell List

/**
 * Displays client portfolio
 * 
 * @author Joris Scharpff
 */
public class PortfolioWidget extends Composite {
	protected interface PortfolioUIBinder extends UiBinder<Widget, PortfolioWidget> {}
	protected final PortfolioUIBinder PortfolioUI = GWT.create( PortfolioUIBinder.class );
	
	/** The portfolio that is displayed */
	protected Portfolio portfolio;
	
	/** The HTML components */
	@UiField protected VerticalPanel pnlTasks;
	@UiField protected VerticalPanel pnlDetails;
	
	/**
	 * Creates a new PortfolioWidget
	 */
	public PortfolioWidget( ) {
		super( );
		
		initWidget( PortfolioUI.createAndBindUi( this ) );
		
		// add empty panel to details panel for styling
		pnlDetails.add( new PortfolioDetails( ) );
	}
	
	/**
	 * Sets the portfolio displayed in the widget
	 * 
	 * @param portfolio The portfolio
	 */
	public void setPortfolio( Portfolio portfolio ) {
		this.portfolio = portfolio;
		
		repaint( );
	}
	
	/** @return The portfolio of the widget */
	public Portfolio getPortfolio( ) { return portfolio; }
	
	/**
	 * Update the view, only for player views
	 */
	protected void repaint( ) {
		pnlTasks.clear( );
		if( getPortfolio( ) == null ) return;
		
		// add all tasks of the portfolio
		for( Task t : getPortfolio( ).getTasks( ) ) {
			pnlTasks.add( createTaskItem( t ) );
		}
	}
	
	/**
	 * Creates a new item to display a task in the list
	 * 
	 * @param task The task to create an item for
	 * @return The task info item (a panel)
	 */
	private PortfolioItem createTaskItem( final Task task ) {
		final PortfolioItem p = new PortfolioItem( );
		p.setTask( task );
		p.setHandler( new PortfolioItemHandler( ) {
			@Override
			public void onSelect( Task task ) {
				showDetails( task );
			}
		} );
		
		return p;
	}
	
	/**
	 * Shows the task details for the selected task
	 * 
	 * @param task The selected task
	 */
	private void showDetails( Task task ) {
		pnlDetails.clear( );
		final PortfolioDetails p = new PortfolioDetails( );
		p.setTask( task );
		pnlDetails.add( p );
	}
}
