package plangame.gwt.client.resource.locale;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * Generated interface for the Lang.properties file
 */
@SuppressWarnings("javadoc")
public interface Lang extends Messages {
	/** @return The instance created by GWT */
	public Lang text = GWT.create( Lang.class );

	// General terms
	public String Week( );
	public String Weeks( );
	public String Task( );
	public String Tasks( );
	public String Method( );
	public String Methods( );
	public String Unassigned( );

	// General notifications
	public String NoTaskSelected( );
	public String SelectTask( );
	public String NoMethodSelected( );
	public String SelectMethod( );
	public String NoServerSelecetd( );
	public String SelectServer( );
	public String NoClientSelected( );
	public String SelectClient( );
	public String NoPlayerSelected( );
	public String SelectPlayer( );
	public String NoPortfolioSelected( );
	public String SelectPortfolio( );

	// Plan modifications
	/**
	 * @param arg0 The method 
	 * @param arg1 The reason for failure
	 * @return 
	 */
	public String PlanTaskFail( String arg0, String arg1 );
	/**
	 * @param arg0 The method 
	 * @param arg1 The reason for failure
	 * @return 
	 */
	public String UnplanTaskFail( String arg0, String arg1 );
	/**
	 * @param arg0 The task 
	 * @param arg1 The reason for failure
	 * @return 
	 */
	public String ChangeTaskFail( String arg0, String arg1 );

	// Plan errors
	/**
	 * @param arg0 The method
	 * @return 
	 */
	public String PlanError_AlreadyDelayed( String arg0 );
	/**
	 * @param arg0 The old method 
	 * @param arg1 The new method
	 * @return 
	 */
	public String PlanError_DifferentTasks( String arg0, String arg1 );
	/**
	 * @param arg0 The method
	 * @return 
	 */
	public String PlanError_Executed( String arg0 );
	/**
	 * @param arg0 Method 1 
	 * @param arg1 Method 2
	 * @return 
	 */
	public String PlanError_Overlap( String arg0, String arg1 );
	/**
	 * @param arg0 The method
	 * @return 
	 */
	public String PlanError_PlanInPast( String arg0 );
	/**
	 * @param arg0 The new method 
	 * @param arg1 The planned method
	 * @return 
	 */
	public String PlanError_TaskAlreadyPlanned( String arg0, String arg1 );
	/**
	 * @param arg0 The method
	 * @return 
	 */
	public String PlanError_UnableToComplete( String arg0 );

	// RPC Callback handling
	public String RPC_DialogTitle( );
	public String RPC_Refresh( );

	// Locale formats
	public String Format_Currency( );
	public String Format_CurrencyK( );
	public String Format_BigInt( );
	public String Format_IntK( );
	public String Format_Rank( );
	public String Format_Weeks( );

	// =====[ Client View ]=====
	// Client RPC calls
	public String Client_Connecting( );
	public String Client_ConnectFail( );
	public String Client_ReconnectFail( );
	public String Client_Disconnect( );
	public String Client_Disconnected( );
	/**
	 * @param arg0 The reason for disconnecting the client
	 * @return 
	 */
	public String Client_Kicked( String arg0 );
	public String Client_DisconnectFail( );
	public String Client_DisconnectText( );
	public String Client_LogFailed( );
	public String Client_UnloadNotifyFail( );
	public String Client_ListenFail( );
	public String Client_RetrievingServers( );
	public String Client_RetrieveServersFail( );
	public String Client_DisconnectSelf( );
	public String Client_DisconnectKick( );
	public String Client_DisconnectRestart( );
	public String Client_DisconnectEnd( );


	// =====[ Game View ]=====
	// RPC Commands
	public String GV_JoiningGame( );
	public String GV_JoinGameFail( );
	public String GV_Joined( String arg0 );
	public String GV_RestoringState( );
	public String GV_RestoreStateFail( );

	// Game events
	public String GV_GameStart( );
	public String GV_GameRestart( );
	public String GV_GameEnd( );

	// =====[ Game Manager ]=====
	// The window title
	public String GM_WindowTitle( );

	// UI text
	public String GM_TabClients( );
	public String GM_TabPlanning( );
	public String GM_StartGame( );
	public String GM_StartPlanning( );
	public String GM_StartExecution( );
	public String GM_StopExecute( );
	public String GM_ChangeGameState( );
	public String GM_ChangeClientState( );

