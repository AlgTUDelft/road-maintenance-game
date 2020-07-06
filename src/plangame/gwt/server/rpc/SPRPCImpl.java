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
package plangame.gwt.server.rpc;

import plangame.game.plans.JointPlan;
import plangame.game.plans.Plan;
import plangame.game.plans.PlanChange;
import plangame.game.player.PlanPreference;
import plangame.gwt.client.serviceprovider.SPRPC;
import plangame.gwt.shared.clients.SPClient;
import plangame.gwt.shared.exceptions.ClientNotConnectedException;
import plangame.gwt.shared.exceptions.ClientNotInGameException;
import plangame.gwt.shared.exceptions.GameServerException;
import plangame.gwt.shared.exceptions.InvalidClientStateException;
import plangame.gwt.shared.exceptions.InvalidGameStateException;
import plangame.gwt.shared.exceptions.NoServerException;
import plangame.gwt.shared.exceptions.NoSuchGameServerException;
import plangame.model.object.BasicID;
import plangame.model.tasks.Task;
import plangame.planner.PlannerException;

/**
 * Server side implementation of all service provider RPC methods
 *
 * @author Joris Scharpff
 */
@SuppressWarnings("serial")
public class SPRPCImpl extends GameClientImpl implements SPRPC {		
	/**
	 * @see plangame.gwt.client.serviceprovider.SPRPC#getSuggestion(plangame.model.object.BasicID, plangame.game.player.PlanPreference)
	 */
	@Override
	public Plan getSuggestion( BasicID clientID, JointPlan jplan, Task task, PlanPreference pref ) throws NoServerException, GameServerException, ClientNotConnectedException, ClientNotInGameException, NoSuchGameServerException {
		// get the client
		final SPClient client = getClient( clientID );
		try {
			return getGameServer( client ).getSuggestion( client, jplan, task, pref );
		} catch( PlannerException pe ) {
			throw new GameServerException( getGameServer( client ).getID( ), pe.getMessage( ) );
		}
	}
	
	/**
	 * @see plangame.gwt.client.serviceprovider.SPRPC#submitPlan(plangame.model.object.BasicID, plangame.game.plans.Plan)
	 */
	@Override
	public void submitPlan( BasicID clientID, Plan plan ) throws NoServerException, ClientNotConnectedException, ClientNotInGameException, NoSuchGameServerException {
		final SPClient client = getClient( clientID );
		getGameServer( client ).submitPlan( client, plan );
	}

	/**
	 * @see plangame.gwt.client.serviceprovider.SPRPC#acceptPlan(plangame.model.object.BasicID, boolean)
	 */
	@Override
	public void acceptPlan( BasicID clientID, boolean accept ) throws NoServerException, ClientNotConnectedException, ClientNotInGameException, NoSuchGameServerException {
		final SPClient client = getClient( clientID );
		getGameServer( client ).acceptPlan( client, accept );
	}
	
	/**
	 * @see plangame.gwt.client.serviceprovider.SPRPC#planChange(plangame.model.object.BasicID, plangame.game.plans.PlanChange)
	 */
	@Override
	public void planChange( BasicID clientID, PlanChange change ) throws InvalidGameStateException, InvalidClientStateException, NoServerException, ClientNotConnectedException, ClientNotInGameException, NoSuchGameServerException {
		final SPClient client = getClient( clientID );
		getGameServer( client ).planChanged( client, change );
	}
	
	/**
	 * @see plangame.gwt.client.serviceprovider.SPRPC#ackReassign(plangame.model.object.BasicID, plangame.model.object.BasicID)
	 */
	@Override
	public void ackReassign( BasicID oldID, BasicID newID ) throws NoServerException, ClientNotConnectedException {
		getServer( ).reassinged( oldID );
	}
	
	/**
	 * @throws ClientNotConnectedException 
	 * @see plangame.gwt.server.rpc.ClientImpl#getClient(plangame.model.object.BasicID)
	 */
	@Override
	public SPClient getClient( BasicID clientID ) throws NoServerException, ClientNotConnectedException {
		return (SPClient)super.getClient( clientID );
	}
}
