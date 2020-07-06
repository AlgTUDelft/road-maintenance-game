/**
 * @file PlanViewWIdget.java
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
 * @date         17 sep. 2012
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.widgets.taskinfo;

import plangame.gwt.client.resource.locale.Format;
import plangame.gwt.client.resource.locale.Format.Style;
import plangame.model.tasks.Task;
import plangame.model.tasks.TaskMethod;
import plangame.model.time.TimePoint;
import plangame.model.time.TimeSpan;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Joris Scharpff
 */
public class TaskInfoWidget extends Composite {
	protected interface TaskInfoWidgetUIBinder extends UiBinder<Widget, TaskInfoWidget> { }
	protected TaskInfoWidgetUIBinder TaskInfoWidgetUI = GWT.create( TaskInfoWidgetUIBinder.class );
	
	/** The HTML components */
	@UiField protected Label lblNoMethod;
	@UiField protected HTMLPanel pnlCardContainer;
	@UiField protected VerticalPanel pnlCard;
		
	// task info
	@UiField protected Label lblTaskName;
	@UiField protected Label lblDesc;
	@UiField protected Label lblAsset;
	@UiField protected Label lblRevenue;
	@UiField protected Label lblIdleTTL;
	@UiField protected Label lblQualityDemandDesc;
	@UiField protected Label lblQualityDemand;
	
	// method info
	@UiField protected Label lblMethod;
	@UiField protected Label lblDuration;
	@UiField protected Label lblCost;
	@UiField protected Label lblDelayDuration;
	@UiField protected Label lblDelayRisk;
	@UiField protected Label lblDelayCost;
	@UiField protected Label lblMethodTTL;
	@UiField protected Label lblQualityImpactDesc;
	@UiField protected Label lblQualityImpact;
	
	/** The task method that is currently shown in the info panel */
	protected TaskMethod method;
	
	/**
	 * Creates a new plan view widget
	 */
	public TaskInfoWidget( ) {
		super( );
		
		// initialise the widget UI
		initWidget( TaskInfoWidgetUI.createAndBindUi( this ) );
		
		// initialise the widget variables
		showQuality( false );
		setMethod( null );
	}
	
	/**
	 * Sets the method that is shown in the panel, updated on the next repaint
	 * call to the widget.
	 * 
	 * @param method The new method to display, null to clear info
	 */
	public void setMethod( TaskMethod method ) {
		this.method = method;
		
		// repaint the control
		paint( );
	}
	
	/**
	 * Updates the info
	 */
	private void paint( ) {
		if( method == null ) {
			lblNoMethod.setVisible( true );
			pnlCard.setVisible( false );
			return;
		} 
		
		// show the task card
		lblNoMethod.setVisible( false );
		pnlCard.setVisible( true );
		
		// fill in the info
		// task info first
		final Task task = method.getTask( );
		lblTaskName.setText( task.getDescription( ) );
		lblDesc.setText( task.getLongDescription( ) );
		lblAsset.setText( task.getAsset( ).getDescription( ) );
		lblRevenue.setText( Format.f( task.getRevenue( ), Style.CurrK ) );
		lblIdleTTL.setText( task.getAsset( ).getTTLClass( ) );
		lblQualityDemand.setText( Format.f( task.getQualityDemand( ) ) );
		
		// FIXME cost is now computed as if the task is planned at the start of the
		// plan, this is correct now because we use fixed costs. However, if costs
		// are time dependent this is incorrect.
		
		// methods
		lblMethod.setText( method.getDescription( ) );
		lblDuration.setText( Format.f( method.getRegularDuration( ).getWeeks( ), Style.Weeks ) );
		lblCost.setText( Format.f( method.getCost( new TimeSpan( new TimePoint( ), method.getRegularDuration( ) ) ), Style.CurrK ) );
		lblDelayDuration.setText( Format.f( method.getDelayDuration( ).getWeeks( ), Style.Weeks ) );
		lblDelayRisk.setText( Format.f( method.getDelayRisk( ), Style.Percentage ) );
		lblDelayCost.setText( Format.f( method.getCost( new TimeSpan( new TimePoint( ), method.getDelayDuration( ) ) ), Style.CurrK ) );
		lblMethodTTL.setText( method.getTTLImpact( ) );
		lblQualityImpact.setText( Format.f( method.getQualityImpact( ) ) );
	}
	
	/**
	 * Enables or disables the display of the quality objective
	 * 
	 * @param show True if the quality objective should be displayed
	 */
	public void showQuality( boolean show ) {
		lblQualityDemand.setVisible( show );
		lblQualityDemandDesc.setVisible( show );
		lblQualityImpact.setVisible( show );
		lblQualityImpactDesc.setVisible( show );
	}
	
	/**
	 * @return True if the quality objective is shown in the UI
	 */
	public boolean showsQuality( ) {
		return lblQualityDemand.isVisible( );
	}
}
