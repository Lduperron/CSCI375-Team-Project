package core.shared;

import java.util.List;

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
	TEST
}