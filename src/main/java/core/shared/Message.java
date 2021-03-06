package core.shared;

/**
 * Enumerated values for Message to be passed from client to server.
 */
public enum Message {


	/**
	 * Connected to client/server, and waiting for game start
	 */
	CONNECT,

	/**
	 * The user has quit the game
	 */
	QUIT,

	/**
	 * Test functionality
	 */
	TEST, SPAWN, YOUCONTROL, NEWOBJECT, REQUESTSTATE, OBJMOVE, OBJRELOCATE, REQUESTMOVE, MOUSEEVENTTOSERVERONOBJECT, MOUSEEVENTTOSERVERONTILE, MOUSEEVENTFROMSERVER, COLLISION, REMOVEOBJECT,
	
	/**
	 * Health change
	 */
	CHANGEHEALTH,
	
	/*
	 * Update enemy direction on client
	 */
	UPDATEENEMY
}