/**
 * @file GameWidget.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright � 2013 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         28 aug. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import plangame.game.plans.PlanTask;
import plangame.gwt.client.ClientView;
import plangame.gwt.client.gamedata.ClientGameData;
import plangame.gwt.client.widgets.listeners.SelectionListener;
import plangame.gwt.client.widgets.listeners.ViewListener;
import plangame.gwt.shared.DebugGlobals;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;

/**
 * All (major) custom widget should implement this widget that provides some
 * basic functionality within the framework.
 *
 * @author Joris Scharpff
 */
public abstract class BasicWidget extends Composite {	
	/** The game data object of the client */
	private ClientGameData data;
	
	/** The planned task that is currently selected in the widget */
	private PlanTask selected;
	
	/** All widget listeners */
	private List<ViewListener> listeners;
	
	/** Last known start of paint */
	private long painttime;
	
	/**
	 * Creates the basic widget
	 */
	public BasicWidget( ) {
		// initialise default listeners
		listeners = new ArrayList<ViewListener>( );
		
		painttime = -1;
	}
	
	/**
	 * Sets the client data provider, set by the view
	 * 
	 * @param data The client game data 
	 */
	public void setGameData( ClientGameData data ) {
		this.data = data;
	}
	
	/** @return The game data */
	protected ClientGameData getGameData( ) { return data; }
	
	/**
	 * Checks if the game data is ready for use
	 * @return True if the game data is set and ready
	 */
	public boolean gameDataReady( ) { return (data != null && data.isReady( )); }
	
	/**
	 * Adds a new selection listener to the widget
	 * 
	 * @param listener The listener
	 */
	public void addMethodSelectionListener( SelectionListener listener ) {
		listeners.add( listener );
	}
	
	/**
	 * Removes a listener from the array of listeners
	 * 
	 * @param listener The listener to remove
	 * @return True if removed successfully
	 */
	public boolean removeListener( ViewListener listener ) {
		return listeners.remove( listener );
	}
	
	/**
	 * Deselects the current method
	 */
	public void deselect( ) {
		if( getSelected( ) == null ) return;
		
		setSelected( null );
	}
	
	/**
	 * Sets the new selection in the widget and notifies all listeners
	 * 
	 * @param selected The new selection
	 */
	public void setSelected( PlanTask selected ) {
		setSelected( selected, true );
	}
	
	/**
	 * Sets the new selection in the widget, fires onSelectionChange event in all
	 * MethodSelectionListeners if required
	 * 
	 * @param selected The newly selected method
	 * @param notify True to notify the listeners of the new selection
	 */
	public void setSelected( PlanTask selected, boolean notify ) {
		final PlanTask oldselection = getSelected( );
		this.selected = selected;
		
		if( notify )
			for( ViewListener l : listeners )
				if( l instanceof SelectionListener )
					((SelectionListener) l).onSelectionChange( this, oldselection, this.selected );
		
		// update the widget
		onSetSelection( oldselection, this.selected );
	}
	
	/***
	 * @return The currently selected method in the widget
	 */
	public PlanTask getSelected( ) {
		return selected;
	}
	
	/**
	 * Checks whether the specified task is selected in a null-safe way
	 * 
	 * @param ptask The planned task to check (or null for no selection)
	 * @return True if the method is the current selection
	 */
	public boolean isSelected( PlanTask ptask ) {
		return (selected == null && ptask == null) || (selected != null && selected.equals( ptask ) );
	}
	
	/**
	 * Clears the selection in only this widget if the current selection equals
	 * the specified task. This function is typically used just before the
	 * widget is repainted because of a joint plan change to prevent double full
	 * repaints.
	 * 
	 * @param ptask The planned task
	 * @return True if the selection was cleared
	 */
	public boolean clearIfSelected( PlanTask ptask ) {
		if( getSelected( ) == null || !getSelected( ).equals( ptask ) ) return false;
		
		// clear the selection
		selected = null;
		return true;
	}
	
	/**
	 * Called when a new planned task is selected, can be overridden to perform updates
	 * 
	 * @param oldselection The previous selection
	 * @param newselection The newly selected planned task
	 */
	protected void onSetSelection( PlanTask oldselection, PlanTask newselection ) { }
	
	/**
	 * Marks the start of a UI paint update
	 */
	protected void paintStart( ) {
		painttime = System.currentTimeMillis( );
	}
	
	/**
	 * Marks the end of a UI paint update without any description
	 */
	protected void paintEnd( ) { paintEnd( null ); }
	
	/**
	 * Marks the end of a UI paint update
	 * 
	 * @param msg Additional descriptive message
	 */
	protected void paintEnd( String msg ) {
		if( painttime == -1 ) {
			ClientView.getInstance( ).warning( "Widget " + getClass( ).getName( ) + " finsihed painting without marking the start" );
			return;
		}
		
		// check if the we should ouput the UI draw time
		if( DebugGlobals.uiProfiling( ) ) {
			final String stamp = DateTimeFormat.getFormat( "hh:mm:ss" ).format( new Date( ) );
			ClientView.getInstance( ).debug( "[PROFILE " + stamp + "] " + getClass( ).getName( ) + (msg != null ? " (" + msg + ")" : "" ) + ": " + (System.currentTimeMillis( ) - painttime) + " msec", false );
		}
		
		painttime = -1;
	}
}
