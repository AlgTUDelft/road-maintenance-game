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
package plangame.gwt.client.widgets.finance;

import java.util.Collection;
import java.util.List;

import plangame.game.plans.DelayStatus;
import plangame.game.plans.JointPlan;
import plangame.game.plans.PlanChange;
import plangame.game.plans.PlanTask;
import plangame.game.player.Player;
import plangame.game.score.PendingScore;
import plangame.game.score.TTLScore;
import plangame.gwt.client.ClientView;
import plangame.gwt.client.gamedata.JointPlanUpdateListener;
import plangame.gwt.client.resource.css.FinanceWidgetCSS;
import plangame.gwt.client.resource.locale.Format;
import plangame.gwt.client.resource.locale.Format.Style;
import plangame.gwt.client.resource.locale.Lang;
import plangame.gwt.client.widgets.BasicWidget;
import plangame.model.object.ObjectMap;
import plangame.model.tasks.Task;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget that displays all financial information to the service provider
 *
 * @author Joris Scharpff
 */
public class FinanceWidget extends BasicWidget implements JointPlanUpdateListener {
	protected interface FinanceWidgetUIBinder extends UiBinder<Widget, FinanceWidget> {}

	/** Player using this widget */
	protected Player player;
	
	// UI elements
	@UiField protected VerticalPanel pnlMain;
	@UiField protected Grid grid;
	@UiField protected HTMLPanel pnlOther;
		
	/** Data row indexes */
	private enum Row {
		Name,
		Revenue,
		Cost,
		TTLCost,
		ProfitNoRisk,
		DelayText,
		DelayCost,
		DelayTTLCost,
		ProfitTotal
	}
	
	/** Column index mapping */
	private ObjectMap<Task, Integer> taskcols;
	
	/** Cache table values */
	private double[][] cache;
	
	/**
	 * Creates a new FinanceWidget
	 */
	public FinanceWidget( ) {
		super( );
		
		// create task column index mapping
		taskcols = new ObjectMap<Task, Integer>( );
		
		// initialise the UI using the binder
		initWidget( ((FinanceWidgetUIBinder)GWT.create( FinanceWidgetUIBinder.class )).createAndBindUi( this ) );
		
		updateOtherPlayer( false );
	}

	/**
	 * Sets the player using this widget
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
		return this.player;
	}
	
	/**
	 * @see plangame.gwt.client.gamedata.JointPlanUpdateListener#onJointPlanSet(plangame.game.plans.JointPlan)
	 */
	@Override
	public void onJointPlanSet( JointPlan jplan ) {
		// update all task info, we have a new joint plan
		update( );
	}

	/**
	 * @see plangame.gwt.client.gamedata.JointPlanUpdateListener#onJointPlanChange(plangame.game.plans.PlanChange, boolean)
	 */
	@Override
	public void onJointPlanChange( PlanChange change, boolean validated ) {
		// update all tasks
		update( );
	}
	
	/**
	 * Sets the tasks to be displayed in the task selection
	 * 
	 * @param tasks The tasks
	 */
	public void setTasks( List<Task> tasks ) {
		// update the grid
		resetGrid( tasks );
	}
	
	/**
	 * @return The list of tasks set for the control
	 */
	public Collection<Task> getTasks( ) {
		return taskcols.getKeys( );
	}
	
	/**
	 * @see plangame.gwt.client.widgets.BasicWidget#onSetSelection(plangame.model.tasks.TaskMethod, plangame.model.tasks.TaskMethod)
	 */
	@Override
	protected void onSetSelection( PlanTask oldselection, PlanTask newselection ) {
		super.onSetSelection( oldselection, newselection );
		
		// highlight selected task
		final Task prev = (oldselection != null ? oldselection.getTask( ) : null );		
		final Task task = (newselection != null ? newselection.getTask( ) : null );		
		if( prev != null && prev.getPlayer( ).equals( getPlayer( ) ) ) grid.getColumnFormatter( ).removeStyleName( taskcols.get( prev ), getCSS( ).gridselectedrow( ) );
		if( task != null && task.getPlayer( ).equals( getPlayer( ) ) ) grid.getColumnFormatter( ).addStyleName( taskcols.get( task ), getCSS( ).gridselectedrow( ) );
		
		updateOtherPlayer( task != null && !task.getPlayer( ).equals( getPlayer( ) ) );
	}
	
