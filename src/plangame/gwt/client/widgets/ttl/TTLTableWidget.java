/**
 * @file FinanceWidget.java
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
 * @date         31 aug. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.ttl;

import java.util.List;

import plangame.game.plans.DelayStatus;
import plangame.game.plans.JointPlan;
import plangame.game.plans.PlanChange;
import plangame.game.plans.PlanTask;
import plangame.game.player.Player;
import plangame.game.score.TTLScore;
import plangame.gwt.client.ClientView;
import plangame.gwt.client.gamedata.JointPlanUpdateListener;
import plangame.gwt.client.resource.css.TTLTableWidgetCSS;
import plangame.gwt.client.resource.locale.Format;
import plangame.gwt.client.resource.locale.Format.Style;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.widgets.BasicWidget;
import plangame.gwt.client.widgets.listeners.TTLWidget;
import plangame.model.object.ObjectMap;
import plangame.model.tasks.Task;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget that displays all TTL information to the service provider
 *
 * @author Joris Scharpff
 */
public class TTLTableWidget extends BasicWidget implements TTLWidget, JointPlanUpdateListener {
	protected interface TTLTableWidgetUIBinder extends UiBinder<Widget, TTLTableWidget> {}

	// UI elements
	@UiField protected VerticalPanel pnlMain;
	@UiField protected HTMLPanel pnlOther;
	@UiField protected Grid grid;
	@UiField protected CheckBox chkPayments;
	
	/** The player using this widget */
	protected Player player;
	
	/** True to show relative TTL impact (percentages) */
	protected boolean relative;
	
	/** True to show payments instead of TTL */
	protected boolean showpayments;
	
	/** Data row indexes */
	private enum Row {
		Name,
		Individual,
		NetworkBest,
		NetworkWorst,
		TotalNoDelay,
		DelayText,
		IndividualDelay,
		NetworkBestDelay,
		NetworkWorstDelay,
		TotalIncDelay
	}
	
	/** Column index mapping */
	private ObjectMap<Task, Integer> taskcols;
	
	/** Cache table values */
	private double[][] cache;
	
	/**
	 * Creates a new FinanceWidget
	 */
	public TTLTableWidget( ) {
		super( );
		
		// create task column index mapping
		taskcols = new ObjectMap<Task, Integer>( );
		relative = true;
		showpayments = false;
		
		// initialise the UI using the binder
		initWidget( ((TTLTableWidgetUIBinder)GWT.create( TTLTableWidgetUIBinder.class )).createAndBindUi( this ) );

		updateOtherPlayer( false );

		// add value change handler to checkbox
		chkPayments.addValueChangeHandler( new ValueChangeHandler<Boolean>( ) {
			@Override public void onValueChange( ValueChangeEvent<Boolean> event ) {
				setDisplayPayments( event.getValue( ) );
			}
		} );
	}

	/**
	 * Sets the player using the widget
	 * 
	 * @param player The player
	 */
	public void setPlayer( Player player ) {
		this.player = player;
	}
	
	/**
	 * @return The player using this widget
	 */
	public Player getPlayer( ) {
		return player;
	}
	
	/**
	 * @see plangame.gwt.client.widgets.listeners.TTLWidget#setDisplayRelative(boolean)
	 */
	@Override
	public void setDisplayRelative( boolean relative ) {
		this.relative = relative;

		update( );
	}
	
	/**
	 * @return Whether TTL is displayed relative
	 */
	public boolean isShownRelative( ) { return relative; }
	
	/**
	 * Enables/disables display of payments instead of TTL numbers
	 * 
	 * @param payments True to show payments, false for TTL
	 */
	public void setDisplayPayments( boolean payments ) {
		this.showpayments = payments;
		
		update( );
	}
	
	/**
	 * @return True if payments are shown
	 */
	public boolean showPayments( ) { return showpayments; }
	
	/**
	 * @see plangame.gwt.client.gamedata.JointPlanUpdateListener#onJointPlanSet(plangame.game.plans.JointPlan)
	 */
	@Override
	public void onJointPlanSet( JointPlan jplan ) {
		update( );
	}
	
