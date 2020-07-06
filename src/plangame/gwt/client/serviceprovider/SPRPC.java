/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package plangame.gwt.client.serviceprovider;

import plangame.game.plans.JointPlan;
import plangame.game.plans.Plan;
import plangame.game.plans.PlanChange;
import plangame.game.player.PlanPreference;
import plangame.gwt.client.rpc.GameClientRPC;
import plangame.gwt.shared.exceptions.ClientNotConnectedException;
import plangame.gwt.shared.exceptions.ClientNotInGameException;
import plangame.gwt.shared.exceptions.GameServerException;
import plangame.gwt.shared.exceptions.InvalidClientStateException;
import plangame.gwt.shared.exceptions.InvalidGameStateException;
import plangame.gwt.shared.exceptions.NoServerException;
import plangame.gwt.shared.exceptions.NoSuchGameServerException;
import plangame.model.object.BasicID;
import plangame.model.tasks.Task;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * All service provider specific RPC methods
 *
 * @author Joris Scharpff
 */
@RemoteServiceRelativePath("SPRPC")
public interface SPRPC extends GameClientRPC {		
	/**
	 * Asks the server for an automatic suggestion for a plan or task
	 * 
	 * @param clientID The client to find a suggestion for
	 * @param jplan The joint plan to start from
	 * @param task The task to request a suggestion for
	 * @param pref The client's plan preferences
	 * @return The suggestion
	 * @throws GameServerException if the planner failed
	 * @throws NoServerException
	 * @throws NoSuchGameServerException 
	 * @throws ClientNotInGameException 
	 * @throws ClientNotConnectedException 
	 */
	public Plan getSuggestion( BasicID clientID, JointPlan jplan, Task task, PlanPreference pref ) throws NoServerException, GameServerException, ClientNotConnectedException, ClientNotInGameException, NoSuchGameServerException;
		
	/**
	 * Submits the plan to the server
	 * 
	 * @param clientID The client submitting the plan
	 * @param plan The plan to submit
	 * @throws NoSuchGameServerException 
	 * @throws ClientNotInGameException 
	 * @throws ClientNotConnectedException 
	 * @throws NoServerException 
	 */
	public void submitPlan( BasicID clientID, Plan plan ) throws ClientNotConnectedException, ClientNotInGameException, NoSuchGameServerException, NoServerException;
	
	/**
	 * Accepts or declines the current joint plan
	 * 
	 * @param clientID The client accepting / declining
	 * @param accept True if the client accepts the plan
	 * @throws NoServerException
	 * @throws NoSuchGameServerException 
	 * @throws ClientNotInGameException 
	 * @throws ClientNotConnectedException 
	 */
	public void acceptPlan( BasicID clientID, boolean accept ) throws NoServerException, ClientNotConnectedException, ClientNotInGameException, NoSuchGameServerException;

	/**
	 * Notifies the game server of a change in the client plan
	 * 
	 * @param clientID The client
	 * @param change The plan change
	 * @throws InvalidGameStateException
	 * @throws NoServerException
	 * @throws ClientNotConnectedException
	 * @throws ClientNotInGameException
	 * @throws NoSuchGameServerException
	 * @throws InvalidClientStateException 
	 */
	public void planChange( BasicID clientID, PlanChange change ) throws InvalidGameStateException, NoServerException, ClientNotConnectedException, ClientNotInGameException, NoSuchGameServerException, InvalidClientStateException;
	
	/**
	 * Acknowledges the server that we have reassigned the client of our view
	 * 
	 * @param oldID The ID of the client that was replaced
	 * @param newID The ID of the new client
	 * @throws NoServerException
	 * @throws ClientNotConnectedException
	 */
	public void ackReassign( BasicID oldID, BasicID newID ) throws NoServerException, ClientNotConnectedException;
}
