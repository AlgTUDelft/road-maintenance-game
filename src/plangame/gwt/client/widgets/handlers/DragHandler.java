/**
 * @file TaskWidgetHandler.java
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
 * @date         19 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.handlers;


/**
 * Task widgets events handler
 * 
 * @author Joris Scharpff
 * @param <T> The draggable object
 */
public abstract class DragHandler<T> {
	/** The source object */
	protected T source;
	
	/** Start drag X position */
	protected int startX;
	
	/** Start drag Y position */
	protected int startY;
	
	/** Drag state */
	protected boolean dragging;
	
	/**
	 * Initialise the dragging handler
	 */
	public DragHandler( ) {
		startX = -1;
		startY = -1;
		dragging = false;
	}
	
	/**
	 * Fires the start drag event
	 * 
	 * @param source The source of the event
	 * @param startX The starting X position
	 * @param startY The starting Y position
	 */
	public void fireStartDrag( T source, int startX, int startY ) {
		if( dragging ) return;
		
		this.source = source;
		this.startX = startX;
		this.startY = startY;
		
		dragging = startDrag( );
	}
	
	/**
	 * The start drag event
	 * 
	 * @return boolean indicating whether the drag is allow to start
	 */
	public abstract boolean startDrag( );
	
	/**
	 * Fires the dest drag event
	 * 
	 * @param endX The dest X position
	 * @param endY The dest Y position
	 */
	public void fireEndDrag( int endX, int endY ) {
		if( !dragging ) return;
		dragging = false;
		
		endDrag( endX, endY );
	}
	
	/**
	 * The dest drag event
	 * 
	 * @param endX The dest X position
	 * @param endY The dest Y position
	 */
	public abstract void endDrag( int endX, int endY );
	
	/**
	 * Mouse move event
	 * 
	 * @param currX The current X position
	 * @param currY The current Y position
	 */
	public void fireMoveDrag( int currX, int currY ) {
		if( !dragging ) return;
		
		moveDrag( currX, currY );
	}
	
	/**
	 * The drag move event
	 * 
	 * @param currX The current X position
	 * @param currY The current Y position
	 */
	public abstract void moveDrag( int currX, int currY );

	/** @return The source object  */
	public T getSource( ) { return source; } 

	/** @return The orig X coordinate  */
	public int getStartX( ) { return startX; } 
	
	/** @return The orig Y coordinate  */
	public int getStartY( ) { return startY; } 
}
