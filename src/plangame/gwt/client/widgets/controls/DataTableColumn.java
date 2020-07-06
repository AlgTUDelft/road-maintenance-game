/**
 * @file DataTableColumn.java
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


/**
 * Column for the data table
 *
 * @author Joris Scharpff
 * @param <T> The data table key object
 */
public abstract class DataTableColumn<T> {
	/**
	 * Called to render the contents of the column given object t
	 * 
	 * @param key The key object to render in this column cell
	 * @return The HTML String to render in the table
	 */
	public abstract String render( T key );
}
