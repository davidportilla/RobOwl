package carte;

/**
 * Cette classe represente un chemin entre deux noeuds.
 * 
 * @author Maguelone Brac
 * @author Clement Delord
 * @author Thomas Fernandez
 * @author Clara Maurel
 * @author David Portilla Abellan
 * @author Patricia Ventura Diaz
 * @version 21-01-2013
 */
public class Arc {

	/**
	 * Variable auxiliere pour generer des id uniques
	 */
	private static long uniqueID;

	/**
	 * Id de l'arc
	 */
	private long id;

	/**
	 * Extremite 1 de l'arc.
	 */
	private Noeud noeud1;

	/**
	 * Extremite 2 de l'arc.
	 */
	private Noeud noeud2;

	/**
	 * Longueur de l'arc en km.
	 */
	private double longueur;

	/**
	 * Type d'arc: inonde, plat ou escarpe
	 */
	private TypeArc type;

	/**
	 * True si l'arc est en sens unique
	 */
	private boolean oneWay;

	/**
	 * Vitesse max. en km/h
	 */
	private int maxSpeed;

	/**
	 * Rayon moyen de la terre en kilometres.
	 */
	public final static int RAYON_TERRE = 6370;

	/**
	 * Enum avec les differents types d'arc que l'on peut avoir: <br>
	 * NORMAL, ESCARPE, INONDE
	 */
	public enum TypeArc {
		NORMAL, ESCARPE, INONDE
	}

	/**
	 * Constructeur. Vitesse max. = 130 km/h. Double sens.
	 * 
	 * @param noeud1
	 *            noeud de depart de l arc
	 * @param noeud2
	 *            noeud arrivee de l arc
	 * @param type
	 *            type de l arc (normal, inonde, escarpe)
	 * 
	 */
	public Arc(Noeud noeud1, Noeud noeud2, TypeArc type) {
		this.id = uniqueID++;
		this.noeud1 = noeud1;
		this.noeud2 = noeud2;
		this.type = type;
		this.longueur = calculerLongueur(noeud1, noeud2);
		this.oneWay = false;
		this.maxSpeed = 130;
	}

	/**
	 * Constructeur.
	 * 
	 * @param noeud1
	 *            noeud de depart de l arc
	 * @param noeud2
	 *            noeud arrivee de l arc
	 * @param type
	 *            type de l arc (normal, inonde, escarpe)
	 * @param oneWay
	 *            true si l'arc est a sens unique
	 * @param maxSpeed
	 *            vitesse max. en km/h
	 */
	public Arc(Noeud noeud1, Noeud noeud2, TypeArc type, boolean oneWay,
			int maxSpeed) {
		this(noeud1, noeud2, type);
		this.oneWay = oneWay;
		this.maxSpeed = maxSpeed;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Permet de connaitre les deux noeuds extremites de l arc.
	 * 
	 * @return Noeud[2] avec les deux extremes de l'arc
	 */
	public Noeud[] getNoeuds() {
		Noeud[] noeuds = new Noeud[2];
		noeuds[0] = this.noeud1;
		noeuds[1] = this.noeud2;
		return noeuds;
	}

	/**
	 * @return la longueur de l arc en km
	 */
	public double getLongueur() {
		return this.longueur;
	}

	/**
	 * @return le type d'arc: normal, escarpe ou inonde
	 */
	public TypeArc getType() {
		return this.type;
	}

	/**
	 * @return true si l'arc est a sens unique
	 */
	public boolean isOneWay() {
		return oneWay;
	}

	/**
	 * @return vitesse max. en km/h
	 */
	public int getMaxSpeed() {
		return this.maxSpeed;
	}

	/**
	 * Modifie le type de l'arc
	 * 
	 * @param type le type de l'arc
	 *
	 */
	public void setType(TypeArc type) {
		this.type = type;
	}

	/**
	 * @param maxSpeed
	 *            vitesse max en km/h de l'arc.
	 */
	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	/**
	 * Calcule la longueur de l'arc sans utiliser l'altitude de chaque noeud.
	 * 
	 * @return longueur la longueur de l'arc en km
	 */
	private static double calculerLongueur(Noeud n1, Noeud n2) {
		double lat1 = n1.getLatitude();
		double lon1 = n1.getLongitude();
		double lat2 = n2.getLatitude();
		double lon2 = n2.getLongitude();

		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		dist = dist * 1.609344; // in km
		return (dist);
	}

	/**
	 * Convertit les degres en radians
	 * 
	 * @param deg parametre a convertir
	 * @return le parametre converti
	 */
	public static final double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/**
	 * Convertit les radians en degres
	 * 
	 * @param rad parametre a convertir
	 * @return le parametre converti
	 */
	public static final double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}

	/**
	 * Retourne un String qui represente l'arc.
	 * @return String representant l'arc
	 */
	@Override
	public String toString() {
		return "Arc [id=" + id + ", noeud1=" + noeud1.getId() + ", noeud2="
				+ noeud2.getId() + ", longueur=" + longueur + ", type=" + type
				+ ", oneWay=" + oneWay + ", maxSpeed=" + maxSpeed + "]";
	}

	/**
	 * Deux arcs sont egaaux si leurs deux extremites ont les memes coordonnees.
	 * 
	 * @return true si arcs egaux, false sinon.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Arc)) {
			return false;
		} else {
			Arc arc = (Arc) obj;
			return arc.getNoeuds()[0].equals(this.noeud1)
					&& arc.getNoeuds()[1].equals(this.noeud2);
		}
	}

}