	// RPC Commands
	public String GM_RetrievingPortfolios( );
	public String GM_RetrievePortfoliosFailed( );
	public String GM_AssignPortfolio( );
	public String GM_AssigningPortfolio( );
	public String GM_AssignPortfolioFail( );
	public String GM_StartingGame( );
	public String GM_StartGameFail( );
	public String GM_StartingPlanning( );
	public String GM_StartPlanningFail( );
	public String GM_ExecutingPlan( );
	public String GM_ExecutePlanFail( );
	public String GM_StoppingExecution( );
	public String GM_StopExecutionFail( );
	public String GM_SubmittingPending( );
	public String GM_SubmitPendingFail( );
	public String GM_ChangingGameState( );
	public String GM_ChangeGameStateFail( );
	public String GM_ChangingClientState( );
	public String GM_ChangeClientStateFail( );

	// State help titles and texts
	public String GM_StateInitialisingTitle( );
	public String GM_StateInitialising( );
	public String GM_StateStartingTitle( );
	public String GM_StateStarting( );
	public String GM_StateIdleTitle( );
	public String GM_StateIdle( );
	public String GM_StatePlanningTitle( );
	public String GM_StatePlanning( );
	public String GM_StateAcceptTitle( );
	public String GM_StateAccept( );
	public String GM_StateExecutingTitle( );
	public String GM_StateExecuting( );
	public String GM_StateFinishedTitle( );
	public String GM_StateFinished( );

	// =====[ Score board ]=====
	// The window title
	public String SB_WindowTitle( );

	// =====[ Service Provider ]=====
	// The window title
	public String SP_WindowTitle( );

	// UI text
	public String SP_FinanceTab( );
	public String SP_TrafficTab( );
	public String SP_MechanismTab( );
	public String SP_PlotsTab( );
	public String SP_OptionsTab( );

	// Commands
	public String SP_AddMethod( );
	public String SP_ChangeMethod( );
	public String SP_MoveMethod( );
	public String SP_RemoveMethod( );
	public String SP_SubmitPlan( );
	public String SP_AcceptPlan( );
	public String SP_DeclinePlan( );
	public String SP_RequestTaskSuggestion( );
	public String SP_RequestPlanSuggestion( );

	// Notifications
	public String SP_AllTasksPlanned( );
	public String SP_Reassigned( );
	public String SP_PlanContainsErrors( );
	public String SP_SubmitConfirm( );
	public String SP_SubmitConfirmUnplanned( );
	public String SP_MethodDelayed( String arg0 );
	public String SP_MethodCompleted( String arg0 );
	public String SP_MethodEditExecuting( String arg0 );
	public String SP_MethodRemoveExecuting( String arg0 );
	public String SP_MethodSuggestExecuting( String arg0 );
	public String SP_MethodEditNotMine( String arg0 );
	public String SP_MethodRemoveNotMine( String arg0 );
	public String SP_MethodSuggestNotMine( String arg0 );

	// RPC Commands
	public String SP_StartingPlanning( );
	public String SP_AllPlansSubmitted( );
	public String SP_JointPlanAccept( );
	public String SP_JointPlanDecline( );
	public String SP_RoundComplete( );
	public String SP_SubmittingPlan( );
	public String SP_SubmitFail( );
	public String SP_AcceptFail( );
	public String SP_RequestingSuggestion( );
	public String SP_RequestSuggestionFail( );
	public String SP_UpdatingProfile( );
	public String SP_UpdateProfileFail( );
	public String SP_ChangePlanFail( );
	public String SP_AckReassignFail( );

	// State titles
	public String SP_StateInitialisingTitle( );
	public String SP_StateAwaitingPortfolioTitle( );
	public String SP_StateWaitingToStartTitle( );
	public String SP_StateIdleTitle( );
	public String SP_StateInPlanningTitle( );
	public String SP_StateSubmittedTitle( );
	public String SP_StateAcceptingTitle( );
	public String SP_StateAcceptedTitle( );
	public String SP_StateDeclinedTitle( );
	public String SP_StateExecutingTitle( );
	public String SP_StateFinishedTitle( );
	public String SP_StateReconnectingTitle( );
	public String SP_StateDisconnectedTitle( );

