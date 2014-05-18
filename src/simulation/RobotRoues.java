package simulation;

import carte.Arc.TypeArc;
import carte.CarteSynchronized;

/**
 * Robot a roues.
 * 
 * @author Maguelone Brac
 * @author Clement Delord
 * @author Thomas Fernandez
 * @author Clara Maurel
 * @author David Portilla Abellan
 * @author Patricia Ventura Diaz
 * @version 21-01-2013
 */
public class RobotRoues extends Robot {

	/**
	 * Vitesse du robot en conditions normals.
	 */
	public final static int VITESSENORMALE = 10;

	/**
	 * Capacite d'eau maximale du robot.
	 */
	public final static int EAUMAX = 5;

	/**
	 * Constructeur.
	 * 
	 * @param position
	 *            la position du robot
	 * @param carte
	 *            la carte sur laquelle evolue le robot
	 * 
	 */
	public RobotRoues(long position, CarteSynchronized carte) {
		super(position, carte, 5);
	}
	
	/**
	 * Constructeur.
	 * 
	 * @param position la position du robot
	 * @param carte la carte sur laquelle evolue le robot
	 * @param eau quantite d'eau initiale du robot
	 */
	public RobotRoues(long position, CarteSynchronized carte, int eau) {
		super(position, carte, eau);
	}

	/**
	 * Permet de connaitre la vitesse du robot sur tout type d'arc.
	 * 
	 * @return vitesse la vitesse du robot
	 */
	@Override
	public int getVitesse(TypeArc arc) {
		
		int vit = 0;
		
		if (arc == TypeArc.NORMAL)
		{
			vit = VITESSENORMALE*3 ;
		}
		
		if (arc == TypeArc.INONDE  || arc == TypeArc.ESCARPE)
		{
			vit = VITESSENORMALE/2 ;
		}		
		
		return vit ;
	}
	
	/**
	 * Run du robot.
	 */
	@Override
	public void run() {
		while(true){
			super.cycleDuRobot(EAUMAX);
		}
	}
	
}
