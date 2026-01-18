import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class Noodle {
    HashMap<String, Diplome> diplomeHashMap = new HashMap<>();
    HashMap<String, Enseignant> enseignantsByNom = new HashMap<>();

    //HashMap<String, Enseignant> enseignantHashMap = new HashMap<>();

    public Noodle() {
        this.diplomeHashMap = new HashMap<>();
    }

    public void creerEnseignant(String prenom, String nom) {
        String id = prenom + " " + nom;

        if (enseignantsByNom.containsKey(id)) {
            throw new IllegalArgumentException(
                    "Enseignant déjà existant : " + id
            );
        }

        enseignantsByNom.put(id, new Enseignant(prenom, nom));
    }

    public Enseignant getEnseignant(String nom) {
        return enseignantsByNom.get(nom);
    }




    public HashMap<String, Diplome> getDiplomeHashMap() {
        for (String nom : diplomeHashMap.keySet()) {
            //System.out.println("Nom du diplôme : " + diplomeHashMap.Diplome);
        }
        return diplomeHashMap;
    }

    public void afficherDiplomes() {
        diplomeHashMap.forEach((nomDiplome, d) -> System.out.println(nomDiplome + " : " + d));
    }

    /*public HashMap<String, Enseignant> getEnseignantHashMap() {
        return enseignantHashMap;
    }*/

    public Diplome creerDiplome(String nomDiplome, TypeDiplome type, int annee, int maxEtu, int ects){
        // Verif si le diplome existe déjà
        if (diplomeHashMap.containsKey(nomDiplome)) {
            throw new IllegalArgumentException("Un diplôme nommé '" + nomDiplome + "' existe déjà.");
        } else {
            // Ajout du diplome dans la HashMap
            Diplome dip = new Diplome(nomDiplome, type, annee, maxEtu, ects);
            diplomeHashMap.put(nomDiplome, dip);

            int cpt = dip.annee;
            while(cpt>0){
                int cleAnnee = cpt;
                ArrayList<UE> nouvelleListe = new ArrayList<>();
                dip.UEHashMap.put(cleAnnee, nouvelleListe);
                //System.out.println("Année "+ cleAnnee + " crée.");
                cpt--;
            }
            System.out.println("Diplôme " + nomDiplome + " créé");
            return dip;
        }
    }


    public void consulterAnneeDiplome (Diplome dip, int annee){
        System.out.println("Liste des UE enseignées en année " + annee + " de " + dip.nomDiplome);
        ArrayList<UE> ues = dip.UEHashMap.get(annee);
        if (ues == null || ues.isEmpty()) {
            System.out.println("  (Aucune UE)");
            return;
        }

        for (int i = 0; i < ues.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + ues.get(i));
        }

    }

    public void consulterDiplome(Diplome dip) {
        System.out.println("Voici la liste des UE par années pour " + dip.nomDiplome + " :");

        dip.UEHashMap.forEach((Integer annee, ArrayList<UE> listUE) -> {
            System.out.println("Année " + annee + " :");

            if (listUE.isEmpty()) {
                System.out.println("  (Aucune UE)");
            } else {
                for (int i = 0; i < listUE.size(); i++) {
                    System.out.println("  " + (i + 1) + ") " + listUE.get(i));
                }
            }
        });
    }


    public Enseignant ajouterEnseigantUE(UE ue, Enseignant enseignant, int nbHeures) {
        //on  ajoute l'enseignant à la HashMap de l'UE associé au nombre d'heures
        ue.heuresParEnseignant.put(enseignant.name, nbHeures);
        System.out.print("L'enseignant " + enseignant.name + " " + enseignant.firstName + " ajouté à l'UE "+ ue + " avec un volume de "+ nbHeures + "h");
        return enseignant;

    }

    public Enseignant enregistrerEnseignant(String nom, String prenom) {
        if (enseignantsByNom.containsKey(nom)) {
            throw new IllegalArgumentException("Enseignant déjà existant: " + nom);
        }
        Enseignant e = new Enseignant(nom, prenom);
        enseignantsByNom.put(nom, e);
        return e;
    }


    public UE assignerUE(Diplome dip, UE ue, int nbAnneeDip){
        //On ajoute l'UE dans la bonne année du diplome
        ArrayList<UE> listeAInserer = dip.UEHashMap.get(nbAnneeDip);

        //On ajoute l'UE dans la bonne année du diplome
        listeAInserer.add(ue);

        System.out.println("UE " + ue + " ajoutée à l'année " + nbAnneeDip + " du diplôme " + dip.nomDiplome );
        return ue;
    }

    public Diplome getDiplome(String nomDiplome) {
        return diplomeHashMap.get(nomDiplome);
    }

    public int getTotal(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nom vide");
        }

        // 1) ALL : total de tous les diplômes
        if (name.equalsIgnoreCase("ALL")) {
            int total = 0;
            for (Diplome d : diplomeHashMap.values()) {
                total += totalHeuresDiplome(d);
            }
            return total;
        }

        // 2) Diplôme : si le nom correspond à un diplôme
        Diplome d = diplomeHashMap.get(name);
        if (d != null) {
            return totalHeuresDiplome(d);
        }

        // 3) UE : si le nom correspond à une UE (unique)
        UE ue = findUEByName(name);
        if (ue != null) {
            return ue.cm + ue.td + ue.tp;
        }

        // 4) Enseignant : total des heures affectées (sur toutes les UE)
        // (fonctionne déjà avec ton HashMap heuresParEnseignant dans UE)
        if (enseignantsByNom.containsKey(name)) {
            return totalHeuresEnseignant(name);
        }

        // Si tu veux accepter aussi "Dupont" même non enregistré, remplace le test ci-dessus par:
        // return totalHeuresEnseignant(name); avec erreur si total == 0.

        throw new IllegalArgumentException("Aucun diplôme, UE ou enseignant ne correspond à: " + name);
    }

    private int totalHeuresDiplome(Diplome d) {
        int total = 0;
        for (UE ue : d.UEList) {
            total += ue.cm + ue.td + ue.tp;
        }
        return total;
    }

    private UE findUEByName(String nomUE) {
        for (Diplome d : diplomeHashMap.values()) {
            for (UE ue : d.UEList) {
                if (ue.nomUE.equals(nomUE)) {
                    return ue;
                }
            }
        }
        return null;
    }

    private int totalHeuresEnseignant(String nomEnseignant) {
        int total = 0;
        for (Diplome d : diplomeHashMap.values()) {
            for (UE ue : d.UEList) {
                Integer h = ue.heuresParEnseignant.get(nomEnseignant);
                if (h != null) total += h;
            }
        }
        return total;
    }

    public TotalTargetType getTotalTargetType(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nom vide");
        }

        if (name.equalsIgnoreCase("ALL")) return TotalTargetType.ALL;

        if (diplomeHashMap.containsKey(name)) return TotalTargetType.DIPLOME;

        if (enseignantsByNom.containsKey(name)) return TotalTargetType.ENSEIGNANT;

        // sinon, on essaie UE
        UE ue = findUEByName(name);
        if (ue != null) return TotalTargetType.UE;

        throw new IllegalArgumentException("Aucun diplôme, UE ou enseignant ne correspond à: " + name);
    }

    public UE trouverUEUnique(String nomUE) {
        UE found = null;

        for (Diplome d : diplomeHashMap.values()) {
            for (UE ue : d.UEList) {
                if (ue.nomUE.equals(nomUE)) {
                    if (found != null) {
                        throw new IllegalArgumentException(
                                "UE ambiguë: '" + nomUE + "' existe dans plusieurs diplômes"
                        );
                    }
                    found = ue;
                }
            }
        }

        if (found == null) {
            throw new IllegalArgumentException("UE introuvable: " + nomUE);
        }

        return found;
    }






    // public int calculerVolumetrieHeures(){
    // Il faut accèder au à la liste d'UE de chaque diplome de la formation,
    // puis accéder à la HashMap d'enseignant de l'UE pour faire la somme des heures,
    // ou alors dans UE on fait un compteur du nombre d'heures (surement plus simple)
      //  diplomeHashMap.forEach((nomDiplome, d) -> d.UEList.get()));eee
  //  }









}
