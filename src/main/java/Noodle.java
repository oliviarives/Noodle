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
            //Afficher les années
            dip.UEHashMap.forEach((c, listUE) -> {
                System.out.print("Année " + c + " : ");
                System.out.println(listUE.size() + " UE(s) - Liste: " + listUE);
            });
            System.out.println("Diplôme créé");
            return dip;
        }
    }

  //  public Diplome consulterDiplome(String nomDiplome){


}