	// State help texts
	public String SP_StateInitialising( );
	public String SP_StateAwaitingPortfolio( );
	public String SP_StateWaitingToStart( );
	public String SP_StateIdle( );
	public String SP_StateInPlanning( );
	public String SP_StateSubmitted( );
	public String SP_StateAccepting( );
	public String SP_StateAccepted( );
	public String SP_StateDeclined( );
	public String SP_StateExecuting( );
	public String SP_StateFinished( );
	public String SP_StateReconnecting( );
	public String SP_StateDisconnected( );


	// =====[ Server Manager ]=====
	// RPC calls
	public String SM_CreatingGame( );
	public String SM_CreateGameFail( );
	public String SM_RestartingGame( );
	public String SM_RestartGameFail( );
	public String SM_EndingGame( );
	public String SM_EndGameFail( );
	public String SM_Kicking( );
	public String SM_KickFail( );
	public String SM_KickingAll( );
	public String SM_KickedAll( String arg0 );
	public String SM_KickAllFail( );
	public String SM_RetrievingClients( );
	public String SM_RetrieveClientsFail( );
	public String SM_ReassignClient( );
	public String SM_ReassigningFailed( );

	// UI Labels
	public String SM_TabServers( );
	public String SM_TabClients( );

	// UI Commands
	public String SM_RefreshServers( );
	public String SM_CreateServer( );
	public String SM_RestartServer( );
	public String SM_EndServer( );
	public String SM_RefreshClients( );
	public String SM_AssignClient( );
	public String SM_Kick( );
	public String SM_KickAll( );

	// UI Notifications / dialogs
	public String SM_NoActiveServer( );
	public String SM_ConfirmRestart( );
	public String SM_ConfirmEnd( );
	public String SM_AssignInGame( );
	public String SM_AssignInvalidType( );

	// =====[ Widgets ]=====
	// The browser does not support the canvas element
	public String W_NoCanvasSupport( );

	// Score bar widget
	/**
	 * @param arg0 Best case 
	 * @param arg1 Delta
	 * @return 
	 */
	public String W_Score_Profits( String arg0, String arg1 );
	/**
	 * @param arg0 Best case 
	 * @param arg1 Delta
	 * @return 
	 */
	public String W_Score_TTLOverall( String arg0, String arg1 );
	/**
	 * @param arg0 Best case 
	 * @param arg1 Delta
	 * @return 
	 */
	public String W_Score_TTLMine( String arg0, String arg1 );

	// Finance widget
	public String W_Finance_TableTitle( );
	public String W_Finance_RowRevenue( );
	public String W_Finance_RowCost( );
	public String W_Finance_RowTTLCost( );
	public String W_Finance_RowProfitExRisk( );
	public String W_Finance_RowIfDelayed( );
	public String W_Finance_RowDelayCost( );
	public String W_Finance_RowTTLDelayCost( );
	public String W_Finance_RowProfits( );
	public String W_Finance_ColTotal( );
	public String W_Finance_OtherPlayer( );

	// TTL Widget
	public String W_TTL_ShowPayments( );
	public String W_TTL_TableTitle( );
	public String W_TTL_TotalCell( );
	public String W_TTL_RowIndividual( );
	public String W_TTL_RowNetworkBest( );
	public String W_TTL_RowNetworkWorst( );
	public String W_TTL_RowTotalNoDelay( );
	public String W_TTL_RowIfDelayed( );
	public String W_TTL_RowTotalDelay( );
	public String W_TTL_OtherPlayer( );

	// Mechanism Payments Widget
	public String W_Mech_TableTitle( );
	public String W_Mech_TotalCell( );
	public String W_Mech_RowIndividual( );
	public String W_Mech_RowNetworkBest( );
	public String W_Mech_RowNetworkWorst( );
	public String W_Mech_RowTotalNoDelay( );
	public String W_Mech_RowIfDelayed( );
	public String W_Mech_RowTotalDelay( );

	// TTL Timeline widget
	public String W_Timeline_PlotIdle( );
	public String W_Timeline_PlotIndividual( );
	public String W_Timeline_PlotNetworkBest( );
	public String W_Timeline_PlotNetworkWorst( );
	public String W_Timeline_XCaption( );
	public String W_Timeline_YCaptionAbs( );
	public String W_Timeline_YCaptionRel( );

