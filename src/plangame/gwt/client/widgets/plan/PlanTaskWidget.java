/**
 * @file TaskWidget.java
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
package plangame.gwt.client.widgets.plan;

import plangame.game.plans.PlanTask;
import plangame.gwt.client.resource.locale.Format;
import plangame.gwt.client.resource.locale.Format.Style;
import plangame.gwt.client.util.ClientUtil;
import plangame.gwt.client.widgets.BasicWidget;
import plangame.gwt.client.widgets.handlers.DragHandler;
import plangame.gwt.client.widgets.plan.PlanWidget.DrawingVars;
import plangame.model.time.TimeDuration;
import plangame.model.time.TimePoint;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Joris Scharpff
 */
public class PlanTaskWidget extends BasicWidget {
	protected interface PlanTaskWidgetUIBinder extends UiBinder<Widget, PlanTaskWidget> { }
	protected PlanTaskWidgetUIBinder PlanTaskWidgetUI = GWT.create( PlanTaskWidgetUIBinder.class );
	
	/** The planned task currently represented */
	protected PlanTask ptask;
	
	/** The planning widget containing this widget */
	protected PlanWidget planwidget;
	
	/** The task drag handler */
	protected DragHandler<PlanTaskWidget> draghandler;
	
	/** Whether this widget is selectable */
	protected boolean selectable;
	
	/** The current week it is shown at */
	protected TimePoint week;
	
	/** The current row it is shown in, -1 for entire row */
	protected int row;
	
	/** Height correction for border size */
	private final static int BORDER_SIZE = 2;
		
	/** The HTML components */
	@UiField protected FocusPanel fcpTask;
	@UiField protected AbsolutePanel pnlTaskInfo;
	@UiField protected Label lblName;
	@UiField protected AbsolutePanel pnlTaskDelay;
	@UiField protected Label lblDelayProb;
	
	/**
	 * Creates a new task widget for the specified task method
	 * 
	 * @param ptask The tas method
	 * @param planwidget The plan widget containing this widget
	 */
	public PlanTaskWidget( PlanWidget planwidget ) {		
		super( );
		
		// set parent widget
		this.planwidget = planwidget;

		// initialise the widget
		initWidget( PlanTaskWidgetUI.createAndBindUi( this ) );		
		ClientUtil.disableTextSelect( getElement( ) );
				
		// setup the default settings
		setSelectable( true );
		
		// init defaults for week and row numbers
		clearRow( );
	}
		
	/**
	 * Sets whether the task widget is selectable
	 * 
	 * @param selectable True if the method is selectable in the UI
	 */
	protected void setSelectable( boolean selectable ) {
		this.selectable = selectable;
	}
	
	/** @return True if the method is selectable */
	protected boolean isSelectable( ) { return selectable; }
	
	/**
	 * Sets the current week it is shown at
	 * 
	 * @param week The week number
	 */
	protected void setWeek( TimePoint week ) {
		this.week = week;
		
		// update X position correspondingly
		getPanel( ).setWidgetPosition( this, getDV( ).getGridX( week ), getPanel( ).getWidgetTop( this ) );
	}
	
	/**
	 * @return The week it is shown at
	 */
	protected TimePoint getWeek( ) {
		return week;
	}
	
	/**
	 * Retrieves the row in which the task is displayed
	 * 
	 * @return The row index
	 */
	protected int getRow( ) {
		return row;
	}
	
	/**
	 * Updates the display of the delay status
	 */
	protected void updateDelay( ) {
		lblDelayProb.setText( " " + Format.f( ptask.getMethod( ).getDelayRisk( ), Style.Percentage ) );
		
		// set style based on delay value
		switch( ptask.getDelayStatus( ) ) {
			case Pending:
				pnlTaskDelay.addStyleName( planwidget.getCSS( ).taskdelaypending( ) );
				pnlTaskDelay.removeStyleName( planwidget.getCSS( ).taskdelayasplanned( ) );
				pnlTaskDelay.removeStyleName( planwidget.getCSS( ).taskdelaydelayed( ) );
				break;

			case AsPlanned:
				pnlTaskDelay.removeStyleName( planwidget.getCSS( ).taskdelaypending( ) );
				pnlTaskDelay.addStyleName( planwidget.getCSS( ).taskdelayasplanned( ) );
				pnlTaskDelay.removeStyleName( planwidget.getCSS( ).taskdelaydelayed( ) );
				break;
				
			case Delayed:
				pnlTaskDelay.removeStyleName( planwidget.getCSS( ).taskdelaypending( ) );
				pnlTaskDelay.removeStyleName( planwidget.getCSS( ).taskdelayasplanned( ) );
				pnlTaskDelay.addStyleName( planwidget.getCSS( ).taskdelaydelayed( ) );
				break;				
		}
	}

	/**
	 * Updates the position of the widget to reflect a possible new index of the
	 * method's player in the plan widget
	 */
	protected void updatePlayerIndex( ) {
		// set the task widget position accordingly
		getPanel( ).setWidgetPosition( this, getPanel( ).getWidgetLeft( this ), getDV( ).getGridY( ptask.getPlayer( ) ) );
	}
	
	/** 
	 * Sets the drag handler
	 * 
	 * @param draghandler The drag handler
	 */
	protected void setDragHandler( DragHandler<PlanTaskWidget> draghandler ) {
		this.draghandler = draghandler;
	}
	
