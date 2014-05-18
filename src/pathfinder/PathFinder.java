package pathfinder;

import java.util.ArrayList;
import java.util.logging.Logger;

import simulation.Robot;
import simulation.RobotDrone;
import carte.Arc;
import carte.Arc.TypeArc;
import carte.Carte;

/**
 * Classe pour trouver le chemin le plus rapide.
 * 
 * @author Maguelone Brac
 * @author Clement Delord
 * @author Thomas Fernandez
 * @author Clara Maurel
 * @author David Portilla Abellan
 * @author Patricia Ventura Diaz
 * @version 21-01-2013
 */
public class PathFinder {

	private static final Logger LOGGER = Logger.getLogger(PathFinder.class.getName());
	
	/**
	 * Types d'algoritme utilisables: DIJKSTRA et AETOILE
	 */
	public enum Algorithme {
		DIJKSTRA, AETOILE
	}
	
	/**
	 * Détermine le chemin le plus cours de la position du robot actuelle à son but via un algorithme particulier.
	 * @param robot Robot qui effectue une requete
	 * @param butId Id du noeud objectif
	 * @param alg Algorithme a utiliser
	 * @return ItineraireRobot l'itineraire a temps de parcours minimal
	 * @throws PathNotFoundException cas ou aucun chemin n'est trouve
	 */
	public static ItineraireRobot findPath(Robot robot, long butId, Algorithme alg) throws PathNotFoundException {
		long startTime = System.nanoTime();
		
		//++++++++++++++++++++INITIALISATION++++++++++++++++++++
		
		ItineraireRobot itineraire = null; //la sortie de la fonction findPath
		Carte carte = robot.getCarte();
		long[] noeuds = carte.getIdNoeuds(); // Tableau regroupant tous les id des noeuds de la carte
		Arc[] arcs = carte.getArcs(); // Tableau regroupant tous les arcs de la carte
		long nstart = robot.getPosition();
		
		if(robot instanceof RobotDrone){
			//On fabrique les elements constituants l'itineraire
			ArrayList<Long> chemin = new ArrayList<Long>();
			ArrayList<Double> tempsParcours = new ArrayList<Double>();
			
			//Le drone, quelquesoit sa position, se dirige directement vers l'objectif
			chemin.add(butId);
			
			//On calcule ensuite le temps de parcours du vol
			Arc voloiseau = new Arc(carte.getNoeud(robot.getPosition()),carte.getNoeud(butId),TypeArc.NORMAL);
			tempsParcours.add(calculTempsParcours(voloiseau,robot));
			return new ItineraireRobot(chemin, tempsParcours);
			
		}
		else{
			switch (alg) {
			case DIJKSTRA:
				
				//On recherche l'indice du noeud de depart dans le tableau des noeuds
				int start = rechercheNoeud(nstart , noeuds); //l'indice du noeud de depart dans le tableau des noeuds
				
				if(start == noeuds.length){//si le depart n'est pas sur la carte
					LOGGER.severe("Depart absent de la carte");
					throw new PathNotFoundException("Depart absent de la carte");
				}
					
				ArrayList<double[]> mTemps = new ArrayList<double[]>(); //matrice de l'algorithme
				int iNoeudCourant = 2+start; //Index du noeud courant (en colonne donc +2 par rapport au tableau de noeud original) dans la boucle de l'algorithme
				int futurnoeud = iNoeudCourant; //index du futur noeud (en colonne donc +2 par  rapport au tableau de noeud original)de l'itin�raire
							
				boolean continuer = true;
						
				//++++++++++++++++++++BOUCLE DE L'ALGORITHME++++++++++++++++++++

				while(continuer == true){
						
					//+++++ Processus de creation d'une nouvelle ligne de la matrice +++++//
								
					int ligneprecedente = chercherLigneNoeud(mTemps, iNoeudCourant);//On recherche l'indice de la ligne du noeud precedent			
					int nouvelleligne = chercherLigneNoeud(mTemps, futurnoeud); //On recherche l'indice de la ligne du noeud precedent
								
					//On regarde si on a deja explore l'itineraire considere (si on a deja cree une ligne avec ce noeud)
					if (nouvelleligne == mTemps.size()){ //si ce n'est pas le cas on cree une nouvelle ligne dans la matrice correspondant a ce noeud
		
						double[] temps = new double[noeuds.length+2]; // tableau ligne des temps de parcours du noeud courant vers chaque voisin
						
						for(int i = 0; i < temps.length; i++){//de base toutes les cases contiennent des zeros
							temps[i]=0;
						}
						
						temps[0] = ligneprecedente; //indique de quelle ligne et donc quel noeud a ete choisi
						temps[1]=futurnoeud; //indice du noeud actuel (correspondant a la colonne (donc +2 par rapport au tableau noeud initial))
						
						ArrayList<int[]> tabvoisins = voisins(futurnoeud - 2,robot); //tableau des index (ref tableau noeud) des voisins du noeud courant
						
						for(int i = 0; i < tabvoisins.size(); i++){// Remplit la matrice des temps de parcours pour les voisins du noeud courant. Ces temps sont les temps totaux de l'itineraire en cours (somme)
	
							temps[tabvoisins.get(i)[0]+2]=calculTempsParcours(arcs[tabvoisins.get(i)[1]],robot);
							
							if (mTemps.size()>0){
								temps[tabvoisins.get(i)[0]+2]=temps[tabvoisins.get(i)[0]+2]+mTemps.get((int)temps[0])[(int)temps[1]];
							}
						}
						
						mTemps.add(temps);//Ajout de la nouvelle ligne dans la matrice de l'algorithme			
					}
					
					else{//si c'est le cas on change juste la ligne de provenance du futur noeud
						mTemps.get(nouvelleligne)[0]=ligneprecedente;
					}

					//++++++ Selection du prochain noeud +++++
					
					iNoeudCourant = futurnoeud; //Le noeud courant devient le futur noeud
	
					if (noeuds[iNoeudCourant-2] == butId){//si le NoeudCourant est le noeud objectif, on sort de la boucle de l'algorithme
						continuer = false;
					}
					
					else{//sinon on choisit le prochain noeud le plus interessant
						double[] next = new double[3];
						
						next = choixDuProchainNoeud(mTemps , noeuds); // [0] le noeud courant, [1] le futur noeud et [2] le temps correspondant au couple choisi et -1 si tout a ete explore
		
						if(next[2] == -1){//si tous les itineraires possibles ont ete explores en vain
							LOGGER.severe("Le robot est isole de l'objectif");
							throw new PathNotFoundException("Le robot est isole de l'objectif");
						}
						else{ // sinon on assigne les nouveaux noeuds utilises
							iNoeudCourant = (int) next[0];
							futurnoeud = (int) next[1];
							
						}
						
					}		
					
				}		
				
				//++++++++++++++++++++RECUPERATION DE L'ITINERAIRE++++++++++++++++++++
				
				//On fabrique les elements constituants l'itineraire
				ArrayList<Long> chemin = new ArrayList<Long>();
				ArrayList<Double> tempsParcours = new ArrayList<Double>();
				
				//On construit un tableau ordonne des lignes de la matrice de l'algo constituants l'itineraire
				ArrayList<Integer> lignes = new ArrayList<Integer>(); // attention, pour contuire l'itineraire on doit "remonter" dans mTemps
				
				lignes.add(mTemps.size()-1); // on ajoute la derniere ligne, celle du noeud objectif
				
				while (lignes.get(lignes.size()-1)!=0){// tant que l'on ne retombe pas sur la ligne de depart
					lignes.add((int)mTemps.get(lignes.get(lignes.size()-1))[0]);
				}
				
				//On construit alors les tableau chemin et tempsParcours caracterisant l'itineraire
				if(lignes.size()>1){ //Si le noeud de depart n'est pas le noeud d'arrivee
					for (int i = lignes.size()-2; i>=0; i--){
						//On ajoute chaque noeud de l'itineraire
						chemin.add(noeuds[(int)mTemps.get(lignes.get(i))[1]-2]);
						//On ajoute les temps de parcours totaux pour aller à chaque noeud
						double T = mTemps.get(lignes.get(i+1))[(int)mTemps.get(lignes.get(i))[1]];
						tempsParcours.add(T);
					}
				}
				else{ //sinon 
					chemin.add(noeuds[(int)mTemps.get(0)[1]-2]); //On ajoute le noeud position du robot qui est aussi le noeud objectif
					tempsParcours.add(0.0);//On ajoute un temps de parcours nul puisque le robot y est deja
				}
					
				// On calcule le temps de parcours entre chaque noeuds de l'itineraire
				for (int i = tempsParcours.size()-1; i>=1; i--){
					tempsParcours.set(i, tempsParcours.get(i)-tempsParcours.get(i-1));
				}
				
				itineraire = new ItineraireRobot(chemin, tempsParcours);					
	
			break;
			

			case AETOILE:
					
				//On recherche l'indice du noeud de depart dans le tableau des noeuds
				int start1 = rechercheNoeud(nstart , noeuds); //l'indice du noeud de depart dans le tableau des noeuds
			
				if(start1 == noeuds.length){//si le depart n'est pas sur la carte
					LOGGER.severe("Depart absent de la carte");
					throw new PathNotFoundException("Depart absent de la carte");
				}
					
				ArrayList<double[]> mDist = new ArrayList<double[]>(); //matrice de l'algorithme
				int iNoeudCourant1 = 2+start1; //Index du noeud courant (en colonne donc +2 par rapport au tableau de noeud original) dans la boucle de l'algorithme
				int futurnoeud1 = iNoeudCourant1; //index du futur noeud (en colonne donc +2 par  rapport au tableau de noeud original)de l'itin�raire
					
				boolean continuer1 = true;
				
				//++++++++++++++++++++BOUCLE DE L'ALGORITHME++++++++++++++++++++
				
				while(continuer1 == true){
					
					//+++++ Processus de creation d'une nouvelle ligne de la matrice +++++//
								
					int ligneprecedente = chercherLigneNoeud(mDist, iNoeudCourant1);//On recherche l'indice de la ligne du noeud precedent
					
					int nouvelleligne = chercherLigneNoeud(mDist, futurnoeud1); //On recherche l'indice de la ligne du noeud precedent
								
					//On regarde si on a deja explore l'itineraire considere (si on a deja cree une ligne avec ce noeud)
					if (nouvelleligne == mDist.size()){ //si ce n'est pas le cas on cree une nouvelle ligne dans la matrice correspondant a ce noeud
		
						double[] dists = new double[noeuds.length+2]; // tableau ligne des temps de parcours du noeud courant vers chaque voisin
				
						for(int i = 0; i < dists.length; i++){//de base toutes les cases contiennent des zeros
							dists[i]=0;
						}
				
						dists[0] = ligneprecedente; //indique de quelle ligne et donc quel noeud a ete choisi
						dists[1]=futurnoeud1; //indice du noeud actuel (correspondant a la colonne (donc +2 par rapport au tableau noeud initial))
						
						ArrayList<int[]> tabvoisins = voisins(futurnoeud1 - 2,robot); //tableau des index (ref tableau noeud) des voisins du noeud courant
						
						for(int i = 0; i < tabvoisins.size(); i++){// Remplit la ligne des distances à vol d'oiseau des voisins du noeud courant avec l'objectif. Ces distances sont sommées avec les distances parcouru pour l'itineraire en cours (somme)
							
							Arc voloiseauibut = new Arc(carte.getNoeud(noeuds[tabvoisins.get(i)[0]]),carte.getNoeud(butId),TypeArc.NORMAL);
							Arc voloiseauinter = new Arc(carte.getNoeud(noeuds[tabvoisins.get(i)[0]]),carte.getNoeud(noeuds[iNoeudCourant1-2]),TypeArc.NORMAL);
							Arc voloiseauprebut = new Arc(carte.getNoeud(noeuds[iNoeudCourant1-2]),carte.getNoeud(butId),TypeArc.NORMAL);
							
							dists[tabvoisins.get(i)[0]+2]=voloiseauibut.getLongueur()+voloiseauinter.getLongueur();
							
							if (mDist.size()>0){
								dists[tabvoisins.get(i)[0]+2]=dists[tabvoisins.get(i)[0]+2]+mDist.get((int)dists[0])[(int)dists[1]]-voloiseauprebut.getLongueur();
							}
						}
						
						mDist.add(dists);//Ajout de la nouvelle ligne dans la matrice de l'algorithme			
					}
							
					else{//si c'est le cas on change juste la ligne de provenance du futur noeud
						mDist.get(nouvelleligne)[0]=ligneprecedente;
					}					
				
					//++++++ Selection du prochain noeud +++++
					
					iNoeudCourant1 = futurnoeud1; //Le noeud courant devient le futur noeud
	
					if (noeuds[iNoeudCourant1-2] == butId){//si le NoeudCourant est le noeud objectif, on sort de la boucle de l'algorithme
						continuer1 = false;
					}
					
					else{//sinon on choisit le prochain noeud le plus interessant
						double[] next = new double[3];
						
						next = choixDuProchainNoeud(mDist , noeuds); // [0] le noeud courant, [1] le futur noeud et [2] le temps correspondant au couple choisi et -1 si tout a ete explore
		
						if(next[2] == -1){//si tous les itineraires possibles ont ete explores en vain
							LOGGER.severe("Le robot est isole de l'objectif");
							throw new PathNotFoundException("Le robot est isole de l'objectif");
						}
						else{ // sinon on assigne les nouveaux noeuds utilises
							iNoeudCourant1 = (int) next[0];
							futurnoeud1 = (int) next[1];
							
						}
						
					}		
					
				}
							
				//++++++++++++++++++++RECUPERATION DE L'ITINERAIRE++++++++++++++++++++
	
				//On fabrique les elements constituants l'itineraire
				ArrayList<Long> chemin1 = new ArrayList<Long>();
				ArrayList<Double> tempsParcours1 = new ArrayList<Double>();
				
				//On construit un tableau ordonne des lignes de la matrice de l'algo constituants l'itineraire
				ArrayList<Integer> lignes1 = new ArrayList<Integer>(); // attention, pour construire l'itineraire on doit "remonter" dans mDist
				
				lignes1.add(mDist.size()-1); // on ajoute la derniere ligne, celle du noeud objectif
				while (lignes1.get(lignes1.size()-1)!=0){// tant que l'on ne retombe pas sur la ligne de depart
					lignes1.add((int)mDist.get(lignes1.get(lignes1.size()-1))[0]);
				}
				
				//On construit alors les tableau chemin et tempsParcours caracterisant l'itineraire
				if(lignes1.size()>1){ //Si le noeud de depart n'est pas le noeud d'arrivee
					//On ajoute le premier noeud de l'itineraire
					chemin1.add(noeuds[(int)mDist.get(lignes1.get(lignes1.size()-2))[1]-2]);
					//On ajoute le temps de parcours du depart vers le premier noeud de l'itineraire
					//On recherche l'arc constitue par le depart et le premier noeud de l'itineraire
					Arc arcDepartItineraire = chercherArc(arcs, noeuds[(int)mDist.get(0)[1]-2], noeuds[(int)mDist.get(lignes1.get(lignes1.size()-2))[1]-2]);
					tempsParcours1.add(calculTempsParcours(arcDepartItineraire,robot));
					
					//On ajoute chaque noeud de l'itineraire
					//On calcule et ajoute les temps de parcours entre chaque noeuds de l'itineraire
					for (int i = lignes1.size()-3; i>=0; i--){
						chemin1.add(noeuds[(int)mDist.get(lignes1.get(i))[1]-2]);
						//On calcule le temps pour aller de la ligne i+1 a la ligne i
						//On recherche l'arc constitue par les deux noeuds consideres
						Arc arcCourantItineraire = chercherArc(arcs, noeuds[(int)mDist.get(lignes1.get(i))[1]-2], noeuds[(int)mDist.get(lignes1.get(i+1))[1]-2]);
						tempsParcours1.add(calculTempsParcours(arcCourantItineraire,robot));
					}
				}
				else{ //sinon 
					chemin1.add(noeuds[(int)mDist.get(0)[1]-2]); //On ajoute le noeud position du robot qui est aussi le noeud objectif
					tempsParcours1.add(0.0);//On ajoute un temps de parcours nul puisque le robot y est deja
				}
				itineraire = new ItineraireRobot(chemin1, tempsParcours1);
				
			break;
				
			}
		}
		
		
		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		LOGGER.fine("Time of Pthfinder: " + duration);
		
		System.out.println("Le robot " + robot.getId() + " a trouvé son chemin :");
		System.out.println(itineraire.toString());
		
		return itineraire;
	}
	
