/**
 * @file JointPlanUpdate.java
 * @brief [brief description]
 *
 * This file is created at Almende B.V. It is open-source software and part of the Common
 * Hybrid Agent Platform (CHAP). A toolbox with a lot of open-source tools, ranging from
 * thread pools and TCP/IP components to control architectures and learning algorithms.
 * This software is published under the GNU Lesser General Public license (LGPL).
 *
 * Copyright � 2013 Joris Scharpff <joris@almende.com>
 *
 * @author       Joris Scharpff
 * @date         11 sep. 2013
 * @project      NGI
 * @company      Almende B.V.
 */
package plangame.gwt.client.gamedata;

import plangame.game.plans.JointPlan;
import plangame.game.plans.PlanChange;


/**
 * Callbacks when then joint plan is updates
 * 
 * @author Joris Scharpff
 */
public interface JointPlanUpdateListener extends DataUpdateListener {
	/**
	 * Called whenever the joint plan is set
	 * 
	 * @param jplan The new joint plan
	 */
	public void onJointPlanSet( JointPlan jplan );
	
	/**
	 * Called whenever a plan change is applied to the current joint plan
	 * 
	 * @param change The plan change that was applied
	 * @param validated True if the change was validated
	 */
	public void onJointPlanChange( PlanChange change, boolean validated );
}