	// Service Provider options widget
	public String W_SPOptions_TTLRelative( );
	public String W_SPOptions_TTLOnMap( );

	// Score manager widget
	public String W_SCMGR_SB_ColPlayerName( );
	public String W_SCMGR_SB_ColPortfolio( );
	public String W_SCMGR_SB_ColProfits( );
	public String W_SCMGR_SB_ColProfitRank( );
	public String W_SCMGR_SB_ColTTL( );
	public String W_SCMGR_SB_ColTTLRank( );

	// Player manager widget
	public String W_PMGR_NoPlayers( );
	public String W_PMGR_ColPlayerID( );
	public String W_PMGR_ColPlayerName( );
	public String W_PMGR_ColPlayerPortfolio( );
	public String W_PMGR_ColPlayerStatus( );
	public String W_PMGR_NoPlayerName( );

	// Client manager widget
	public String W_CMGR_NoClients( );
	public String W_CMGR_ColClientID( );
	public String W_CMGR_ColClientType( );
	public String W_CMGR_ColClientGame( );
	public String W_CMGR_ColClientPlayername( );
	public String W_CMGR_NoPlayerName( );
	public String W_CMGR_NotInGame( );

	// Server manager widgets
	public String W_SMGR_NoServers( );
	public String W_SMGR_ColServerID( );
	public String W_SMGR_ColServerName( );
	public String W_SMGR_ColServerPlayers( );
	public String W_SMGR_ColServerDesc( );

	// General dialog texts
	public String Dialog_Done( );
	public String Dialog_OK( );
	public String Dialog_Cancel( );

	// Start game dialog
	public String StartGameDialog_Title( );
	public String StartGameDialog_WelcomeTitle( String arg0 );
	public String StartGameDialog_ColourLabel( );
	public String StartGameDialog_MapLabel( );
	public String StartGameDialog_OK( );

	// End game dialog
	public String EndGameDialog_Title( );
	public String EndGameDialog_ResultsTitle( );
	public String EndGameDialog_ProfitTitle( );
	public String EndGameDialog_TTLTitle( );
	public String EndGameDialog_NetworkTTLTitle( );

	// The delay dialog
	public String DelayDialog_Title( );
	public String DelayDialog_Delay( );
	public String DelayDialog_DoneContinue( );

	// Re-assing client dialog
	public String ReassignDialog_Title( );
	public String ReassignDialog_Client( );
	public String ReassignDialog_SameClients( );

	// Assign portfolio dialog
	public String PortfolioDialog_Title( );

	// Start execution dialog
	public String ExecuteDialog_Title( );
	public String ExecuteDialog_Mode( );
	public String ExecuteDialog_Sleep( );
	public String ExecuteDialog_NoMode( );
	public String ExecuteDialog_InvalidSleep( );
	public String ExecuteDialog_ConfirmLargeSleep( );

	// Change State dialogs
	public String StateDialog_Title( );
	public String StateDialog_SelectState( );
	public String StateDialog_NoStateSelected( );

