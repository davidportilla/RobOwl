package pathfinder;

/**
 * Signale que c'est impossible de trouver un chemin.
 * 
 * @author Maguelone Brac
 * @author Clement Delord
 * @author Thomas Fernandez
 * @author Clara Maurel
 * @author David Portilla Abellan
 * @author Patricia Ventura Diaz
 * @version 21-01-2013
 */
public class PathNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an PathNotFound with null as its error detail message.
	 */
	public PathNotFoundException() {
		super();
	}

	/**
	 * Constructs an PathNotFound with the specified detail message.
	 * 
	 * @param String
	 *            message
	 */
	public PathNotFoundException(String message) {
		super(message);
	}

	/**
	 * Constructs an PathNotFound with the specified detail message and cause.
	 * 
	 * @param String
	 *            message
	 * @param Throwable
	 *            cause
	 */
	public PathNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a PathNotFound with the specified cause and a detail message
	 * of (cause==null ? null : cause.toString()) (which typically contains the
	 * class and detail message of cause).
	 * 
	 * @param Throwable
	 *            cause
	 */
	public PathNotFoundException(Throwable cause) {
		super(cause);
	}
}
