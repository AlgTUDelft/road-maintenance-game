/**
 * @file DataListBox.java
 * @brief Short description of file
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright © 2012 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         12 dec. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.controls;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * DataListBox defines a list box that keeps track of the associated objects.
 * Uses the toString method as default renderer, custom rendering may be 
 * specified using the setRenderer method. 
 *
 * @author Joris Scharpff
 * @param <T> The list data object type
 */
public class DataListBox<T> extends Composite implements HasValueChangeHandlers<T>, HasEnabled {
	/** The data list */
	protected List<T> data;
	
	/** The list box renderer */
	protected Renderer<T> renderer;
	
	/**
	 * Creates a new data list box
	 */
	public DataListBox( ) {
		initWidget( new ListBox( ) );
		
		// create empty data list
		data = new ArrayList<T>( );
		
		// create default renderer that uses toString
		renderer = new Renderer<T>( ) {
			@Override public String render( T object ) { return object.toString( );	}
			@Override public void render( T object, Appendable appendable ) throws IOException { appendable.append( render( object ) ); }
		};
		
		// also listen for key events
		// FIXME check if the value actually changed
		getListBox( ).addKeyUpHandler( new KeyUpHandler( ) {
			@Override public void onKeyUp( KeyUpEvent event ) {
				fireChange( );
			}
		} );
		getListBox( ).addKeyDownHandler( new KeyDownHandler( ) {
			@Override public void onKeyDown( KeyDownEvent event ) {
				fireChange( );
			}
		} );
	}
	
	/**
	 * Clears the list
	 */
	public void clear( ) {
		data.clear( );
		getListBox( ).clear( );
	}
	
	/**
	 * Adds an item to the list box
	 * 
	 * @param item The item to add
	 */
	public void addItem( T item ) {
		data.add( item );
		getListBox( ).addItem( item.toString( ) );
	}
	
	/**
	 * Adds an item at the specific index
	 * 
	 * @param item The item to add
	 * @param index The index to add it at
	 * @throws IndexOutOfBoundsException if the index is out of bounds
	 */
	public void addItem( T item, int index ) throws IndexOutOfBoundsException {
		data.add( index, item );
		getListBox( ).insertItem( item.toString( ), index );
	}
	
	/**
	 * Adds all items of the collection
	 * 
	 * @param items The items to add
	 */
	public void addAll( Collection<T> items ) {
		data.addAll( items );
		for( T item : items )
			getListBox( ).addItem( item.toString( ) );
	}
	
	/**
	 * Retrieves the item at the index
	 * 
	 * @param index The index of the item
	 * @return The item at the index
	 * @throws IndexOutOfBoundsException if the index is out of range (index < 0
	 * || index >= size())
	 */
	public T getItem( int index ) throws IndexOutOfBoundsException {
		return data.get( index );
	}
	
	/**
	 * Removes an item from the list. More specifically, it removes the first
	 * object o for which o.equals( item ) evaluates to true.
	 * 
	 * @param item The item to remove
	 * @return The removed object or null if not found
	 */
	public T remove( T item ) { 
		// find the index of the item in the data array
		final int idx = data.indexOf( item );
		if( idx == -1 ) return null;
		
		// check if removing this item will change the index
		final int curridx = getListBox( ).getSelectedIndex( );
		
		// remove the item
		getListBox( ).removeItem( idx );
		final T removed = data.remove( idx );
		
		// check if the list index has changed
		if( curridx != getListBox( ).getSelectedIndex( ) ) fireChange( );
		
		// return the removed object
		return removed;
	}
	
	/**
	 * Replaces the object in the list by the new object
	 * 
	 * @param oldobject The list item to be replaced
	 * @param newobject The new list item
	 * @return The old object or null if not found
	 */
	public void replace( T oldobject, T newobject ) {
		final int idx = data.indexOf( oldobject );
		data.set( idx, newobject );
		getListBox( ).removeItem( idx );
		getListBox( ).insertItem( newobject.toString( ), idx );
	}
	
	/**
	 * Returns the selected text item
	 * 
	 * @return The selected text or null if no item is selected
	 */
	public String getSelectedText( ) {
		if( !isSelected( ) ) return null;
		return getListBox( ).getItemText( getListBox( ).getSelectedIndex( ) );
	}
	