	/**
	 * Trouve l'indice d'un noeud dans le tableau des noeuds à partir de son iD
	 * @param idNoeud l'iD du noeud recherché
	 * @param noeuds le tableau de noeuds
	 * @return int l'indice du noeud dans le tableau
	 */
	private static int rechercheNoeud(long idNoeud, long[] noeuds){
		int it = 0; //l'indice du noeud dans le tableau des noeuds
		boolean cont = true;
		while(cont == true){
			if (it == noeuds.length){
				cont= false;
			}
			else{ 
				if( noeuds[it] == idNoeud ){
					cont = false;
				}
				else {
					it ++;
				}
			}
		}
		return it;
	}
	
	/**
	 * Recherche la ligne dans la matrice de l'algorithme correspondant à l'indice d'un noeud.
	 * @param mAlg la matrice de l'algorithme
	 * @param noeud l'indice du noeud à trouver (indice de colonne donc +2 par rapport au tableau de noeud)
	 * @return l'indice de la ligne correpondante ou le nombre de lignes de mAlg si le noeud est non trouvé
	 */
	private static int chercherLigneNoeud(ArrayList<double[]> mAlg, int noeud){
		boolean continuerRecherche = true;
		int k = 0;	
		while(continuerRecherche == true){ //on regarde si on a deja explore l'itineraire considere (si on a deja cree une ligne avec ce noeud)
			if(k==mAlg.size()){
				continuerRecherche = false;
			} else {
				if(mAlg.get(k)[1]==noeud) {
					continuerRecherche = false;
				} else {
					k++;
				}
			}
		}
		return k;
	}

