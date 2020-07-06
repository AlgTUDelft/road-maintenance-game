/**
 * @file FinanceWidgetCSS.java
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
 * @date         1 sep. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.resource.css;

import com.google.gwt.resources.client.CssResource;

/**
 * Finance Widget CSS resource
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("javadoc")
public interface TTLTableWidgetCSS extends CssResource {
	String mainpanel( );
	String otherpanel( );
	String taskselect( );
	String grid( );
	String paymentcheck( );
	@ClassName("grid-labelcol") String gridlabelcol( );
	@ClassName("grid-labelcolcell") String gridlabelcolcell( );
	@ClassName("grid-header") String gridheader( );
	@ClassName("grid-selectedname") String gridselectedname( );
	@ClassName("grid-selectedrow") String gridselectedrow( );
	@ClassName("grid-delayrow") String griddelayrow( );
	@ClassName("grid-delaycell") String griddelaycell( );
	@ClassName("grid-totalscellex") String gridtotalscellex( );
	@ClassName("grid-totalscell") String gridtotalscell( );
	@ClassName("grid-evenrow") String gridevenrow( );
	@ClassName("grid-oddrow") String gridoddrow( );	
	@ClassName("grid-evencol") String gridevencol( );
	@ClassName("grid-oddcol") String gridoddcol( );	
	@ClassName("gridcell-pending") String gridcellpending( );
	@ClassName("gridcell-delayed") String gridcelldelayed( );
	@ClassName("gridcell-asplanned") String gridcellasplanned( );
	@ClassName("gridcell-asplannednocost") String gridcellasplannednocost( );
}
