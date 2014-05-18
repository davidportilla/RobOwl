package carte;

/**
 * Cette classe represente un noeud.
 * 
 * @author Maguelone Brac
 * @author Clement Delord
 * @author Thomas Fernandez
 * @author Clara Maurel
 * @author David Portilla Abellan
 * @author Patricia Ventura Diaz
 * @version 21-01-2013
 */
public class Noeud {

	/**
	 * Id unique du noeud
	 */
	private long id;

	/**
	 * Latitude du noeud.
	 */
	private double latitude;

	/**
	 * Longitude du noeud.
	 */
	private double longitude;

	/**
	 * Altitude du noeud.
	 */
	private double altitude;

	/**
	 * Represente l'intensite de feu qui va de 0 (pas de feu) a 5.
	 */
	private int intensiteFeu;

	/**
	 * Un noeud est une station si un robot peut s'y recharger en eau.
	 */
	private boolean isSource;

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Constructeur.
	 * 
	 * @param id
	 * @param latitude
	 * @param longitude
	 * @param isSource
	 */
	public Noeud(long id, double latitude, double longitude, boolean isSource) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.isSource = isSource;
		this.intensiteFeu = 0;
		this.altitude = 0;
	}

	/**
	 * Constructeur.
	 * 
	 * @param id
	 * @param latitude
	 * @param longitude
	 * @param altitude
	 * @param intensiteFeu
	 * @param isSource
	 */
	public Noeud(long id, double latitude, double longitude, double altitude,
			boolean isSource) {
		this(id, latitude, longitude, isSource);
		this.altitude = altitude;
	}

	/**
	 * Permet d'augmenter le feu d'une unite.
	 * Si le feu est à 5, il n'augmente pas.
	 */
	synchronized void augmenterFeu() {
		if (this.intensiteFeu < 5) {
			this.intensiteFeu++;
		}
	}

	/**
	 * Permet de reduire le feu d'une unite. A 0 le noeud redevient un noeud
	 * normal (feu eteint).
	 * Si le feu est à 0, il ne reduit pas.
	 */
	synchronized void reduireFeu() {
		if (this.intensiteFeu > 0) {
			this.intensiteFeu--;
		}
	}

	/**
	 * Permet de savoir l'intensite de l'incendie.
	 * 
	 * @return un entier de 0 (pas d incendie) a 5 (intensite maximale)
	 */
	public int getIntensiteFeu() {
		return this.intensiteFeu;
	}

	/**
	 * Permet de connaitre la latitude du noeud.
	 * 
	 * @return latitude la latitude du noeud
	 */
	public double getLatitude() {
		return this.latitude;
	}

	/**
	 * Permet de connaitre la longitude du noeud.
	 * 
	 * @return longitude la longitude du noeud
	 */
	public double getLongitude() {
		return this.longitude;
	}

	/**
	 * Permet de connaitre l'altitude du noeud.
	 * 
	 * @return latitude l'altitude du noeud
	 */
	public double getAltitude() {
		return this.altitude;
	}

	/**
	 * Permet de savoir si le noeud est une station de recharge.
	 * 
	 * @return true si le noeud est une station, false sinon
	 */
	public boolean isSource() {
		return this.isSource;
	}

	/**
	 * Modifie station de recharge un noeud (true or false).
	 * 
	 * @param b
	 */
	void setSource(boolean b) {
		this.isSource = b;
	}

	/**
	 * Retourne un String avec les coordonees du noeud, ses caracteristiques, et
	 * son etat (incendie ou pas et avec quelle intensite)
	 */
	@Override
	public String toString() {
		return "Noeud " + this.id + '\n' + "| Position: (lat: " + this.latitude
				+ ", lng: " + this.longitude + ")" + '\n' + "| Source: "
				+ this.isSource + '\n' + "| Intesite du feu: "
				+ this.intensiteFeu + '\n';
	}

	/**
	 * Equals si latitude et longitude egales.
	 * 
	 * @param noeud
	 * @return true si egals, false ailleurs.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Noeud)) {
			return false;
		}
		Noeud noeud = (Noeud) obj;
		return (this.getLatitude() == noeud.getLatitude() && this
				.getLongitude() == noeud.getLongitude());
	}

}