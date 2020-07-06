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
package plangame.gwt.client.widgets.mechanism;

import java.util.List;

import plangame.game.plans.DelayStatus;
import plangame.game.plans.JointPlan;
import plangame.game.plans.PlanChange;
import plangame.game.plans.PlanTask;
import plangame.game.score.TTLScore;
import plangame.gwt.client.ClientView;
import plangame.gwt.client.gamedata.JointPlanUpdateListener;
import plangame.gwt.client.resource.css.MechanismWidgetCSS;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget that displays all mechanism payments information
 *
 * @author Joris Scharpff
 */
public class MechanismWidget extends BasicWidget implements JointPlanUpdateListener {
	protected interface MechanismWidgetUIBInder extends UiBinder<Widget, MechanismWidget> {}

	// UI elements
	@UiField protected VerticalPanel pnlMain;
	@UiField protected Grid grid;
	
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
	public MechanismWidget( ) {
		super( );
		
		// create task column index mapping
		taskcols = new ObjectMap<Task, Integer>( );
			
		// initialise the UI
		initWidget( ((MechanismWidgetUIBInder)GWT.create( MechanismWidgetUIBInder.class )).createAndBindUi( this ) );
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
		// FIXME selective update
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
		if( prev != null ) grid.getColumnFormatter( ).removeStyleName( taskcols.get( prev ), getCSS( ).gridselectedrow( ) );
		if( task != null ) grid.getColumnFormatter( ).addStyleName( taskcols.get( task ), getCSS( ).gridselectedrow( ) );
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
		grid.setText( Row.Name.ordinal( ), 0, Lang.text.W_Mech_TableTitle( ) );
		grid.setText( Row.Individual.ordinal( ), 0, Lang.text.W_Mech_RowIndividual( ) );
		grid.setText( Row.NetworkBest.ordinal( ), 0, Lang.text.W_Mech_RowNetworkBest( ) );
		grid.setText( Row.NetworkWorst.ordinal( ), 0, Lang.text.W_Mech_RowNetworkWorst( ) );
		grid.setText( Row.TotalNoDelay.ordinal( ), 0, Lang.text.W_Mech_RowTotalNoDelay( ) );
		grid.setText( Row.DelayText.ordinal( ), 0, Lang.text.W_Mech_RowIfDelayed( ) );
		grid.setText( Row.IndividualDelay.ordinal( ), 0, Lang.text.W_Mech_RowIndividual( ) );
		grid.setText( Row.NetworkBestDelay.ordinal( ), 0, Lang.text.W_Mech_RowNetworkBest( ) );
		grid.setText( Row.NetworkWorstDelay.ordinal( ), 0, Lang.text.W_Mech_RowNetworkWorst( ) );
		grid.setText( Row.TotalIncDelay.ordinal( ), 0, Lang.text.W_Mech_RowTotalDelay( ) );
			
		// add task columns
		grid.getRowFormatter( ).addStyleName( 0, getCSS( ).gridheader( ) );
		int taskcol = 1;
		for( Task t : tasks ) {
			taskcols.put( t, taskcol );
			grid.setText( 0, taskcol++, t.getDescription( ) );
		}
		grid.setText( 0, taskcol, Lang.text.W_Mech_TotalCell( ) );
		
		// set styles for specific cells & rows
		for( int i = 1; i < grid.getRowCount( ); i++ )
			grid.getCellFormatter( ).addStyleName( i, 0, getCSS( ).gridlabelcol( ) );
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
				
		if( !gameDataReady( ) ) { clearColumn( idx ); return; }
				
		// get the planned method
		final PlanTask pt = getGameData( ).getJointPlan( ).getPlanned( task );
		if( pt == null ) {
			clearColumn( idx );
			return;
		}
		
		// get method delay status for delay part
		final DelayStatus delay = pt.getDelayStatus( );
		
		// get the payments and style
		final TTLScore payment = getGameData( ).getData( ).getPayments( task ); 
		final Style style = Style.CurrK;
		
		// fill in the task column
		updateValue( Row.Individual, idx, payment.getIndividual( ).getRegular( ), style );
		updateValue( Row.NetworkBest, idx, payment.getNetworkRegular( ).getBestRealised( ), style );
		updateValue( Row.NetworkWorst, idx, payment.getNetworkRegular( ).getRealisedDelta( ), style );
		updateValueSum( Row.TotalNoDelay, idx, Row.Individual, Row.NetworkWorst, DelayStatus.Pending, style );

		updateValue( Row.IndividualDelay, idx, payment.getIndividual( ).getDelayed( ), delay, style );
		updateValue( Row.NetworkBestDelay, idx, payment.getNetworkDelayed( ).getBestRealised( ), delay, style );
		updateValue( Row.NetworkWorstDelay, idx, payment.getNetworkDelayed( ).getRealisedDelta( ), delay, style );
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
			
			// FIXME make configurable
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
		for( int i = 0; i < taskcols.size( ); i++ )
			total += cache[ row ][ i ];
		
		final int totalidx = taskcols.size( );
		cache[ row ][ totalidx ] = total;
		grid.setText( row, grid.getColumnCount( ) - 1, Format.f( total, style ) );
	}
	
	/**
	 * @return The CSS of the widget
	 */
	private MechanismWidgetCSS getCSS( ) {
		return ClientView.getInstance( ).getResources( ).mechcss( );
	}
}
