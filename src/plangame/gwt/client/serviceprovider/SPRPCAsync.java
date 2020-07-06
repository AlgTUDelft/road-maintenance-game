package plangame.gwt.client.serviceprovider;

import plangame.game.plans.JointPlan;
import plangame.game.plans.Plan;
import plangame.game.plans.PlanChange;
import plangame.game.player.PlanPreference;
import plangame.gwt.client.rpc.GameClientRPCAsync;
import plangame.model.object.BasicID;
import plangame.model.tasks.Task;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>SPRPC</code>.
 */
@SuppressWarnings("javadoc")
public interface SPRPCAsync extends GameClientRPCAsync {
	public void getSuggestion( BasicID clientID, JointPlan jplan, Task task, PlanPreference pref, AsyncCallback<Plan> result );
	public void submitPlan( BasicID clientID, Plan plan, AsyncCallback<Void> result );
	public void acceptPlan( BasicID clientID, boolean accept, AsyncCallback<Void> result );
	public void planChange( BasicID clientID, PlanChange change, AsyncCallback<Void> callback );
	public void ackReassign( BasicID oldID, BasicID newID, AsyncCallback<Void> callback );
}
