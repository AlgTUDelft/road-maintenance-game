/**
 * @file TaskWidgetDragHandler.java
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
 * @date         27 mrt. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.plan;

import plangame.game.plans.PlanChange;
import plangame.gwt.client.ClientView;
import plangame.gwt.client.serviceprovider.ServiceProvider;
import plangame.gwt.client.widgets.handlers.DragHandler;
import plangame.gwt.client.widgets.plan.PlanWidget.DrawingVars;
import plangame.gwt.shared.clients.Client.ClientType;
import plangame.model.time.TimePoint;

/**
 *
 * @author Joris Scharpff
 */
public class PlanTaskWidgetDragHandler extends DragHandler<PlanTaskWidget> {
	/** The task widget represented by this handler */
	protected PlanTaskWidget tw;
	
	/** The drawvars of the planwidget, stored for easy reference */
	protected final DrawingVars drawvars;
	
	/** The number of weeks in the joint plan */
	protected final int weeks;
	
	/** The week that is was planning in when the drag started */
	protected TimePoint startweek;
	
	/**
	 * Creates a new TaskWidget draghandler
	 * 
	 * @param tw The task widget
	 * @param weeks The number of weeks in the plan
	 */
	public PlanTaskWidgetDragHandler( PlanTaskWidget tw, int weeks ) {
		this.tw = tw;
		drawvars = tw.planwidget.drawvars;
		this.weeks = weeks;
	}
	
	/** 
	 * @see plangame.gwt.client.widgets.handlers.DragHandler#startDrag()
	 */
	@Override
	public boolean startDrag( ) {
		startweek = tw.getWeek( );

		// cannot move tasks that have been executed
		return tw.planwidget.canDrag( getSource( ).getPlannedTask( ) );						
	}
	
	/**
	 * @see plangame.gwt.client.widgets.handlers.DragHandler#moveDrag(int, int)
	 */
	@Override
	public void moveDrag( int currX, int currY ) {
		// only available to service providers
		if( !ClientView.ofType( ClientType.ServiceProvider ) ) return;
		
		// compute the week we are now planning it in
		final int newX = drawvars.getGridX( tw.getWeek( ) ) + currX - getStartX( );
		final int newweek = drawvars.getGridWeek( newX );
		final int maxweek = weeks - tw.getPlannedTask( ).getPeriod( true ).getWeeks( );
	
		// move the tasks (if required)
		//if( newweek == tw.getWeek( ) ) return;
		tw.setWeek( new TimePoint( Math.min( newweek, maxweek ) ) );
		
		// invalidate my row number and check if the new position causes tasks to overlap
		tw.clearRow( );
		tw.planwidget.updateRows( );
	}


	/**
	 * @see plangame.gwt.client.widgets.handlers.DragHandler#endDrag(int, int)
	 */
	@Override
	public void endDrag( int endX, int endY ) {
		// only for service providers
		if( !ClientView.ofType( ClientType.ServiceProvider ) ) return;
		
		// compute the week we are now planning it in
		final TimePoint week = tw.getWeek( );
		if( week.equals( startweek ) ) return;
		
		// update the plan according to the new position
		ServiceProvider.getInstance( ).applyChange( PlanChange.move( tw.getPlannedTask( ), week ), false );
	}

}