	/**
	 * Recherche l'arc reliant deux noeuds
	 * @param arcs le tableau des arcs de la carte
	 * @param noeud1 l'id d'un noeud extremite de l'arc recherche
	 * @param noeud2 l'id de l'autre noeud extremite de l'arc recherche
	 * @return l arc correspondant
	 * @throws PathNotFoundException 
	 */
	private static Arc chercherArc(Arc[] arcs, long noeud1, long noeud2) throws PathNotFoundException{
		Arc arc = null;//initialisation arbitraire
		int m = 0;
		boolean continueRecherche = true;
		while(continueRecherche){
			if (m<arcs.length){
				if((arcs[m].getNoeuds()[0].getId()==noeud1)&&(arcs[m].getNoeuds()[1].getId()==noeud2)){
					arc = arcs[m];
					continueRecherche = false;
				} else {
					if((arcs[m].getNoeuds()[1].getId()==noeud1)&&(arcs[m].getNoeuds()[0].getId()==noeud2)){
						arc = arcs[m]; 
						continueRecherche = false;
					} else {
						m++;
					}
				}
			} else {
				continueRecherche = false;
			}
		}
		if (m==arcs.length){
			LOGGER.severe("Arc introuvable");
			throw new PathNotFoundException("Arc introuvable");
		}
		return arc;
	}	
	
	
	/**
	 * construit le tableau des id des noeuds voisins d'un noeud et reference les arcs correspondant
	 * @param iNoeud l'index (ref du tableau noeud) du noeud dont on veut connaitre les voisins
	 * @param robot le robot considéré, possédant la carte
	 * @return ArrayList<long[2]> avec les id des noeuds voisins dans la premiere colonne et la ligne correspondant a l'arc dans le tableau d'arcs
	 */
	private static ArrayList<int[]> voisins(int iNoeud, Robot robot){
		
		Carte carte = robot.getCarte();
		Arc[] arcs = carte.getArcs();
		long[] noeuds = carte.getIdNoeuds();
		
		ArrayList<int[]> voisins = new ArrayList<int[]>(); //matrice de largeur 2 des voisins et des arcs correspondants
		
		long iDNoeud = noeuds[iNoeud];
		int j = 0;

		for (int i = 0; i < arcs.length; i++) { // on parcourt le tableau d'arcs
			int[] indexs = new int[2]; // tableau réunissant l'indice du voisin et de l'arc correspondant
			
				if(arcs[i].getNoeuds()[0].getId() == iDNoeud) {  // on teste si le noeud considere est a une extremite de l'arc courant
					indexs[1] = i;  // on garde la position de l'arc dans le tableau d'arcs
				
					j = 0;
					while ((noeuds[j] != arcs[i].getNoeuds()[1].getId())&&(j<noeuds.length)) {  // on trouve la position du voisin dans le tableau de noeuds
						j++;
					}
					indexs[0] = j;  // on garde la position du noeud dans le tableau des noeuds
					voisins.add(indexs);
				}
					
				if(arcs[i].getNoeuds()[1].getId() == iDNoeud) { // on teste si le noeud considere est a l'autre extremite de l'arc courant
					indexs[1] = i;  // on garde la position de l'arc dans le tableau d'arcs
					
					j = 0;
					while ((noeuds[j] != arcs[i].getNoeuds()[0].getId())&&(j<noeuds.length)) {
						j++;
					}				
					indexs[0] = j;  // on garde la position du noeud dans le tableau des noeuds
					voisins.add(indexs);			
				}
		}
		return voisins;
	}
	
