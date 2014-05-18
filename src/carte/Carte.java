package carte;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.logging.Logger;

/**
 * Representation d'une carte avec des noeuds et des arcs.
 * 
 * @author Maguelone Brac
 * @author Clement Delord
 * @author Thomas Fernandez
 * @author Clara Maurel
 * @author David Portilla Abellan
 * @author Patricia Ventura Diaz
 * @version 21-01-2013
 */
public class Carte extends Observable {

	private static final Logger LOGGER = Logger
			.getLogger(Carte.class.getName());

	/**
	 * Map avec les noeuds de la carte. Key value: l'id du noeud.
	 */
	private HashMap<Long, Noeud> noeuds;

	/**
	 * Arcs de la carte.
	 */
	private HashMap<Long, Arc> arcs;
	
	/**
	 * Ids. des noeuds incendies
	 */
	private HashSet<Long> incendies;
	
	/**
	 * Ids. des noeuds sources
	 */
	private HashSet<Long> sources;

	/**
	 * Constructeur.
	 * 
	 * @param noeuds
	 * @param arcs
	 */
	public Carte(HashMap<Long, Noeud> noeuds, HashMap<Long, Arc> arcs) {
		this.noeuds = noeuds;
		this.arcs = arcs;
		this.incendies = new HashSet<Long>();
		this.sources = new HashSet<Long>();
		for (long id : noeuds.keySet()) {
			if (noeuds.get(id).getIntensiteFeu()>0) {
				this.incendies.add(id);
			}
		}
		for (long id : noeuds.keySet()) {
			if (noeuds.get(id).isSource()) {
				this.sources.add(id);
			}
		}
	}
	
	/**
	 * 
	 * @return noeuds de la carte
	 */
	public synchronized Noeud[] getNoeuds() {
		return this.noeuds.values().toArray(
				new Noeud[this.noeuds.values().size()]);
	}
	
	/**
	 * 
	 * @return map avec les noeuds de la carte
	 */
	public synchronized HashMap<Long, Noeud> getNoeudsMap() {
		return this.noeuds;
	}
	
	/**
	 * 
	 * @return le tableau d'id des noeuds
	 */
	public synchronized long[] getIdNoeuds() {
		Noeud[] n = this.getNoeuds();
		long[] nId = new long[n.length];
		for (int i = 0; i < nId.length; i++) {
			nId[i] = n[i].getId();
		}
		return nId;
	}
	
	/**
	 * 
	 * @return arcs de la carte
	 */
	public synchronized Arc[] getArcs() {
		return this.arcs.values().toArray(new Arc[this.arcs.values().size()]);
	}
	
	/**
	 * 
	 * @return map avec les arcs de la carte
	 */
	public synchronized HashMap<Long, Arc> getArcsMap() {
		return this.arcs;
	}
	
	/**
	 * 
	 * @param id
	 * @return Noeud repere par cet id
	 */
	public synchronized Noeud getNoeud(long id) {
		return this.noeuds.get(id);
	}

	/**
	 * 
	 * @param id
	 * @return Arc repere par cet id
	 */
	public synchronized Arc getArc(long id) {
		return this.arcs.get(id);
	}
	
	/**
	 * Return l'id d'un Noeud
	 * 
	 * @param noeud
	 * @return id du noeud
	 */
	public synchronized long getIdByNoued(Noeud noeud) {
		return noeud.getId();
	}

	/**
	 * Return l'id d'un arc
	 * 
	 * @param arc
	 * @return id de l'arc
	 */
	public synchronized long getIdByArc(Arc arc) {
		return arc.getId();
	}
	
	/**
	 * @return les incendies
	 */
	public synchronized HashSet<Long> getIncendies() {
		return this.incendies;
	}
	
	/**
	 * @return les sources
	 */
	public synchronized HashSet<Long> getSources() {
		return this.sources;
	}

	/**
	 * Permet d'augmenter le feu sur un noeud.
	 * 
	 * @param idNoeud
	 */
	public synchronized void augmenterFeu(long idNoeud) {
		this.noeuds.get(idNoeud).augmenterFeu();
		if (this.noeuds.get(idNoeud).getIntensiteFeu() == 1) {
			this.incendies.add(idNoeud);
			LOGGER.info("Nouvel incendie en " + idNoeud);
		}
		this.notifierObservateurs("Feu augmenté en" + idNoeud);
		//LOGGER.info("Feu augmente en " + idNoeud);
	}

	/**
	 * Permet de reduire le feu sur un noeud.
	 * 
	 * @param idNoeud
	 */
	protected synchronized void reduireFeu(long idNoeud) {
		this.noeuds.get(idNoeud).reduireFeu();
		if (this.noeuds.get(idNoeud).getIntensiteFeu() == 0) {
			this.incendies.remove(idNoeud);
			LOGGER.info("Feu eteint en " + idNoeud);
		} else {
			this.notifierObservateurs("Feu reduit en" + idNoeud);
			//LOGGER.info("Feu reduit en " + idNoeud);
		}
	}

	/**
	 * Permet d'ajouter une source.
	 * 
	 * @param idNoeud
	 */
	public synchronized void addSource(long idNoeud) {
		Noeud n = this.noeuds.get(idNoeud);
		n.setSource(true);
		this.noeuds.put(idNoeud, n);
		this.sources.add(idNoeud);
		this.notifierObservateurs("source creee en " + idNoeud);
	}
	
	/**
	 * Permet de supprimer une source.
	 * 
	 * @param idNoeud
	 */
	public synchronized void enleverSource(long idNoeud) {
		Noeud n = this.noeuds.get(idNoeud);
		n.setSource(false);
		this.noeuds.put(idNoeud, n);
		this.sources.remove(idNoeud);
		this.notifierObservateurs("source enleve de " + idNoeud);
	}

	/**
	 * Notifie aux observateurs les changements dans la carte.
	 * 
	 * @param arg un objet
	 */
	public synchronized void notifierObservateurs(Object arg) {
		this.setChanged();
		this.notifyObservers(arg);
	}

	/**
	 * Retrouve l'arc entre deux noeuds.
	 * @param noeud1
	 * @param noeud2
	 * @return id de l'arc
	 */
	public synchronized long getArcId(long noeud1, long noeud2) {
		long arc = 0;
		for (Entry<Long, Arc> e : arcs.entrySet()) {
			Arc a = e.getValue();
			if (noeuds.get(noeud1).equals(a.getNoeuds()[0])
					&& noeuds.get(noeud2).equals(a.getNoeuds()[1])) {
				arc = e.getKey();
			} else if (noeuds.get(noeud1).equals(a.getNoeuds()[1])
					&& noeuds.get(noeud2).equals(a.getNoeuds()[0])) {
				arc = e.getKey();
			}
		}
		return arc;
	}
	
	/**
	 * 
	 * @param noeudId 
	 * @return HashSet avec les id des noeuds voisins
	 */
	public synchronized HashSet<Long> getVoisins(long noeudId) {
		HashSet<Long> voisins = new HashSet<Long>();
		for (Arc arc : this.arcs.values()) {
			if (arc.getNoeuds()[0].getId() == noeudId) {
				voisins.add(arc.getNoeuds()[1].getId());
			} else if (arc.getNoeuds()[1].getId() == noeudId) {
				voisins.add(arc.getNoeuds()[0].getId());
			}
		}
		return voisins;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public synchronized String toString() {
		return "Carte [noeuds=" + noeuds + ", incendies=" + incendies
				+ ", sources=" + sources + ", arcs=" + arcs + "]";
	}
	
}