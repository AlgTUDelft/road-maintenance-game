/**
 * @file TreeTable.java
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
 * @date         5 apr. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.controls;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Flextable implementation with sections that can be collapsed 
 *
 * @author Joris Scharpff
 */
public class TreeTable extends FlexTable {
	/** Table sections */
	protected List<TableSection> sections;
	
	/** Sections currently under construction (to prevent going through them all) */
	protected Stack<TableSection> current;
	
	/** The number of columns to use per row, set to -1 for arbitrary */
	protected int columns;
			
	/**
	 * Creates a new TreeTable
	 */
	public TreeTable( ) {
		super( );
		
		// create sections and current building section arrays
		sections = new ArrayList<TableSection>( );
		current = new Stack<TableSection>( );
		
		// set column number to arbitrary
		columns = -1;
	}
	
	/**
	 * Sets the number of column to be used in the table from now on, makes sure
	 * all rows added in the future will have this number of columns. Set this
	 * value to -1 to use arbitrary number of columns per row
	 * 
	 * @param columns The number of columns
	 */
	public void setColumns( int columns ) {
		this.columns = columns;
	}
	
	/**
	 * Marks the start if a new section that can be collapsed/expanded
	 * 
	 * @param header The header text that is displayed
	 */
	public void startSection( String header ) {
		startSection( header, null );
	}
	
	/**
	 * Marks the start of a new section that can be collapsed/expanded. The 
	 * handler is called every time a section in collapsed or expanded 
	 * 
	 * @param header The header text that is displayed
	 * @param handler Section event handler
	 */
	public void startSection( String header, TableSectionHandler handler ) {
		// add a row for the section
		final int row = addRow( );
		
		// add a new section, details are filled in later
		final TableSection sec = new TableSection( this, header, row, handler );
		setWidget( 0, sec.getHeader( ) );
		sections.add( sec );
		current.push( sec );
		
		// add CSS classes for styling the header
		addRowCSS( row, "tt-header" );
		addRowCSS( row, "tt-header" + current.size( ) );
	}
	
	/**
	 * Ends the current section
	 * 
	 * @throws RuntimeException if there is no current section to end
	 */
	public void endSection( ) throws RuntimeException {
		if( current.size( ) == 0 ) throw new RuntimeException( "There are no current sections to end" );
		
		// pop the section from the current sections stack
		current.pop( );
	}
	
	/**
	 * Adds a row to the table with the same number of columns as the previous
	 * row. Creates a single column if this is the first row. Note that
	 * startSection also creates a row and hence influences the number of columns
	 * that will be created by this function.
	 * 
	 * @return The row index
	 */
	public int addRow( ) {
		if( getRowCount( ) == 0 ) return addRow( (columns == -1 ? 1 : columns) );
		
		return addRow( getCellCount( getRowCount( ) - 1 ) );
	}
	
	/**
	 * Adds a row to the table with the specified number of columns
	 * 
	 * @param columns The number of columns
	 * @return The row index
	 */
	public int addRow( int columns ) {
		// check if the number of columns match the set number
		assert (this.columns == -1 || this.columns == columns) : "The number of columns does not match demanded number of columns";
		
		// add the row
		final int row = getRowCount( );
		insertRow( row );
		for( int i = 0; i < columns; i++ )
			addCell( row );
		
		// add CSS for in-section rows
		if( current.size( ) > 0 )
			addRowCSS( "section-row" );
		
		// expand table sections
		expand( row );
		
		// and return the row index
		return row;
	}
	
	/**
	 * Adds a row and fills the columns with the specified texts. The number of
	 * columns the row will have equals the number of strings specified.
	 * 
	 * @param text The column texts
	 * @return The index of the new row
	 */
	public int addRow( String... text ) {
		assert (text.length > 0) : "Empty column text array";
		assert (columns == -1 || text.length == columns) : "Column texts do not equal demanded column number";
		
		// add the row
		final int row = addRow( text.length );
		for( int i = 0; i < text.length; i++ )
			setText( i, text[i] );
		
		return row;
	}
	
	/**
	 * Adds the new row to all sections under construction
	 * 
	 * @param row The row number to include in current sections
	 */
	private void expand( int row ) {
		for( TableSection sec : current )
			sec.expand( );
		
		// add style names for the rows
		addRowCSS( row, "tt-section" );
	}
	
