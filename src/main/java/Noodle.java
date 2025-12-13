import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class Noodle {
    HashMap<String, Diplome> diplomeHashMap = new HashMap<>();

    //HashMap<String, Enseignant> enseignantHashMap = new HashMap<>();

    public Noodle(){
        this.diplomeHashMap = diplomeHashMap;
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
        System.out.println(dip.UEHashMap.get(annee));
    }

    public void consulterDiplome (Diplome dip){
         System.out.printf("Voici la liste des UE par années pour " + dip.nomDiplome +" : ");
         dip.UEHashMap.forEach((c, listUE) -> {
                System.out.print("Année " + c + " : ");
                System.out.println(listUE.size() + " UE(s) - Liste: " + listUE);
            });
    }

    public Enseignant ajouterEnseigantUE(UE ue, Enseignant enseignant, int nbHeures) {
        //on  ajoute l'enseignant à la HashMap de l'UE associé au nombre d'heures
        ue.EnseignantHashMap.put(enseignant, nbHeures);
        System.out.print("L'enseignant " + enseignant.name + " " + enseignant.firstName + " ajouté à l'UE "+ ue + " avec un volume de "+ nbHeures + "h");
        return enseignant;
    }

    public UE assignerUE(Diplome dip, UE ue, int nbAnneeDip){
        //On ajoute l'UE dans la bonne année du diplome
        ArrayList<UE> listeAInserer = dip.UEHashMap.get(nbAnneeDip);

        //On ajoute l'UE dans la bonne année du diplome
        listeAInserer.add(ue);

        System.out.println("UE " + ue + " ajoutée à l'année " + nbAnneeDip + " du diplôme " + dip.nomDiplome );
        return ue;
    }

   // public int calculerVolumetrieHeures(){
    // Il faut accèder au à la liste d'UE de chaque diplome de la formation,
    // puis accéder à la HashMap d'enseignant de l'UE pour faire la somme des heures,
    // ou alors dans UE on fait un compteur du nombre d'heures (surement plus simple)
      //  diplomeHashMap.forEach((nomDiplome, d) -> d.UEList.get()));eee
  //  }









}
