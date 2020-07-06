/**
 * @file TaskPanel.java
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
 * @date         30 aug. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.controls;

import java.util.ArrayList;
import java.util.Collection;

import plangame.model.object.ObjectMap;
import plangame.model.tasks.Task;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Control to allow for easy task selection
 *
 * @author Joris Scharpff
 */
public class TaskSelect extends Composite implements HasValueChangeHandlers<Task> {
	/** Currently active task */
	protected Task activetask;
	
	/** Panel containing selectable labels */
	protected Panel pnlLabels;

	/** The tasks and mapped select labels */
	protected ObjectMap<Task, HyperLabel> tasklabels;
			
	/** The all label */
	protected HyperLabel alllabel;
	
	/** The control orientation */
	protected TaskSelectOrientation orientation;
	
	/** Possible control orientations */
	public enum TaskSelectOrientation {
		/** Displays the task labels next to each other */
		Horizontal,
		/** Displays the task labels on top of each other */
		Vertical
	}
	
	/**
	 * Creates a new Task Panel
	 */
	public TaskSelect( ) {
		// initialise task label map
		tasklabels = new ObjectMap<Task, HyperLabel>( );
		
		// create the panel container for the labels
		pnlLabels = new HorizontalPanel( );
		
		// create initial all label
		alllabel = new HyperLabel( );
		alllabel = createLabel( null );

		// set default orientation
		setOrientation( TaskSelectOrientation.Horizontal );
		
		// setup initial labels
		setTasks( new ArrayList<Task>( ) );
		
		// initialise the widget
		initWidget( pnlLabels );
	}

	/**
	 * Sets the orientation of the panel
	 * 
	 * @param orientation The task label display orientation
	 */
	// FIXME does not work because the panel is not replaced in the DOM
	public void setOrientation( TaskSelectOrientation orientation ) {
		// no need to update control if this hasn't changed
		if( orientation == this.orientation ) return;
		this.orientation = orientation;
		
		// change panel to new orientation
		pnlLabels = (orientation == TaskSelectOrientation.Horizontal ? new HorizontalPanel( ) : new VerticalPanel( ) );
		for( HyperLabel t : tasklabels.getValues( ) ) 
			pnlLabels.add( t );
		pnlLabels.add( alllabel );
		
		// set CSS class
		pnlLabels.addStyleName( "taskselect" );
	}
	
	/**
	 * Sets the tasks that are to be displayed on the control
	 * 
	 * @param tasks The list of tasks to display 
	 */
	public void setTasks( Collection<Task> tasks ) {
		// add all tasks
		pnlLabels.clear( );
		tasklabels.clear( );
		for( Task t : tasks ) {
			// create the labels
			final HyperLabel lbl = createLabel( t );
			pnlLabels.add( lbl );
			tasklabels.put( t, lbl );
		}
		
		// add all label
		pnlLabels.add( alllabel );
		
		// set selected task to first (or all if no tasks )
		if( tasks.size( ) > 0 )
			setActiveTask( tasks.iterator( ).next( ), true );
		else
			setActiveTask( null, true );
	}
	
	/**
	 * @return The list of tasks displayed in the task select
	 */
	public Collection<Task> getTasks( ) {
		return tasklabels.getKeys( );
	}
	
	/**
	 * Creates a new label for the task
	 * 
	 * @param task The task or null for the 'all' label
	 * @return The created label
	 */
	private HyperLabel createLabel( final Task task ) {
		// create a label
		final HyperLabel tab = new HyperLabel( );
		tab.setText( task != null ? task.getDescription( ) : getAllText( ) );
		tab.setHoverable( true );
		tab.getElement( ).getStyle( ).setBorderColor( "black" );
		tab.getElement( ).getStyle( ).setBorderWidth( 1.0, Unit.PT );
		tab.addStyleName( "taskselect-label" );
		
		// create event
		tab.addClickHandler( new ClickHandler( ) {
			@Override
			public void onClick( ClickEvent event ) {
				setActiveTask( task, true );
			}
		} );
		
		return tab;
	}
	
	/**
	 * Gets the label for the task
	 * 
	 * @param task The task, null for the all label
	 * @return The label corresponding to the task
	 */
	private HyperLabel getLabel( Task task ) {
		if( task == null ) return alllabel;
		return tasklabels.get( task );
	}
	
	/**
	 * Sets the text displayed on the all label
	 * 
	 * @param text The text
	 */
	public void setAllText( String text ) {
		alllabel.setText( text );
	}
	
	/** @return The text displayed on the all label */
	public String getAllText( ) { return alllabel.getText( ); }
	
	/**
	 * Shows or hides the all label
	 * 
	 * @param visible True to show the all label
	 */
	public void setAllVisible( boolean visible ) {
		alllabel.setVisible( visible );
	}
	
	/**@return True if the all label is visible */
	public boolean isAllVisible( ) { return alllabel.isVisible( ); }

	/**
	 * Sets the current active task
	 * 
	 * @param task The task that is active
	 * @param event True to fire a value change event
	 */
	public void setActiveTask( Task task, boolean event ) {
		// set the selected task
		final Task prev = activetask;
		if( activetask == task ) return;
		activetask = task;
		
		// update CSS
		getLabel( prev ).setStyleDependentName( "selected", false );
		getLabel( activetask ).setStyleDependentName( "selected", true );
		
		// fire change event
		if( event ) ValueChangeEvent.fire( TaskSelect.this, task );		
	}
	
	/**
	 * @return The currently active task
	 */
	public Task getActiveTask( ) {
		return activetask;
	}
	
	/**
	 * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
	 */
	@Override
	public HandlerRegistration addValueChangeHandler( ValueChangeHandler<Task> handler ) {
		return addHandler( handler, ValueChangeEvent.getType( ) );
	}	
}