	/**
	 * Sets the widget for the column of the last added row
	 * 
	 * @param column The column
	 * @param widget The widget to set
	 * @throws IndexOutOfBoundsException
	 */
	public void setWidget( int column, Widget widget ) {
		setWidget( getRowCount( ) - 1, column, widget );
	}
	
	/**
	 * Sets the text that is displayed in the column of the last added row
	 * 
	 * @param column The column to set text in
	 * @param text The text to set
	 */
	public void setText( int column, String text ) {
		setText( getRowCount() - 1, column, text );
	}
	
	/**
	 * Adds the CSS class to the last added cell
	 * 
	 * @param css The CSS class
	 */
	public void addCellCSS( String css ) {
		addCellCSS( getRowCount( ) - 1, getCellCount( getRowCount( ) - 1 ) - 1, css );
	}
	
	/**
	 * Adds the CSS class to the specified cell of the last added row
	 * 
	 * @param col The cell column index
	 * @param css The CSS class
	 */
	public void addCellCSS( int col, String css ) {
		addCellCSS( getRowCount( ) - 1, col, css );
	}
	
	/**
	 * Adds the CSS class to the cell
	 * 
	 * @param row The row index
	 * @param col The column index
	 * @param css The CSS class to add
	 */
	public void addCellCSS( int row, int col, String css ) {
		getCellFormatter( ).addStyleName( row, col, css );
	}
	
	/**
	 * Adds the CSS class to the last added row
	 * 
	 * @param css The CSS class
	 */
	public void addRowCSS( String css ) {
		addRowCSS( getRowCount( ) - 1, css );
	}
	
	/**
	 * Adds the CSS class to the specified row
	 * 
	 * @param row The row index
	 * @param css The CSS class
	 * @throws IndexOutOfBoundsException
	 */
	public void addRowCSS( int row, String css ){
		getRowFormatter( ).addStyleName( row, css );
	}
	
	/**
	 * Table section
	 */
	static class TableSection {
		/** The table */
		protected TreeTable table;
		
		/** The table header */
		protected TableSectionHeader header;
		
		/** Handler for section events */
		protected TableSectionHandler handler;
		
		/** The section start row */
		protected int start;
		
		/** The section end row */
		protected int end;
		
		/**
		 * Creates a new table section
		 * 
		 * @param table The enclosing TreeTable
		 * @param header The header text
		 * @param startrow The row index at which the section starts
		 * @param handler The handler for section events, null for no handling
		 */
		public TableSection( TreeTable table, String header, int startrow, TableSectionHandler handler ) {
			this.table = table;
			this.header = new TableSectionHeader( this, header );
			this.handler = handler;
			
			start = startrow;
			end = startrow;
		}
		
		/**
		 * Retrieves the section header
		 * 
		 * @return The header object
		 */
		protected TableSectionHeader getHeader( ) {
			return header;
		}
		
		/**
		 * Toggles the display of the header
		 */
		protected void toggle( ) {
			// only sensible if there is at least one row below
			if( start == end ) return;
			
			// show / hide the row
			final RowFormatter rf = table.getRowFormatter( );
			final boolean vis = !rf.isVisible( start + 1 );
			for( int r = start + 1; r <= end; r++ )
				rf.setVisible( r, vis );
			
			// notify the handler
			if( handler != null ) {
				if( vis )
					handler.onExpand( this );
				else
					handler.onCollapse( this );
			}
				
		}
		
		/**
		 * Adds a row to this section
		 */
		protected void expand( ) {
			end++;
		}
	}
	
	/**
	 * Handler for table section events
	 */
	interface TableSectionHandler {
		/**
		 * Called when the section is collapsed
		 * 
		 * @param section The section that was collapsed
		 */
		public void onCollapse( TableSection section );
		
		/**
		 * Called when the section is expanded
		 * 
		 * @param section The section that was expanded
		 */
		public void onExpand( TableSection section );
	}
	
	/**
	 * Headers for TableSections
	 */
	static class TableSectionHeader extends HTML {
		/** The section for which this is the header */
		protected TableSection section;
		
		/** The header text to display */
		protected String header;
		
		/**
		 * Creates a new header
		 * 
		 * @param section The section for which this is a header
		 * @param header The header text to display
		 */
		protected TableSectionHeader( final TableSection section, String header ) {
			this.section = section;
			this.header = header;
			
			setText( header );
			addClickHandler( new ClickHandler( ) {
				@Override
				public void onClick( ClickEvent event ) {
					section.toggle( );
				}
			} );
		}
	}
}
