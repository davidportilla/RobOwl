package carte;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Extension de Carte qui sert de moniteur. Cette classe est conçue pour
 * synchroniser le manager, les robots et l'interface graphique pendant la
 * simulation.
 * 
 * @author Maguelone Brac
 * @author Clement Delord
 * @author Thomas Fernandez
 * @author Clara Maurel
 * @author David Portilla Abellan
 * @author Patricia Ventura Diaz
 * @version 21-01-2013
 */
public class CarteSynchronized extends Carte {

	private static final Logger LOGGER = Logger
			.getLogger(Carte.class.getName());

	/**
	 * [idIncendie0, idIncendie1, ...] pour le manager
	 */
	private ArrayList<Long> incendiesAGerer;

	/**
	 * Id du robot -> position de l'incendie
	 */
	private HashMap<Long, Long> assignements;

	/**
	 * True si la simulation est mise en pause
	 */
	private boolean paused;

	/**
	 * @return l attribut paused
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Constructeur
	 * 
	 * @param noeuds
	 *            Noeuds de la carte
	 * @param arcs
	 *            Arcs de la carte
	 */
	public CarteSynchronized(HashMap<Long, Noeud> noeuds,
			HashMap<Long, Arc> arcs) {
		super(noeuds, arcs);
		this.incendiesAGerer = new ArrayList<Long>();
		for (long id : noeuds.keySet()) {
			if (noeuds.get(id).getIntensiteFeu() > 0) {
				this.incendiesAGerer.add(id);
			}
		}
		this.assignements = new HashMap<Long, Long>();
		this.paused = true;
	}

	/**
	 * Tableau avec les incendies pas encore pris en charge par le manager.
	 * 
	 * @return the incendiesAGerer
	 */
	public ArrayList<Long> getIncendiesAGerer() {
		return incendiesAGerer;
	}

	/**
	 * Attend si la liste d'incendies a gerer est vide. Efface l'incendie de la
	 * liste.
	 * 
	 * @return le premier incendie a gerer
	 */
	public synchronized long gererIncendie() {
		this.waitIfPaused();
		while (this.incendiesAGerer.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long incendie = this.incendiesAGerer.get(0);
		this.incendiesAGerer.remove(0);
		LOGGER.info("Le manager va gerer l'incendie en " + incendie);
		return incendie;
	}

	// MANAGER
	/**
	 * Ajoute un assignement et notifyAll.
	 * 
	 * @param robotId
	 * @param incendie
	 */
	public synchronized void assignerIncendie(long robotId, long incendie) {
		this.waitIfPaused();
		this.assignements.put(robotId, incendie);
		notifyAll();
	}

	// MANAGER
	/**
	 * Ajoute un incendie a gerer et notifyAll.
	 * 
	 * @param incendie
	 */
	public synchronized void addIncendieAGerer(long incendie) {
		this.waitIfPaused();
		this.incendiesAGerer.add(incendie);
		notifyAll();
	}

	// ROBOT
	/**
	 * Donne l'incendie assigne a l'id du thread que entre dans la methode. Attend
	 * s'il n'y en a pas.
	 * 
	 * @return long incendie assigne
	 */
	public synchronized long getAssignement() {
		this.waitIfPaused();
		long robotID = Thread.currentThread().getId();
		while (this.assignements.get(robotID) == null) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		long incendie = this.assignements.get(robotID);
		this.assignements.remove(robotID);
		return incendie;
	}

	// ROBOT
	/**
	 * Reduit l'intensite du feu dans l'incendie. NotifyAll().
	 * 
	 * @param incendie
	 */
	@Override
	public synchronized void reduireFeu(long incendie) {
		this.waitIfPaused();
		super.reduireFeu(incendie);
		notifyAll();
	}

	// ROBOT
	/**
	 * Quand un robot recharge, il notifie.
	 * 
	 * @param position
	 */
	public synchronized void recharger(long position) {
		this.waitIfPaused();
		notifyAll();
	}

	// GUI
	/**
	 * Augmente l'intensite du feu de l'incendie.
	 * 
	 * @param position
	 *            id du noeud
	 * @param unites
	 *            de 0 a 5
	 */
	public synchronized void augmenterFeu(long position, int unites) {
		if (super.getNoeud(position).getIntensiteFeu() == 0) {
			this.incendiesAGerer.add(position);
			notifyAll(); // notifier au manager
		}
		for (int i = 1; i <= unites; i++) {
			super.augmenterFeu(position);
		}
	}

	// GUI
	/**
	 * Ajoute une source dans la carte. Notifie a tous.
	 */
	@Override
	public synchronized void addSource(long position) {
		super.addSource(position);
		notifyAll(); // notifier aux robots
	}

	// GUI
	/**
	 * Reduit l'intensite du feu.
	 * 
	 * @param position
	 *            id du noeud
	 * @param unites
	 *            de 0 a 5
	 */
	public synchronized void reduireFeu(long position, int unites) {
		if (super.getNoeud(position).getIntensiteFeu() <= unites) {
			this.incendiesAGerer.remove(position);
		}
		for (int i = 1; i <= unites; i++) {
			super.reduireFeu(position);
		}
	}

	// EVERYBODY
	/**
	 * Attend si la simulation est mise en pause.
	 */
	public synchronized void waitIfPaused() {
		while (this.paused) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// GUI
	/**
	 * Pour commencer ou recommencer la simulation.
	 */
	public synchronized void restart() {
		this.paused = false;
		notifyAll();
		LOGGER.info("La simulation commence");
	}

	// GUI
	/**
	 * Mettre en pause la simulation.
	 */
	public synchronized void pause() {
		this.paused = true;
		LOGGER.info("La simulation est mise en pause");
	}

	// GUI
	/**
	 * Efface les donnees de la simulation.
	 */
	public synchronized void reset() {
		this.assignements = new HashMap<Long, Long>();
		this.paused = true;
		Long[] incendies = super.getIncendies().toArray(
				new Long[super.getIncendies().size()]);
		for (long id : incendies) {
			this.reduireFeu(id, 5);
		}
		Long[] sources = super.getSources().toArray(
				new Long[super.getSources().size()]);
		for (long id : sources) {
			super.enleverSource(id);
		}
		this.incendiesAGerer = new ArrayList<Long>(); // pas necessaire
		LOGGER.info("La simulation est arretee");
	}

}
