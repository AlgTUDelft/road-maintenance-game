package plangame.gwt.client.rpc;

import java.util.List;

import plangame.gwt.shared.LogType;
import plangame.gwt.shared.events.Event;
import plangame.gwt.shared.requests.ConnectRequest;
import plangame.gwt.shared.serverresponse.ConnectResponse;
import plangame.gwt.shared.state.GameServerInfo;
import plangame.model.object.BasicID;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of 
 */
@SuppressWarnings("javadoc")
public interface ClientRPCAsync {
	public void connect( ConnectRequest request, AsyncCallback<ConnectResponse> result );
	public void disconnect( BasicID clientID, AsyncCallback<Void> result );
	public void closing( BasicID clientID, AsyncCallback<Void> result );
	
	public void getServerList( AsyncCallback<List<GameServerInfo>> result );
	public void log( BasicID clientID, String message, LogType loglevel, AsyncCallback<Boolean> result );
	public void listen( BasicID clientID, AsyncCallback<List<Event>> result );
}