	/**
	 * Called when the task list changes to rebuild the grid
	 * 
	 * @param tasks The tasks to display
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
		grid.setText( Row.Name.ordinal( ), 0, Lang.text.W_Finance_TableTitle( ) );
		grid.setText( Row.Revenue.ordinal( ), 0, Lang.text.W_Finance_RowRevenue( ) );
		grid.setText( Row.Cost.ordinal( ), 0, Lang.text.W_Finance_RowCost( ) );
		grid.setText( Row.TTLCost.ordinal( ), 0, Lang.text.W_Finance_RowTTLCost( ) );
		grid.setText( Row.ProfitNoRisk.ordinal( ), 0, Lang.text.W_Finance_RowProfitExRisk( ) );
		grid.setText( Row.DelayText.ordinal( ), 0, Lang.text.W_Finance_RowIfDelayed( ) );
		grid.setText( Row.DelayCost.ordinal( ), 0, Lang.text.W_Finance_RowDelayCost( ) );
		grid.setText( Row.DelayTTLCost.ordinal( ), 0, Lang.text.W_Finance_RowTTLDelayCost( ) );
		grid.setText( Row.ProfitTotal.ordinal( ), 0, Lang.text.W_Finance_RowProfits( ) );
			
		// add task columns
		grid.getRowFormatter( ).addStyleName( 0, getCSS( ).gridheader( ) );
		int taskcol = 1;
		for( Task t : tasks ) {
			taskcols.put( t, taskcol );
			grid.setText( 0, taskcol++, t.getDescription( ) );
		}
		grid.setText( 0, taskcol, Lang.text.W_Finance_ColTotal( ) );
		
		// set styles for specific columns
		grid.getColumnFormatter( ).addStyleName( 0, getCSS( ).gridlabelcol( ) );
		for( int i = 1; i < grid.getRowCount( ); i++ )
			grid.getCellFormatter( ).addStyleName( i, 0, getCSS( ).gridlabelcolcell( ) );
		grid.getRowFormatter( ).addStyleName( Row.DelayText.ordinal( ), getCSS( ).griddelayrow( ) );
		grid.getCellFormatter( ).addStyleName( Row.DelayText.ordinal( ), 0, getCSS( ).griddelaycell( ) );
		grid.getCellFormatter( ).addStyleName( 0, 0, getCSS( ).gridselectedname( ) );		
		grid.getRowFormatter( ).addStyleName( Row.ProfitNoRisk.ordinal( ), getCSS( ).gridtotalscellex( ) );
		grid.getRowFormatter( ).addStyleName( Row.ProfitTotal.ordinal( ), getCSS( ).gridtotalscell( ) );
		
		// and set odd and even row/column styles
		for( int i = 0; i < grid.getRowCount( ); i++ )
			grid.getRowFormatter( ).addStyleName( i, (i % 2 == 0 ? getCSS( ).gridevenrow( ) : getCSS( ).gridoddrow( ) ) );
		for( int i = 0; i < grid.getColumnCount( ); i++ )
			grid.getColumnFormatter( ).addStyleName( i, (i % 2 == 0 ? getCSS( ).gridevencol( ) : getCSS( ).gridoddcol( ) ) );
		
		// clear cached values
		cache = new double[ Row.values( ).length ][ tasks.size( ) + 1 ];

		// update the values displayed in each task column
		for( Task t : getTasks( ) )
			updateTask( t );
		
		// reset selection
		setSelected( getSelected( ), false );
	}
	
	/**
	 * Updates all tasks
	 */
	protected void update( ) {
		paintStart( );
		for( Task t : getTasks( ) )
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
				
		// get the game data
		if( !gameDataReady( ) ) return;
		
		// get the current joint plan
		final JointPlan jplan = getGameData( ).getJointPlan( );
		
		// get the planned method
		final PlanTask pt = jplan.getPlanned( task );
		if( pt == null ) {
			clearColumn( idx );
			setColumnData( idx, -task.getPenalty( ), new PendingScore( ), new PendingScore( ), DelayStatus.Pending );
			for( int i = 1; i < grid.getRowCount( ); i++ )
				grid.getCellFormatter( ).addStyleName( i, idx, getCSS( ).gridcolpenalty( ) );
			return;
		}
		
		for( int i = 1; i < grid.getRowCount( ); i++ )
			grid.getCellFormatter( ).removeStyleName( i, idx, getCSS( ).gridcolpenalty( ) );
		
		// get financial info
		final PendingScore cost = getGameData( ).getData( ).getMaintenanceCost( task );
		final TTLScore payments = getGameData( ).getData( ).getPayments( task );
		final DelayStatus delayed = pt.getDelayStatus( );
		
		// get payment totals
		final PendingScore payment = new PendingScore(
				payments.getIndividual( ).getRegular( ) + payments.getNetworkRegular( ).getWorstRealised( ),
				payments.getIndividual( ).getDelayed( ) + payments.getNetworkDelayed( ).getWorstRealised( ),
				delayed );
		
		setColumnData( idx, task.getRevenue( ), cost, payment, delayed );
	}
	
	/**
	 * Sets the column data
	 * 
	 * @param idx The column index
	 * @param rev The revenue
	 * @param cost The maintenance cost
	 * @param ttlpayment The TTL payments 
	 * @param delayed The task delay status
	 */
	private void setColumnData( int idx, double rev, PendingScore cost, PendingScore ttlpayment, DelayStatus delayed ) {
		// fill in the task column
		updateValue( Row.Revenue, idx, rev, Style.CurrK );
		updateValue( Row.Cost, idx, -cost.getRegular( ), Style.CurrK );
		updateValue( Row.TTLCost, idx, -ttlpayment.getRegular( ), Style.CurrK );
		updateValueSum( Row.ProfitNoRisk, idx, Row.Revenue, Row.TTLCost, DelayStatus.Pending, Style.CurrK );
		updateValue( Row.DelayCost, idx, -cost.getDelayed( ), delayed, Style.CurrK );
		updateValue( Row.DelayTTLCost, idx, -ttlpayment.getDelayed( ), delayed, Style.CurrK );
		updateValueSum( Row.ProfitTotal, idx, Row.ProfitNoRisk, Row.DelayTTLCost, delayed, Style.CurrK );		
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
		updateTotal( row.ordinal( ), style );
		
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
			
			updateTotal( i, Style.CurrK );
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
		for( int i = 0; i < getTasks( ).size( ); i++ )
			total += cache[ row ][ i ];
		
		final int totalidx = getTasks( ).size( );
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
		
		// set size and position to show it over the table
		pnlOther.setSize( "100%", "100%" );
	}
	
	/**
	 * @return The CSS of the widget
	 */
	private FinanceWidgetCSS getCSS( ) {
		return ClientView.getInstance( ).getResources( ).financecss( );
	}
}