	/**
	 * Sets the current planned task that this widget displays
	 * 
	 * @param ptask The task to display
	 */
	protected void setPlannedTask( PlanTask ptask ) {
		this.ptask = ptask;
		
		// set the colour of the widget according with the portfolio
		pnlTaskInfo.getElement( ).getStyle( ).setBackgroundColor( ptask.getPlayer( ).getPortfolio( ).getColour( ) );
		pnlTaskDelay.getElement( ).getStyle( ).setBackgroundColor( ptask.getPlayer( ).getPortfolio( ).getColour( ) );

		// resize the widget according to the method duration
		resize( ptask.getPeriodRegular( ), ptask.getPeriodDelayed( ), null );		
		
		// update texts
		if( isMine( ) || planwidget.showOtherMethodNames( ) )
			lblName.setText( ptask.getTask( ).toString( ) + ":\n" + ptask.getMethod( ).getDescription( ) );
		else
			lblName.setText( ptask.getTask( ).getAsset( ).toString( ) );			
		
		// update the display of delay part
		updateDelay( );
		
		// move it to the correct position
		updatePlayerIndex( );
	}

	/**
	 * @return The planned task 
	 */
	protected PlanTask getPlannedTask( ) {
		return ptask;
	}
	
	/**
	 * @see plangame.gwt.client.widgets.BasicWidget#onSetSelection(plangame.model.tasks.TaskMethod, plangame.model.tasks.TaskMethod)
	 */
	@Override
	protected void onSetSelection( PlanTask oldselection, PlanTask newselection ) {
		// set appropriate style name for the method
		fcpTask.setStyleName( planwidget.getCSS( ).taskselected( ), getPlannedTask( ).equals( newselection ) );		
	}

	
	@UiHandler("fcpTask")
	protected void mouseDown( MouseDownEvent e ) {
		// start drag operation (if allowed)
		if( draghandler != null )
			draghandler.fireStartDrag( this, e.getX( ), e.getY( ) );
	}

	@UiHandler("fcpTask")
	protected void mouseUp( MouseUpEvent e ) {
		e.stopPropagation( );

		if( draghandler != null )
			draghandler.fireEndDrag( e.getX( ), e.getY( ) );
		
		// set the selected method
		if( isSelectable( ) && !isSelected( ptask ) ) {
			planwidget.setSelected( getPlannedTask( ) );
		}
	}

	@UiHandler("fcpTask")
	protected void mouseMove( MouseMoveEvent e ) {
		if( draghandler != null )
			draghandler.fireMoveDrag( e.getX( ), e.getY( ) );
	}

	/**
	 * Clears the stored row position
	 */
	protected void clearRow( ) {
		this.row = -1;
	}
	
	/**
	 * Updates the task height based on its current row position
	 * 
	 * @param row The row index
	 */
	protected void setRow( int row ) {
		this.row = row;		
		
		// set position and height accordingly
		getPanel( ).setWidgetPosition( this, getPanel( ).getWidgetLeft( this ), getDV( ).getGridY( ptask.getPlayer( ), row ) );
		resize( null, null, (int)getDV( ).getRowHeight( ptask.getPlayer( ) ) - 2 * BORDER_SIZE );
	}

	/**
	 * Resizes the task widget using the current values, called by when the plan
	 * widget is resize
	 */
	protected void resize( ) {
		// call setters to enforce resize 
		setPlannedTask( ptask );
		setRow( row );
	}
	
	/**
	 * Resizes the task widget, every parameter that is set to null is ignored
	 * 
	 * @param weeks The number of regular weeks
	 * @param delweeks The number of delay weeks (0 for no delay)
	 * @param height The new height
	 */
	private void resize( TimeDuration weeks, TimeDuration delweeks, Integer height ) {		
		// resize regular time
		if( weeks != null ) {
			pnlTaskInfo.setWidth( getDV( ).getWeekWidth( weeks ) + "px" );
		}
		
		// resize delay time
		if( delweeks != null ) {
			pnlTaskDelay.setWidth( getDV( ).getWeekWidth( delweeks ) + "px" );
		}
		
		// resize task height
		if( height != null ) {
			pnlTaskInfo.setHeight( height + "px" );
			pnlTaskDelay.setHeight( height + "px" );
		}
		
		// reseize the widget (correction for borders)
		if( weeks != null && delweeks != null )
			pnlTaskInfo.setWidth( pnlTaskInfo.getOffsetWidth( ) - (BORDER_SIZE * 2) + "px" );
	}
	
	/**
	 * Retrieves the draw variables
	 * 
	 * @return The drawvars object of the plan widget
	 */
	private DrawingVars getDV( ) {
		return planwidget.drawvars;
	}
	
	/**
	 * Retrieves the plan panel
	 * 
	 * @return The pnlPlanning within the PlanWidget
	 */
	private AbsolutePanel getPanel( ) {
		return planwidget.pnlPlanning;
	}
	
	/**
	 * Checks whether the task widget is one of the PlanWidget player's methods
	 * 
	 * @return True if this method is owned by the player that is using the
	 * interface
	 */
	private boolean isMine( ) {
		return ptask.getPlayer( ).equals( planwidget.getPlayer( ) );
	}
}