	/**
	 * Retrieves the currently selected item or null if none is selected
	 * 
	 * @return The selected item or null if nothing is selected
	 */
	public T getSelectedItem( ) {
		if( !isSelected( ) ) return null;
		return data.get( getListBox( ).getSelectedIndex( ) );
	}
	
	
	/**
	 * Sets the selected item with or without firing a value change event
	 * 
	 * @param item The item to select
	 * @param event True to fire a value change event on change
	 */
	public void setSelectedItem( T item, boolean event ) {
		final int idx = data.indexOf( item );
		
		setSelectedIndex( idx, event );
	}
	
	/**
	 * Sets the selected item. More specifically, selects the first item o for
	 * which o.equals( item ) evaluates to true.
	 * 
	 * @param item The item to select
	 */
	public void setSelectedItem( T item ) {
		setSelectedItem( item, true );
	}
	
	/**
	 * Sets the selected item by its index, optionally fires a value change event
	 * 
	 * @param index The index of the item to set
	 * @param event True to fire a value change event on changes
	 * @throws IndexOutOfBoundsException if index < 0 || index >= size( ) 
	 */
	public void setSelectedIndex( int index, boolean event ) {
		if( index < 0 || index >= size( ) )
			throw new IndexOutOfBoundsException( "Index: " + index + ", size: " + size( ) );

		final boolean changed = isSelected( ) && (getListBox( ).getSelectedIndex( ) != index);
		getListBox( ).setSelectedIndex( index );
		
		// if changed, fire change event
		if( event && changed ) fireChange( );
	}
	
	/**
	 * Sets the selected item by its index, fires value change event on change
	 * 
	 * @param index The index of the item to select
	 * @throws IndexOutOfBoundsException if index < 0 || index >= size( ) 
	 */
	public void setSelectedIndex( int index ) {
		setSelectedIndex( index, true );
	}
	
	/**
	 * Checks whether a selection is made
	 * 
	 * @return True iff any item is selected
	 */
	public boolean isSelected( ) {
		return getListBox( ).getSelectedIndex( ) != -1;
	}
	
	/**
	 * Returns the list of objects associated with the list box items
	 * 
	 * @return The list data collection
	 */
	public Collection<T> getData( ) {
		return data;
	}

	/**
	 * @return The number of items in the list
	 */
	public int size( ) {
		return getData( ).size( );
	}
	
	/**
	 * Sets the number of displayed items
	 * 
	 * @param size The number of items to be displayed
	 */
  public void setVisibleItemCount( int size ) {
    getListBox( ).setVisibleItemCount( size );
  }

  /**
   * Initialises the DataListBox widget
   * 
   * @see com.google.gwt.user.client.ui.Composite#initWidget(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  protected void initWidget( Widget widget ) {
    super.initWidget( widget );
    getListBox( ).addChangeHandler( new ChangeHandler( ) {
      @Override public void onChange( ChangeEvent event ) {
      	fireChange( );
      }
    });
  }
  
  /**
   * Fires a change event
   */
  private void fireChange( ) {
    ValueChangeEvent.fire( this, getSelectedItem( ) );
  }

  /**
	 * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
	 */
	@Override
	public HandlerRegistration addValueChangeHandler( ValueChangeHandler<T> handler ) {
		return addHandler(handler, ValueChangeEvent.getType());
	}
	
	 /**
   * @return The actual list box
   */
  private ListBox getListBox() {
    return (ListBox) getWidget();
  }
  
  /**
   * Enables or disables the use of the list box
   * 
   * @param enabled True to enable the box
   */
  @Override public void setEnabled( boolean enabled ) {
  	getListBox( ).setEnabled( enabled );
  }
  
  /**
   * @return True if the DataListBox is enabled
   */
  @Override public boolean isEnabled( ) {
  	return getListBox( ).isEnabled( );
  }
  
  /**
   * Gives or takes the focus from this DataListBox
   * 
   * @param focus True to give the focus
   */
  public void setFocus( boolean focus ) {
  	getListBox( ).setFocus( focus );
  }
}
