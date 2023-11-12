import java.util.*;

public class pop {

    public static void main(String[] args) {
        // Scanner pour obtenir les entrées utilisateur
        Scanner scan = new Scanner(System.in);

        // Création des sommets du graphe
        Sommet nodeA = new Sommet("A");
        Sommet nodeB = new Sommet("B");
        Sommet nodeC = new Sommet("C");
        Sommet nodeD = new Sommet("D");

        // Création des arêtes du graphe avec les poids associés
        Arete areteAB = new Arete(nodeA, nodeB, 2);
        Arete areteAC = new Arete(nodeA, nodeC, 3);
        Arete areteBD = new Arete(nodeB, nodeD, 8);
        Arete areteCD = new Arete(nodeC, nodeD, 6);

        // Ajout des arêtes aux sommets correspondants
        nodeA.addArete(areteAB);
        nodeA.addArete(areteAC);
        nodeB.addArete(areteBD);
        nodeC.addArete(areteCD);

        // Création du graphe avec les sommets et les arêtes
        Graph graph = new Graph(new ArrayList<>(Arrays.asList(nodeA, nodeB, nodeC, nodeD)),
                new ArrayList<>(Arrays.asList(areteAB, areteAC, areteBD, areteCD)));

        // Affichage des informations sur le graphe
        System.out.println("Voici le graphe : ");
        graph.displayGrapInfo();

        // Sélection du sommet de départ
        System.out.println("Choisissez le sommet de départ :");
        Sommet depart = findSommetByName(graph.getSommets(), scan.nextLine());

        // Sélection du sommet d'arrivée
        System.out.println("Choisissez le sommet d’arrivée :");
        Sommet cible = findSommetByName(graph.getSommets(), scan.nextLine());

        // Exécution de l'algorithme de Dijkstra
        if (depart != null && cible != null) {
            runDijkstra(graph, depart, cible);
        } else {
            System.out.println("L’un des sommets spécifiés n’existe pas.");
        }
    }

    // Recherche d'un sommet par son nom dans la liste des sommets
    private static Sommet findSommetByName(List<Sommet> sommets, String nom) {
        for (int i = 0; i < sommets.size(); i++) {
            if (sommets.get(i).getNom().equalsIgnoreCase(nom)) {
                return sommets.get(i);
            }
        }
        return null;
    }

    // Exécution de l'algorithme de Dijkstra sur le graphe
    private static void runDijkstra(Graph graph, Sommet depart, Sommet cible) {
    	
         Map<Sommet, Double> distances = new HashMap<>();
         Map<Sommet, Sommet> predecessors = new HashMap<>();
         Set<Sommet> StarterNodes = new HashSet<>(graph.getSommets());
         Set<Sommet> FinalNodes = new HashSet<>();

        // Initialisation des distances avec l'infini
        for (Sommet sommet : graph.getSommets()) {
            distances.put(sommet, Double.POSITIVE_INFINITY);
        }

        // La distance du sommet de départ à lui-même est 0
        distances.put(depart, 0.0);

        // Boucle principale de l'algorithme de Dijkstra
        while (!StarterNodes.isEmpty()) {
            Sommet current = getLowestDistanceNode(StarterNodes, distances);
            StarterNodes.remove(current);
            FinalNodes.add(current);

         // Mise à jour des distances et prédécesseurs pour les sommets adjacents
            for (Arete arete : current.getAretes()) {
                Sommet adjacent;
                if (arete.getDestination().equals(current)) {
                    adjacent = arete.getSource();
                } else {
                    adjacent = arete.getDestination();
                }

                // Si le sommet adjacent est déjà dans FinalNodes, on passe au suivant
                if (FinalNodes.contains(adjacent)) {
                    continue;
                }

                // Poids de l'arête entre le sommet actuel et le sommet adjacent
                double PoidsAretes = arete.getPoids();

                // Si la distance actuelle plus le poids de l'arête est inférieure à la distance enregistrée pour le sommet adjacent
                if (distances.get(current) + PoidsAretes < distances.get(adjacent)) {
                    // Mettre à jour la distance pour le sommet adjacent
                    distances.put(adjacent, distances.get(current) + PoidsAretes);
                    
                    // Mettre à jour le prédécesseur du sommet adjacent
                    predecessors.put(adjacent, current);
                }
            }

        }

        // Affichage du chemin le plus court
        printShortestPath(cible, predecessors, distances);
    }

