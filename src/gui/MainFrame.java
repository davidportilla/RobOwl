package gui;

import io.AccessFichiers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import pathfinder.PathFinder.Algorithme;
import simulation.Manager;
import simulation.Robot;
import simulation.RobotChenilles;
import simulation.RobotDrone;
import simulation.RobotPattes;
import simulation.RobotRoues;
import carte.Arc;
import carte.Carte;
import carte.CarteSynchronized;
import carte.Noeud;

/**
 * Fenetre principal du logiciel. Cette classe controle la simulation en
 * implementant les listeners et en observant la carte sur laquelle la simulation se
 * deroule.
 * 
 * @author Maguelone Brac
 * @author Clement Delord
 * @author Thomas Fernandez
 * @author Clara Maurel
 * @author David Portilla Abellan
 * @author Patricia Ventura Diaz
 * @version 21-01-2013
 */
public class MainFrame extends JFrame implements ActionListener, MouseListener,
		MouseMotionListener, ChangeListener, Observer {

	private static final long serialVersionUID = -7121239301015888529L;

	private boolean listenersActive;

	private CarteSynchronized carte;
	private Manager manager;

	private JPanel contentPane;
	private final ImageIcon playIcon = new ImageIcon("images/play.png");
	private final ImageIcon pauseIcon = new ImageIcon("images/pause.png");
	private final ImageIcon stopIcon = new ImageIcon("images/stop.png");
	private final JButton btnPlay = new JButton(playIcon);
	private final JButton btnPause = new JButton(pauseIcon);
	private final JButton btnStop = new JButton(stopIcon);
	private final JLabel positionLabel = new JLabel(
			"RobOwl Multithreading. All rigths reserved.");
	private JCartePanel cartePanel = new JCartePanel();
	private final JPopupMenu popupMenu = new JPopupMenu();
	private final JMenu mnAugmenterFeu = new JMenu("Creer/Augmenter un feu");
	private final JMenuItem mntmAugmenterFeu1 = new JMenuItem("Intensite +1");
	private final JMenuItem mntmAugmenterFeu2 = new JMenuItem("Intensite +2");
	private final JMenuItem mntmAugmenterFeu3 = new JMenuItem("Intensite +3");
	private final JMenuItem mntmAugmenterFeu4 = new JMenuItem("Intensite +4");
	private final JMenuItem mntmAugmenterFeu5 = new JMenuItem("Intensite +5");
	private final JMenu mnDiminuerFeu = new JMenu("Diminuer/Eteindre un feu");
	private final JMenuItem mntmDiminuerFeu1 = new JMenuItem("Intensite -1");
	private final JMenuItem mntmDiminuerFeu2 = new JMenuItem("Intensite -2");
	private final JMenuItem mntmDiminuerFeu3 = new JMenuItem("Intensite -3");
	private final JMenuItem mntmDiminuerFeu4 = new JMenuItem("Intensite -4");
	private final JMenuItem mntmDiminuerFeu5 = new JMenuItem("Intensite -5");
	private final JMenuItem mntmNewSource = new JMenuItem("Nouvelle source");
	private final JMenu mnNewRobot = new JMenu("Nouveau robot");
	private final JMenuItem mntmNewRobotRoues = new JMenuItem("Robot roues");
	private final JMenuItem mntmNewRobotChenilles = new JMenuItem(
			"Robot chenilles");
	private final JMenuItem mntmNewRobotPattes = new JMenuItem("Robot pattes");
	private final JMenuItem mntmNewRobotDrone = new JMenuItem("Robot drone");
	private final JSlider slider = new JSlider();

	private final JLabel infos = new JLabel("<html><b>Informations</b><br><br>"
			+ "- Nombre de robots:<br><br>" + "- Nombre de sources:<br><br>"
			+ "- Nombre de feux:<br></html>");

	private long noeudIDclicked;

	private static int echeleTemporel = 50;
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu mnFile = new JMenu("File");
	private final JMenu mnOptions = new JMenu("Options");
	private final JMenu mnAbout = new JMenu("About");
	private final JMenu mnHelp = new JMenu("Help");
	private final JMenuItem mntmOpenMap = new JMenuItem("Open map");
	private final JMenuItem mntmOpenSimulation = new JMenuItem(
			"Open simulation");
	private final JMenuItem mntmSaveSimulation = new JMenuItem(
			"Save simulation");
	private final JMenu mnAlgorithm = new JMenu("Algorithm");
	private final JMenuItem mntmDjikstra = new JMenuItem("Djikstra");
	private final JMenuItem mntmA = new JMenuItem("A*");
	private final JMenuItem mntmRobowl = new JMenuItem("RobOwl");
	private final JMenuItem mntmAuthors = new JMenuItem("Authors");
	private final JMenuItem mntmHowDoesIt = new JMenuItem("How does it work");
	
	private final JPopupMenu popupRobowl = new JPopupMenu();
	private final JPopupMenu popupAuthors = new JPopupMenu();
	private final JPopupMenu popupHowDoesIt = new JPopupMenu();

	private final JLabel legende = new JLabel(new ImageIcon("images/rect2985.png"));

	/**
	 * 
	 * @return echele temporel pour changer la vitesse de la simulation.
	 */
	public static int getEcheleTemporel() {
		return echeleTemporel;
	}

	private static Algorithme algorithme = Algorithme.DIJKSTRA;

	private final JFileChooser fc = new JFileChooser();

	/**
	 * @return l'algorithme.
	 */
	public static Algorithme getAlgorithme() {
		return algorithme;
	}

	/**
	 * @param algorithme
	 *            l'algorithme a modifier.
	 */
	public static void setAlgorithme(Algorithme algorithme) {
		MainFrame.algorithme = algorithme;
	}

	/**
	 * Cree l image.
	 */
	public MainFrame(Manager manager) {
		this.carte = manager.getCarteSynchronized();
		this.carte.addObserver(this);
		this.manager = manager;
		//cartePanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
		//		null, null));
		this.cartePanel.init(carte, manager);
		cartePanel.setPreferredSize(new Dimension(
				cartePanel.getAdjustedWidth(), cartePanel.getAdjustedHeight()));
		cartePanel.setSize(new Dimension(cartePanel.getAdjustedWidth(),
				cartePanel.getAdjustedHeight()));

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		btnPlay.setBorder(null);
		btnPause.setBorder(null);
		btnStop.setBorder(null);
		
		menuBar.add(mnFile);
		mnFile.add(mntmOpenMap);
		mnFile.add(mntmOpenSimulation);
		mnFile.add(mntmSaveSimulation);
		setJMenuBar(menuBar);
		menuBar.add(mnOptions);
		mnOptions.add(mnAlgorithm);
		mnAlgorithm.add(mntmDjikstra);
		mnAlgorithm.add(mntmA);
		menuBar.add(mnHelp);
		mnHelp.add(mntmHowDoesIt);
		menuBar.add(mnAbout);
		mnAbout.add(mntmRobowl);
		mnAbout.add(mntmAuthors);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("",
				"[823.00px][823.00px][823.00px][823.00px][94.00]",
				"[633px][633px,grow][][29px][16px]"));
		contentPane.add(this.cartePanel,
				"cell 0 0 4 2,alignx center,aligny center");

		cartePanel.add(popupMenu);

		cartePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnPlay.setAlignmentX(Component.CENTER_ALIGNMENT);

		// LISTENERS BARMENU
		mntmOpenMap.addActionListener(this);
		mntmOpenSimulation.addActionListener(this);
		mntmSaveSimulation.addActionListener(this);
		mnAlgorithm.addActionListener(this);
		mntmDjikstra.addActionListener(this);
		mntmA.addActionListener(this);
		mntmRobowl.addActionListener(this);
		mntmAuthors.addActionListener(this);
		mntmHowDoesIt.addActionListener(this);

		// LISTENERS MENU CARTE
		mntmAugmenterFeu1.addActionListener(this);
		mntmAugmenterFeu2.addActionListener(this);
		mntmAugmenterFeu3.addActionListener(this);
		mntmAugmenterFeu4.addActionListener(this);
		mntmAugmenterFeu5.addActionListener(this);
		mntmDiminuerFeu1.addActionListener(this);
		mntmDiminuerFeu2.addActionListener(this);
		mntmDiminuerFeu3.addActionListener(this);
		mntmDiminuerFeu4.addActionListener(this);
		mntmDiminuerFeu5.addActionListener(this);
		mntmNewSource.addActionListener(this);
		mntmNewRobotRoues.addActionListener(this);
		mntmNewRobotChenilles.addActionListener(this);
		mntmNewRobotPattes.addActionListener(this);
		mntmNewRobotDrone.addActionListener(this);

		popupMenu.add(mnAugmenterFeu);
		popupMenu.add(mnDiminuerFeu);
		popupMenu.add(mntmNewSource);
		popupMenu.add(mnNewRobot);

		mnAugmenterFeu.add(mntmAugmenterFeu1);
		mnAugmenterFeu.add(mntmAugmenterFeu2);
		mnAugmenterFeu.add(mntmAugmenterFeu3);
		mnAugmenterFeu.add(mntmAugmenterFeu4);
		mnAugmenterFeu.add(mntmAugmenterFeu5);

		mnDiminuerFeu.add(mntmDiminuerFeu1);
		mnDiminuerFeu.add(mntmDiminuerFeu2);
		mnDiminuerFeu.add(mntmDiminuerFeu3);
		mnDiminuerFeu.add(mntmDiminuerFeu4);
		mnDiminuerFeu.add(mntmDiminuerFeu5);

		mnNewRobot.add(mntmNewRobotRoues);
		mnNewRobot.add(mntmNewRobotChenilles);
		mnNewRobot.add(mntmNewRobotPattes);
		mnNewRobot.add(mntmNewRobotDrone);

		btnPlay.addActionListener(this);
		btnPause.addActionListener(this);
		btnStop.addActionListener(this);

		GridBagConstraints gbc_popupMenu = new GridBagConstraints();
		gbc_popupMenu.gridx = 1;
		gbc_popupMenu.gridy = 0;

		slider.addChangeListener(this);
		
        legende.setBackground(contentPane.getBackground());
        contentPane.add(legende,"cell 4 1,grow");
		
		contentPane.add(slider, "cell 1 2 2 1,growx");
		contentPane.add(btnPlay,
				"flowx,cell 0 3 4 1,alignx center,aligny baseline");
		

		this.cartePanel.addMouseListener(this);
		this.cartePanel.addMouseMotionListener(this);
		contentPane.add(positionLabel, "cell 0 4 5 1,growx,aligny center");

		infos.setMinimumSize(new Dimension(200, 50));
		Font font = new Font("Calibri", Font.BOLD, 16);

		infos.setFont(font);
		
		contentPane.add(infos, "cell 4 0");

		contentPane.add(btnPause, "cell 0 3 4 1");

		contentPane.add(btnStop, "cell 0 3 4 1");

		this.setBounds(50, 20, 950, 1010);
		//this.setBounds(100, 100, 880, 710);

		this.listenersActive = true;
	}

	// ----- CONTROLLER -----

	@Override
	public void actionPerformed(ActionEvent e) {
		if (this.listenersActive) {
			if (e.getSource() == this.mntmOpenMap) {
				int returnVal = fc.showOpenDialog(this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					// This is where a real application would open the file.
					try {
						// disable listeners
						this.listenersActive = false;
						Carte carte = AccessFichiers.readMap(file);
						HashMap<Long, Noeud> noeudsMap = carte.getNoeudsMap();
						HashMap<Long, Arc> arcsMap = carte.getArcsMap();
						this.carte = new CarteSynchronized(noeudsMap, arcsMap);
						this.manager = new Manager(this.carte,
								new ArrayList<Robot>());
						this.carte.addObserver(this);
						this.cartePanel.init(this.carte, this.manager);
						this.btnPlay.setEnabled(true);
						this.btnPause.setEnabled(false);
						this.listenersActive = true;
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			} else if (e.getSource() == this.mntmOpenSimulation) {
				int returnVal = fc.showOpenDialog(this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					// This is where a real application would open the file.
					try {
						this.listenersActive = false;
						this.btnPlay.setEnabled(true);
						this.btnPause.setEnabled(true);
						this.carte.reset();
						Carte carte = AccessFichiers.readSimulationCarte(file,
								this.carte);
						HashMap<Long, Noeud> noeudsMap = carte.getNoeudsMap();
						HashMap<Long, Arc> arcsMap = carte.getArcsMap();
						this.carte = new CarteSynchronized(noeudsMap, arcsMap);
						ArrayList<Robot> robots = AccessFichiers
								.readSimulationRobots(file, this.carte);
						this.manager = new Manager(this.carte, robots);
						this.carte.addObserver(this);
						this.cartePanel.init(this.carte, this.manager);
						this.cartePanel.repaint();
						this.listenersActive = true;
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					System.out.println("Load error");
				}
			} else if (e.getSource() == this.mntmSaveSimulation) {
				int returnVal = fc.showSaveDialog(this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
						AccessFichiers.writeSimulation(file, this.carte,
								this.manager);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					System.out.println("Save error");
				}
			} else if (e.getSource() == this.mntmDjikstra) {
				setAlgorithme(Algorithme.DIJKSTRA);
			} else if (e.getSource() == this.mntmA) {
				setAlgorithme(Algorithme.AETOILE);
			} else if (e.getSource() == this.mntmRobowl) {
				this.popupRobowl.setVisible(true);
				JFrame frame = new JFrame("Robowl info");
			    JTextArea textArea = new JTextArea(5, 40);
		        textArea.setEditable(false);
				
		        String text = "";
		        try{text = AccessFichiers.readTextRobowl(new File("resources/textRobowl.txt"));
		        }
		        catch (IOException e1){
		        	System.out.println("Il n'y a pas de fichier d'info Robowl");
		        }
				textArea.setText(text);
		        textArea.setCaretPosition(textArea.getDocument().getLength());
		    
		        Font font = new Font("Calibri", Font.PLAIN, 15);
		        textArea.setFont(font);
		        textArea.setForeground(Color.BLACK);
		        
		        JMenuItem imRobot = new JMenuItem("Robot en eteindant un feu",
		        		new ImageIcon("images/robot1.png"));
		        imRobot.setBackground(Color.WHITE);
		        
		        frame.add(imRobot);
		        frame.add(textArea);
		        
		        imRobot.setBounds(300,520,400,400);
		        
				frame.setSize(900,200);
				frame.setBounds(150,150,810,940);
				frame.setVisible(true);
				textArea.setVisible(true);
		        imRobot.setVisible(true);

				
				
			} else if (e.getSource() == this.mntmAuthors) {
				this.popupAuthors.setVisible(true);
				JFrame frame = new JFrame("Authors");
			    JTextArea textArea = new JTextArea(5, 40);
		        textArea.setEditable(false);
				
				String text1 = "\n   AUTHORS:\n\n";
				String text2 = " Maguelone Brac \n"
						 + " Clement Delord \n"
						 + " Thomas Fernandez \n"
						 + " Clara Maurel \n"
						 + " David Portilla Abellan \n"
						 + " Patricia Ventura Diaz \n";
		       
				textArea.setText(text1+text2);
		        
		        Font font = new Font("Calibri", Font.BOLD, 17);
		        textArea.setFont(font);
		        textArea.setForeground(Color.blue);
		        JMenuItem imPeople = new JMenuItem("",
		        		new ImageIcon("images/logo-isae.PNG"));
		        imPeople.setBackground(Color.WHITE);
		        imPeople.setSize(120,120);
		        
		        frame.add(imPeople);
		        frame.add(textArea);
		        
		        imPeople.setBounds(160,20,500,270);
		        imPeople.setVisible(true);
		        
		        
				frame.setBounds(150,150,580,350);
				frame.setVisible(true);
				textArea.setVisible(true);
				
			} else if (e.getSource() == this.mntmHowDoesIt) {
				this.popupHowDoesIt.setVisible(true);
				JFrame frame = new JFrame("RobOwl - Multithreading");
			    JTextArea textArea = new JTextArea();
			    JScrollPane scrollPane = new JScrollPane(textArea);
			    
		        textArea.setEditable(false);
				
		        String text = "";
		        try{text = AccessFichiers.readTextRobowl(new File("README.txt"));
		        }
		        catch (IOException e1){
		        	System.out.println("Il n'y a pas de fichier d'information");
		        }
				textArea.setText(text);
		        textArea.setCaretPosition(textArea.getDocument().getLength());
		    
		        Font font = new Font("Calibri", Font.PLAIN, 15);
		        textArea.setFont(font);
		        textArea.setForeground(Color.BLACK);
		        
		        frame.add(scrollPane);   
		        
				frame.setSize(900,200);
				frame.setBounds(150,150,810,940);
				frame.setVisible(true);
		        
				scrollPane.setVisible(true);
				
			}

			if (e.getSource() == this.btnPlay) {
				if (!this.manager.isAlive()) {
					this.manager.start();
					this.carte.restart();
				} else {
					this.carte.restart();
				}
				this.btnPlay.setEnabled(false);
				this.btnPause.setEnabled(true);
			}

			if (e.getSource() == this.btnPause) {
				this.btnPlay.setEnabled(true);
				this.btnPause.setEnabled(false);
				this.carte.pause();
			}

			if (e.getSource() == this.btnStop) {
				this.btnPlay.setEnabled(true);
				this.btnPause.setEnabled(true);
				this.carte.reset();
				HashMap<Long, Noeud> noeudsMap = this.carte.getNoeudsMap();
				HashMap<Long, Arc> arcsMap = this.carte.getArcsMap();
				this.carte = new CarteSynchronized(noeudsMap, arcsMap);
				this.manager = new Manager(this.carte, new ArrayList<Robot>());
				this.carte.addObserver(this);
				this.cartePanel.init(this.carte, this.manager);
				this.cartePanel.repaint();
			}

			if (e.getSource() == this.mntmAugmenterFeu1) {
				this.carte.augmenterFeu(this.noeudIDclicked, 1);
			} else if (e.getSource() == this.mntmAugmenterFeu2) {
				this.carte.augmenterFeu(this.noeudIDclicked, 2);
			} else if (e.getSource() == this.mntmAugmenterFeu3) {
				this.carte.augmenterFeu(this.noeudIDclicked, 3);
			} else if (e.getSource() == this.mntmAugmenterFeu4) {
				this.carte.augmenterFeu(this.noeudIDclicked, 4);
			} else if (e.getSource() == this.mntmAugmenterFeu5) {
				this.carte.augmenterFeu(this.noeudIDclicked, 5);
			}

			if (e.getSource() == this.mntmDiminuerFeu1) {
				this.carte.reduireFeu(this.noeudIDclicked, 1);
			} else if (e.getSource() == this.mntmDiminuerFeu2) {
				this.carte.reduireFeu(this.noeudIDclicked, 2);
			} else if (e.getSource() == this.mntmDiminuerFeu3) {
				this.carte.reduireFeu(this.noeudIDclicked, 3);
			} else if (e.getSource() == this.mntmDiminuerFeu4) {
				this.carte.reduireFeu(this.noeudIDclicked, 4);
			} else if (e.getSource() == this.mntmDiminuerFeu5) {
				this.carte.reduireFeu(this.noeudIDclicked, 5);
			}

			if (e.getSource() == this.mntmNewSource) {
				this.carte.addSource(this.noeudIDclicked);
			}

			if (e.getSource() == this.mntmNewRobotRoues) {
				this.manager.addRobot(new RobotRoues(this.noeudIDclicked,
						this.carte));
			} else if (e.getSource() == this.mntmNewRobotChenilles) {
				this.manager.addRobot(new RobotChenilles(this.noeudIDclicked,
						this.carte));
			} else if (e.getSource() == this.mntmNewRobotPattes) {
				this.manager.addRobot(new RobotPattes(this.noeudIDclicked,
						this.carte));
			} else if (e.getSource() == this.mntmNewRobotDrone) {
				this.manager.addRobot(new RobotDrone(this.noeudIDclicked,
						this.carte));
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (this.listenersActive) {
			if (e.getSource() == this.cartePanel) {

				this.popupMenu.show(e.getComponent(), e.getX(), e.getY());

				this.positionLabel.setText("(" + e.getX() + "," + e.getY()
						+ ")");
				// chercher le noeud le plus proche
				Point2D point = new Point2D.Double(e.getX(), e.getY());
				point = this.cartePanel.getCarteGraph2D()
						.chercherLeNoeudLePlusProche(point);
				this.noeudIDclicked = this.cartePanel.getCarteGraph2D()
						.getIdByPoint(point);
				Noeud noeud = this.carte.getNoeud(this.noeudIDclicked);
				this.positionLabel.setText(positionLabel.getText() + " ---> "
						+ noeud.toString());

			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (this.listenersActive) {
			this.positionLabel.setText("(" + e.getX() + "," + e.getY() + ")");
			// chercher le noeud le plus proche
			Point2D point = new Point2D.Double(e.getX(), e.getY());
			point = this.cartePanel.getCarteGraph2D()
					.chercherLeNoeudLePlusProche(point);
			Long noeudActuel = this.cartePanel.getCarteGraph2D().getIdByPoint(
					point);
			Noeud noeud = this.carte.getNoeud(noeudActuel);
			this.positionLabel.setText(positionLabel.getText() + " ---> "
					+ noeud.toString());
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (this.listenersActive) {
			MainFrame.echeleTemporel = slider.getValue();
			// slider.getValue() de 0 a 100
		}
	}

	@Override
	public void update(Observable o, Object arg) {

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					infos.setText("<html><b>Informations</b><br><br>"
							+ "- Nombre de robots: "
							+ manager.getRobots().size() + "<br><br>"
							+ "- Nombre de sources: "
							+ carte.getSources().size() + "<br><br>"
							+ "- Nombre de feux: "
							+ carte.getIncendies().size() + "<br> </html>");

					cartePanel.repaint();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}
	
}
