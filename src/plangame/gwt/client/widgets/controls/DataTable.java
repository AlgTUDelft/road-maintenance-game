/**
 * @file DataTable.java
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
 * @date         22 aug. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.controls;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Custom wrapper for data grid implementation, class is abstract so that the
 * user can fill in column set & fill methods
 *
 * @author Joris Scharpff
 * @param <T> The data table object key
 */
public abstract class DataTable<T> extends Composite {
	/** The panel containing the grid and pager */
	protected VerticalPanel panel;
	
	/** The data provider for the table */
	protected ListDataProvider<T> data;
	
	/** The actual data grid */
	protected DataGrid<T> grid;
	
	/** The pager */
	protected SimplePager pager;
	
	/** If true, the table will always refresh on changes */
	protected boolean autorefresh;
	
	/**
	 * Creates a new data table
	 */
	public DataTable( ) {
		super( );
		
		// set auto refresh, can be disabled for performance
		setAutoRefresh( true );
		
		// initialise the widget
		panel = new VerticalPanel( );
		panel.setWidth( "100%" );
		
		// create the datagrid and pager
		grid = new DataGrid<T>( 10 );
		pager = new SimplePager( );
		pager.setDisplay( grid );
		
		// setup grid columns
		initColumns( );
		
		// setup data provider
		data = new ListDataProvider<T>( );
		data.addDataDisplay( grid );
		
		// setup selection model
		grid.setSelectionModel( new SingleSelectionModel<T>( ) );
				
		// initialise the widget
		panel.add( grid );
		panel.add( pager );
		initWidget( panel );		
	}
	
	/**
	 * Shows or hides the table pager
	 * 
	 * @param show True to show the pager
	 */
	public void setShowPager( boolean show ) {
		pager.setVisible( show );
	}
	
	/**
	 * @return True if the pager is shown
	 */
	public boolean isPagerShown( ) {
		return pager.isVisible( );
	}
	
	/**
	 * Abstract function that should be overridden to implement columns. Columns
	 * can be added easily using the addColumn function.
	 */
	public abstract void initColumns( );
	
	/**
	 * Adds a column to the data grid
	 * 
	 * @param header The column header text
	 * @param width CSS string setting the width
	 * @param column The cell renderer
	 */
	public void addColumn( String header, String width, final DataTableColumn<T> column ) {
		final Column<T, String> newcol = new Column<T, String>( new TextCell( ) ) {
			@Override public String getValue( T object ) { return column.render( object ); }
		};
		grid.addColumn( newcol, header );
		grid.setColumnWidth( newcol, width );		
	}
	
	/**
	 * Sets the sort handler for the column
	 * 
	 * @param index The column index
	 * @param comparator The column sort comparator
	 * @throws IndexOutOfBoundsException if the column index is not found
	 */
	@SuppressWarnings("unchecked")
	public void addSortHandler( int index, Comparator<T> comparator ) {
		final Column<T, String> col = (Column<T, String>)grid.getColumn( index );
		
		// enable sorting
		col.setSortable( true );
		
		// create sort handler
		final ListHandler<T> lh = new ListHandler<T>( getList( ) );
		lh.setComparator( col, comparator );
		grid.addColumnSortHandler( lh );
		
		grid.getColumnSortList( ).push( col );
	}
	
	/**
	 * Enforces a sort on the table
	 */
	public void sort( ) {
		ColumnSortEvent.fire( grid, grid.getColumnSortList( ) );
	}
	
	/**
	 * Sets the string to be displayed when the table is empty
	 * 
	 * @param text The text
	 */
	public void setEmptyText( String text ) {
		grid.setEmptyTableWidget( new Label( text ) );
	}
	
	/**
	 * @return The list underlying the table
	 */
	private List<T> getList( ) {
		return data.getList( );
	}
	
	/**
	 * @return The selection model
	 */
	@SuppressWarnings("unchecked")
	private SingleSelectionModel<T> getSM( ) {
		return ((SingleSelectionModel<T>) grid.getSelectionModel( ));
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject#setHeight(java.lang.String)
	 */
	@Override
	public void setHeight( String height ) {
		super.setHeight( height );
		
		grid.setHeight( height );
	}
	
	/**
	 * @see com.google.gwt.user.client.ui.UIObject#setWidth(java.lang.String)
	 */
	@Override
	public void setWidth( String width ) {
		super.setWidth( width );
		
		grid.setWidth( width );
	}
	
	/**
	 * Enables or disables the auto refresh option. If enabled, the table will
	 * always be refreshed on any table changes. If disabled, the table will
	 * refresh for most operations but sometimes requires a manual refresh. This
	 * is usually the case when columns render using functions on the object.
	 * 
	 * @param enabled True to enable auto refresh
	 */
	public void setAutoRefresh( boolean enabled ) {
		autorefresh = enabled;
	}
	
	/**
	 * Checks the autorefresh parameter, if true refresh will be performed
	 */
	private void autoRefresh( ) {
		if( autorefresh ) refresh( );
	}
	
	/**
	 * Refreshes (redraws) the table data. Can be called manually to force an
	 * update of the displayed data. If the auto refresh setting is enabled, this
	 * function will be called after every list update.
	 */
	public void refresh( ) {
		data.refresh( );
		grid.onResize( );
		grid.redraw( );
	}
	
	/**
	 * Adds an item to the data table
	 * 
	 * @param item The item to add
	 */
	public void addItem( T item ) {
		getList( ).add( item );
		autoRefresh( );
	}
	
	/**
	 * Adds all the specified items
	 * 
	 * @param items The items to add
	 */
	public void addAll( Collection<T> items ) {
		getList( ).addAll( items );
		autoRefresh( );
	}
	
	/**
	 * Sets the items to display in the list, clears previous items
	 * 
	 * @param items The new items
	 */
	public void setItems( Collection<T> items ) {
		getList( ).clear( );
		addAll( items );
	}
	
	/**
	 * Removes the item from the list, uses the equals method to find the item
	 * 
	 * @param item The item to remove
	 * @return True if the item was found and removed
	 */
	public boolean removeItem( T item ) {
		return getList( ).remove( item );
	}
	
	/**
	 * Searches the list for any item that equals( ) this object and replaces it
	 * by the specified item. If no item matches, the item is added to the list.
	 * 
	 * @param item The item to add or used to replace
	 * @return The replaced item or null if added
	 */
	public T setItem( T item ) {
		// seach for the element
		final int idx = getList( ).indexOf( item );
		if( idx == -1 ) {
			// none found, add it
			addItem( item );
			return null;
		} else {
			// found one, replace it
			final T old = getList( ).get( idx );
			getList( ).set( idx, item );
			return old;
		}
	}
	
	/**
	 * Clears the data table
	 */
	public void clear( ) {
		getList( ).clear( );
		autoRefresh( );
	}
	
	/**
	 * @return The entire list
	 */
	public List<T> getAll( ) {
		return getList( );
	}
	
	/**
	 * @return The table list size
	 */
	public int size( ) {
		return getList( ).size( );
	}
	
	/**
	 * Retrieves the currently selected item
	 * 
	 * @return The selected item or null if no item is selected
	 */
	public T getSelectedItem( ) {
		return getSM( ).getSelectedObject( );
	}
}