    // Recherche du sommet avec la distance la plus basse parmi les sommets non résolus
    private static Sommet getLowestDistanceNode(Set<Sommet> StarterNodes, Map<Sommet, Double> distances) {
        Sommet lowestDistanceNode = null;
        double lowestDistance = Double.POSITIVE_INFINITY;
        for (Sommet node : StarterNodes) {
            double nodeDistance = distances.get(node);
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private static void printShortestPath(Sommet target, Map<Sommet, Sommet> predecessors, Map<Sommet, Double> distances) {
        LinkedList<Sommet> path = new LinkedList<>();
        Sommet current = target;
        
        // Vérification de l’existence d’un chemin
        if (predecessors.get(current) == null && !current.equals(target)) {
            System.out.println("Aucun chemin trouvé de " + target.getNom());
            return;
        }
        
        // Construction du chemin à partir des prédécesseurs
        path.add(current); // Add the target node to the path
        while (predecessors.containsKey(current)) {
            current = predecessors.get(current);
            path.addFirst(current); // addFirst to reverse the path order
        }
        
        // Affichage du chemin en sens inverse (du sommet de départ au sommet cible)
        Iterator<Sommet> iterator = path.iterator();
        if (iterator.hasNext()) {
            Sommet start = iterator.next();
            System.out.print("Chemin le plus court : " + start.getNom());
            while (iterator.hasNext()) {
                System.out.println(" -> " + iterator.next().getNom());
            }
        }
        System.out.println("Distance totale : " + distances.get(target));
    }
}


// Classe représentant un sommet dans le graphe
class Sommet {
    private String nom;
    private List<Arete> aretes;

    // Constructeur avec initialisation du nom et de la liste des arêtes
    Sommet(String nom) {
        this.nom = nom;
        this.aretes = new ArrayList<>();
    }

    // Accesseur pour obtenir le nom du sommet
    public String getNom() {
        return nom;
    }

    // Accesseur pour obtenir la liste des arêtes du sommet
    public List<Arete> getAretes() {
        return aretes;
    }

    // Méthode pour ajouter une arête au sommet et créer une arête inverse si elle n'existe pas déjà
    public void addArete(Arete arete) {
        aretes.add(arete);

        Sommet destination = arete.getDestination();
        boolean reverseEdgeExists = false;

        // Vérification de l'existence de l'arête inverse
        for (Arete existingArete : destination.aretes) {
            if (existingArete.getDestination().equals(this)) {
                reverseEdgeExists = true;
                break;
            }
        }

        // Ajout de l'arête inverse si elle n'existe pas
        if (!reverseEdgeExists) {
            Arete reverseArete = new Arete(destination, this, arete.getPoids());
            destination.aretes.add(reverseArete);
        }
    }
}

// Classe représentant une arête entre deux sommets
class Arete {
    public Sommet source;
    public Sommet destination;
    public int poids;

    // Constructeur avec initialisation de la source, de la destination et du poids
    Arete(Sommet source, Sommet destination, int poids) {
        this.source = source;
        this.destination = destination;
        this.poids = poids;
    }

    // Accesseur pour obtenir la source de l'arête
    public Sommet getSource() {
        return source;
    }

    // Accesseur pour obtenir la destination de l'arête
    public Sommet getDestination() {
        return destination;
    }

    // Accesseur pour obtenir le poids de l'arête
    public int getPoids() {
        return poids;
    }
}

// Classe représentant le graphe composé de sommets et d'arêtes
class Graph {
    public List<Sommet> sommets;
    public List<Arete> aretes;

    // Constructeur avec initialisation des sommets et des arêtes
    Graph(List<Sommet> sommets, List<Arete> aretes) {
        this.sommets = sommets;
        this.aretes = aretes;
    }

    // Accesseur pour obtenir la liste des sommets
    public List<Sommet> getSommets() {
        return sommets;
    }

    // Affichage des informations sur le graphe
    public void displayGrapInfo() {
        System.out.println("-----------------------------");
        System.out.println("Nombre de sommets: " + sommets.size());
        System.out.println("Sommets : ");
        for (Sommet sommet : sommets) {
            System.out.println("- " + sommet.getNom());
        }
        System.out.println("Arêtes:");
        for (Arete arete : aretes) {
            System.out.println("- De: " + arete.getSource().getNom() + " à " + arete.getDestination().getNom() + ", poids: " + arete.getPoids());
        }
        System.out.println("-----------------------------");
    }
}