	/**
	 * @see plangame.gwt.client.gamedata.JointPlanUpdateListener#onJointPlanChange(plangame.game.plans.PlanChange, boolean)
	 */
	@Override
	public void onJointPlanChange( PlanChange change, boolean validated ) {
		// FIXME selective update
		// update all tasks
		update( );
	}
	
	/**
	 * Sets the tasks to be displayed in the task selection
	 * 
	 * @param tasks The tasks
	 */
	public void setTasks( List<Task> tasks ) {
		taskcols.clear( );
		
		// update the grid
		resetGrid( tasks );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.BasicWidget#onSetSelection(plangame.model.tasks.TaskMethod, plangame.model.tasks.TaskMethod)
	 */
	@Override
	protected void onSetSelection( PlanTask oldselection, PlanTask newselection ) {
		super.onSetSelection( oldselection, newselection );

		// get tasks
		final Task prev = (oldselection != null ? oldselection.getTask( ) : null );		
		final Task task = (newselection != null ? newselection.getTask( ) : null );		
		
		// update row style
		if( prev != null && prev.getPlayer( ).equals( getPlayer( ) ) ) grid.getColumnFormatter( ).removeStyleName( taskcols.get( prev ), getCSS( ).gridselectedrow( ) );
		if( task != null && task.getPlayer( ).equals( getPlayer( ) ) ) grid.getColumnFormatter( ).addStyleName( taskcols.get( task ), getCSS( ).gridselectedrow( ) );
		
		updateOtherPlayer( task != null && !task.getPlayer( ).equals( getPlayer( ) ) );
	}
	
	/**
	 * Called when the task list changes to rebuild the grid
	 */
	private void resetGrid( List<Task> tasks ) {
		// clear the task column index mapping
		taskcols.clear( );
				
		// remove the previous grid and create a new one of the correct size
		// number of columns = number of tasks + label column + total column
		pnlMain.remove( grid );
		grid = new Grid( Row.values( ).length, tasks.size( ) + 2 );
		grid.addStyleName( getCSS( ).grid( ) );
		pnlMain.add( grid );
		
		// add all row labels information
		grid.setText( Row.Name.ordinal( ), 0, Lang.text.W_TTL_TableTitle( ) );
		grid.setText( Row.Individual.ordinal( ), 0, Lang.text.W_TTL_RowIndividual( ) );
		grid.setText( Row.NetworkBest.ordinal( ), 0, Lang.text.W_TTL_RowNetworkBest( ) );
		grid.setText( Row.NetworkWorst.ordinal( ), 0, Lang.text.W_TTL_RowNetworkWorst( ) );
		grid.setText( Row.TotalNoDelay.ordinal( ), 0, Lang.text.W_TTL_RowTotalNoDelay( ) );
		grid.setText( Row.DelayText.ordinal( ), 0, Lang.text.W_TTL_RowIfDelayed( ) );
		grid.setText( Row.IndividualDelay.ordinal( ), 0, Lang.text.W_TTL_RowIndividual( ) );
		grid.setText( Row.NetworkBestDelay.ordinal( ), 0, Lang.text.W_TTL_RowNetworkBest( ) );
		grid.setText( Row.NetworkWorstDelay.ordinal( ), 0, Lang.text.W_TTL_RowNetworkWorst( ) );
		grid.setText( Row.TotalIncDelay.ordinal( ), 0, Lang.text.W_TTL_RowTotalDelay( ) );
			
		// add task columns
		grid.getRowFormatter( ).addStyleName( 0, getCSS( ).gridheader( ) );
		int taskcol = 1;
		for( Task t : tasks ) {
			taskcols.put( t, taskcol );
			grid.setText( 0, taskcol++, t.getDescription( ) );
		}
		grid.setText( 0, taskcol, Lang.text.W_TTL_TotalCell( ) );
		
		// set styles for specific cells & rows
		grid.getColumnFormatter( ).addStyleName( 0, getCSS( ).gridlabelcol( ) );
		for( int i = 1; i < grid.getRowCount( ); i++ )
			grid.getCellFormatter( ).addStyleName( i, 0, getCSS( ).gridlabelcolcell( ) );
		grid.getCellFormatter( ).addStyleName( 0, 0, getCSS( ).gridselectedname( ) );
		grid.getRowFormatter( ).addStyleName( Row.DelayText.ordinal( ), getCSS( ).griddelayrow( ) );
		grid.getCellFormatter( ).addStyleName( Row.DelayText.ordinal( ), 0, getCSS( ).griddelaycell( ) );
		grid.getRowFormatter( ).addStyleName( Row.TotalNoDelay.ordinal( ), getCSS( ).gridtotalscellex( ) );
		grid.getRowFormatter( ).addStyleName( Row.TotalIncDelay.ordinal( ), getCSS( ).gridtotalscell( ) );
		
		// and set odd and even row/column styles
		for( int i = 0; i < grid.getRowCount( ); i++ )
			grid.getRowFormatter( ).addStyleName( i, (i % 2 == 0 ? getCSS( ).gridevenrow( ) : getCSS( ).gridoddrow( ) ) );
		for( int i = 0; i < grid.getColumnCount( ); i++ )
			grid.getColumnFormatter( ).addStyleName( i, (i % 2 == 0 ? getCSS( ).gridevencol( ) : getCSS( ).gridoddcol( ) ) );
		
		// clear cached values
		cache = new double[ Row.values( ).length ][ tasks.size( ) + 1 ];
		
		// update the values displayed in each task column
		for( Task t : taskcols.getKeys( ) )
			updateTask( t );

		// reset selected to update displayed name and columns
		setSelected( getSelected( ), false );
	}
	
	/**
	 * Updates all tasks
	 */
	protected void update( ) {
		paintStart( );
		// update the display of all tasks
		for( Task t : taskcols.getKeys( ) )
			updateTask( t );
		paintEnd( );
	}
	
	/**
	 * Updates the values in the task column, totals are automatically updated.
	 * 
	 * @param task The task column to update
	 */
	protected void updateTask( Task task ) {
		// get the task column index
		final int idx = taskcols.get( task, -1 );
		if( idx == -1 ) throw new RuntimeException( "Unknown task specified" );
				
		// check if there is game info available
		if( !gameDataReady( ) ) { clearColumn( idx ); return; }
				
		// get the planned method
		final PlanTask pt = getGameData( ).getJointPlan( ).getPlanned( task );
		if( pt == null ) { clearColumn( idx ); return; }
		
		// get method delay status for delay part
		final DelayStatus delay = pt.getDelayStatus( );
		
		// setup absolute, relative display or payment display
		final TTLScore ttl;
		final Style style;
		if( !showPayments( ) ) { 
			ttl = getGameData( ).getData( ).getTTL( task, isShownRelative( ) );
			style = (!isShownRelative( ) ? Style.IntK : Style.Percentage2 );
		} else {
			ttl = getGameData( ).getData( ).getPayments( task );
			style = Style.CurrK;
		}
		
		// fill in the task column
		updateValue( Row.Individual, idx, ttl.getIndividual( ).getRegular( ), style );
		updateValue( Row.NetworkBest, idx, ttl.getNetworkRegular( ).getBestRealised( ), style );
		updateValue( Row.NetworkWorst, idx, ttl.getNetworkRegular( ).getRealisedDelta( ), style );
		updateValueSum( Row.TotalNoDelay, idx, Row.Individual, Row.NetworkWorst, DelayStatus.Pending, style );

		updateValue( Row.IndividualDelay, idx, ttl.getIndividual( ).getDelayed( ), delay, style );
		updateValue( Row.NetworkBestDelay, idx, ttl.getNetworkDelayed( ).getBestRealised( ), delay, style );
		updateValue( Row.NetworkWorstDelay, idx, ttl.getNetworkDelayed( ).getRealisedDelta( ), delay, style );
		updateValueSum( Row.TotalIncDelay, idx, Row.TotalNoDelay, Row.NetworkWorstDelay, delay, style );
}
	
	/**
	 * Updates the value of the specified cell, also updates total cell
	 * 
	 * @param row The row
	 * @param col The column
	 * @param value The new value
	 * @param style The formatting style
	 */
	private void updateValue( Row row, int col, double value, Style style ) {
		updateValue( row, col, value, DelayStatus.Pending, style );
	}
	
	/**
	 * Updates the value of the specified cell, also updates total cell
	 * 
	 * @param row The row
	 * @param col The column
	 * @param value The new value
	 * @param style The formatting style
	 */
	private void updateValue( Row row, int col, double value, DelayStatus delay, Style style ) {
		final int r = row.ordinal( );
		
		// check if this value is still valid, if not delayed we can forget it
		final double setval = (delay == DelayStatus.AsPlanned ? 0 : value );

		// set new row value
		cache[ r ][ col - 1 ] = setval;
		grid.setText( r, col, Format.f( value, style ) );
		
		// update total
		updateTotal( r, style );
		
		// set cell style based on delay status
		if( delay == DelayStatus.Pending ) grid.getCellFormatter( ).setStyleName( r, col, getCSS( ).gridcellpending( ) );
		if( delay == DelayStatus.Delayed ) grid.getCellFormatter( ).setStyleName( r, col, getCSS( ).gridcelldelayed( ) );
		if( delay == DelayStatus.AsPlanned ) grid.getCellFormatter( ).setStyleName( r, col, getCSS( ).gridcellasplannednocost( ) );
	}	
	
	/**
	 * Updates the value of the specified cell by summing over the specified
	 * range. Also updates the total cell.
	 * 
	 * @param row The row to update
	 * @param col The column index
	 * @param startrow The start of the range
	 * @param endrow The end of the range
	 * @param delay The delay status of the sum
	 * @param style The row display formatting style
	 */
	private void updateValueSum( Row row, int col, Row startrow, Row endrow, DelayStatus delay, Style style ) {
		// sum over the values of the rows
		double sum = 0;
		final int start = (startrow.ordinal( ) <= endrow.ordinal( ) ? startrow.ordinal( ) : endrow.ordinal( ) ); 
		final int end = (startrow.ordinal( ) <= endrow.ordinal( ) ? endrow.ordinal( ) : startrow.ordinal( ) ); 
		for( int i = start; i <= end; i++ )
			sum += cache[ i ][ col - 1 ];
		
		// update the row
		updateValue( row, col, sum, style );
		
		// set cell style based on delay status
		if( delay == DelayStatus.Pending ) grid.getCellFormatter( ).setStyleName( row.ordinal( ), col, getCSS( ).gridcellpending( ) );
		if( delay == DelayStatus.Delayed ) grid.getCellFormatter( ).setStyleName( row.ordinal( ), col, getCSS( ).gridcelldelayed( ) );
		if( delay == DelayStatus.AsPlanned ) grid.getCellFormatter( ).setStyleName( row.ordinal( ), col, getCSS( ).gridcellasplanned( ) );
	}
	
	/**
	 * Clears the entire column except for the name field
	 * 
	 * @param col The column index
	 */
	private void clearColumn( int col ) {
		for( int i = 1; i < grid.getRowCount( ); i++ ) {
			grid.setText( i, col, "" );
			cache[ i ][ col - 1] = 0;
			
			if( i == Row.DelayText.ordinal( ) ) continue;
			
			updateTotal( i, (showPayments( ) ? Style.CurrK : (isShownRelative( ) ? Style.Percentage2 : Style.IntK ) ) );
		}
	}
	
	/**
	 * Updates the value in the total cell
	 * 
	 * @param row The row
	 * @param style The total format style
	 */
	private void updateTotal( int row, Style style ) {
		double total = 0;
		for( int i = 0; i < taskcols.size( ); i++ )
			total += cache[ row ][ i ];
		
		final int totalidx = taskcols.size( );
		cache[ row ][ totalidx ] = total;
		grid.setText( row, grid.getColumnCount( ) - 1, Format.f( total, style ) );
	}
	
	/**
	 * Updates the widget to hide/show info when a task of a different player is
	 * selected
	 * 
	 * @param other True if a task of another player is selected
	 */
	private void updateOtherPlayer( boolean other ) {
		// show the panel
		pnlOther.setVisible( other );
		
		// set size to show it over the table
		pnlOther.setSize( "100%", "100%" );
	}
	
	/**
	 * @return The CSS of the widget
	 */
	private TTLTableWidgetCSS getCSS( ) {
		return ClientView.getInstance( ).getResources( ).ttlcss( );
	}
}