	// The method dialog
	public String MethodDialog_AddTask( );
	public String MethodDialog_EditTask( );
	public String MethodDialog_StepTask( );
	public String MethodDialog_StepMethod( );
	public String MethodDialog_StepTime( );
	public String MethodDialog_StepConfirm( );
	public String MethodDialog_NextStep( );
	public String MethodDialog_PrevStep( );
	/**
	 * @param arg0 The task name
	 * @return The value text in the task panel 
	 */
	public String MethodDialog_TaskStepValue( String arg0 );
	/**
	 * @param arg0 The task name
	 * @return The title in the task step 
	 */
	public String MethodDialog_TaskNameTitle( String arg0 );
	/**
	 * @param arg0 The method name
	 * @return The value text in the method panel 
	 */
	public String MethodDialog_MethodStepValue( String arg0 );
	/**
	 * @param arg0 The method name
	 * @return The title in the method step 
	 */
	public String MethodDialog_MethodNameTitle( String arg0 );
	/**
	 * @param arg0 The week number
	 * @return The value text in the plan panel 
	 */
	public String MethodDialog_PlanStepValue( String arg0 );
	public String MethodDialog_CostTitle( );
	public String MethodDialog_Duration( );
	public String MethodDialog_DelayProbability( );
	public String MethodDialog_DelayDuration( );
	public String MethodDialog_IfDelayed( );
	public String MethodDialog_Revenue( );
	public String MethodDialog_Penalty( );
	public String MethodDialog_TTLClass( );
	public String MethodDialog_Cost( );
	public String MethodDialog_ProfitExRisk( );
	public String MethodDialog_DelayCost( );
	public String MethodDialog_ProfitIncRisk( );
	public String MethodDialog_TTLImpactAvg( );
	public String MethodDialog_TTLTitle( );
	public String MethodDialog_TTLIndividual( );
	public String MethodDialog_TTLNetworkBest( );
	public String MethodDialog_TTLNetworkWorst( );
	public String MethodDialog_TotalTTLExRisk( );
	public String MethodDialog_TotalTTLPaymentExRisk( );
	public String MethodDialog_TTLIndividualDelay( );
	public String MethodDialog_TTLNetworkBestDelay( );
	public String MethodDialog_TTLNetworkWorstDelay( );
	public String MethodDialog_TotalTTLIncRisk( );
	public String MethodDialog_TotalTTLPaymentIncRisk( );
	public String MethodDialog_ConfirmMethodTitle( );
	public String MethodDialog_ConfirmTask( );
	public String MethodDialog_ConfirmMethod( );
	public String MethodDialog_ConfirmTime( );
	public String MethodDialog_ConfirmCostTitle( );
	public String MethodDialog_ConfirmTTLTitle( );
	public String MethodDialog_ConfirmRegular( );
	public String MethodDialog_ConfirmDelayed( );
	public String MethodDialog_ConfirmTotal( );
	public String MethodDialog_ConfirmPayment( );

	// New game dialog
	public String NewGameDialog_Title( );
	public String NewGameDialog_GameID( );
	public String NewGameDialog_GameName( );
	public String NewGameDialog_GameDesc( );
	public String NewGameDialog_GameFile( );
	public String NewGameDialog_GameMaxPlayers( );
	public String NewGameDialog_CreateGame( );
	public String NewGameDialog_DefaultGameIDPrefix( );
	public String NewGameDialog_DefaultGameName( );
	public String NewGameDialog_DefaultGameDesc( );
	/**
	 * @param arg0 The invalid ID
	 * @return The message when an invalid ID is specified 
	 */
	public String NewGameDialog_InvalidID( String arg0 );
	public String NewGameDialog_NoGameFile( );
	public String NewGameDialog_PlayerNumberInvalid( );
	public String NewGameDialog_PlayerNumberOutOfRange( );

	// Join game server dialog
	public String JoinDialog_Title( );
	public String JoinDialog_PlayerName( );
	public String JoinDialog_InvalidPlayerName( );
	public String JoinDialog_Join( );
	public String JoinDialog_Refresh( );
	public String JoinDialog_Disconnect( );

	// Request suggestion dialog
	public String RequestSuggestionDialog_TaskTitle( );
	public String RequestSuggestionDialog_PlanTitle( );
	public String RequestSuggestionDialog_TaskDesc( );
	public String RequestSuggestionDialog_PlanDesc( );
	public String RequestSuggestionDialog_SelectTask( );
	public String RequestSuggestionDialog_CostWeight( );
	public String RequestSuggestionDialog_QualityWeight( );
	public String RequestSuggestionDialog_TTLWeight( );
	public String RequestSuggestionDialog_TaskSuggestionFailed( );

	// Score & results widgets
	public String Score_MaintenanceCost( );
	public String Score_TTL( );
	public String Score_Quality( );
	public String Score_PlannedTask( );
	public String Score_PlannedMethod( );
	public String Score_Regular( );
	public String Score_Delay( );
	public String Score_Individual( );
	public String Score_Network( );
	public String Score_Total( );
	public String Score_QualityDemand( );
	public String Score_QualityResult( );

	// Task info widget
	public String TaskInfo_Asset( );
	public String TaskInfo_Revenue( );
	public String TaskInfo_IdleTTL( );
	public String TaskInfo_QualityDemand( );
	public String TaskInfo_MethodDuration( );
	public String TaskInfo_MethodCost( );
	public String TaskInfo_MethodDelayDuration( );
	public String TaskInfo_MethodDelayRisk( );
	public String TaskInfo_MethodDelayCost( );
	public String TaskInfo_MethodTTL( );
	public String TaskInfo_MethodQualityImpact( );
}