	/**
	 * Calcule le temps de parcours d'un arc correspondant au robot considéré
	 * @param arc l'arc dont on veut connaître le temps de parcours
	 * @param robot le robot considéré
	 * @return le temps de parcours du noeud considéré en secondes
	 */
	private static double calculTempsParcours(Arc arc, Robot robot){
			double longueur = arc.getLongueur();
			double temps = 3600*(longueur/(robot.getVitesse(arc.getType())));
			return temps;
	}
	
	/**
	 * Selectionne le prochain noeud a explorer
	 * @param mAlg la matrice generale de l'algorithme
	 * @param noeuds le tableau des noeuds de la carte
	 * @return double[3] avec [0] iNoeudCourant, [1] futurnoeud, [2] le minimum de temps choisi ou -1 si tout a ete explore
	 */
	private static double[] choixDuProchainNoeud(ArrayList<double[]> mAlg , long[] noeuds ){
		double[] results = new double[3];
		results[2] = -1; //variable temporaire stockant le minimum des valeurs stockées dans la matrice
		//vaut -1 tant que l'on a pas trouvé de noeud inexplore
		
		for (double[] index : mAlg){  //on teste toutes les lignes
			for(int i = 2; i < noeuds.length+2; i++) {  //on teste toutes les cases de chaque ligne
				if(index[i]>0) { //si il y a un zero, on en vient ou ce n'est pas un voisin
					if((index[i]<results[2])||(results[2] ==-1)) {//si l'on trouve une valeur inferieure a la valeur de la variable temporaire ou que l'on a pas encore trouve de noeud inexplore
						int l = chercherLigneNoeud(mAlg, i);//on regarde si on a deja explore l'itineraire considere
						if (l == mAlg.size()) { //si ce n'est pas le cas on change le minimum et on change l'index du futur noeud.
							results[2] = index[i];
							results[1] = i; //futurnoeud
							results[0] = index[1]; //iNoeudCourant
						}
					}
				}
			}
		}
	return results;	
	}
}
