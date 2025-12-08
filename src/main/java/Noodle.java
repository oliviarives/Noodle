import java.lang.reflect.Type;
import java.util.HashMap;

public class Noodle {
    HashMap<String, Diplome> diplomeHashMap = new HashMap<>();
    //HashMap<String, Enseignant> enseignantHashMap = new HashMap<>();

    public Noodle(){
        this.diplomeHashMap = diplomeHashMap;
    }

    public HashMap<String, Diplome> getDiplomeHashMap() {
        return diplomeHashMap;
    }

    //public HashMap<String, Enseignant> getEnseignantHashMap() {
       // return enseignantHashMap;
    //}

    public Diplome creerDiplome(String nomDiplome, TypeDiplome type, int annee, int maxEtu, int ects){
        // Verif si le diplome existe déjà
        if (diplomeHashMap.containsKey(nomDiplome)) {
            throw new IllegalArgumentException("Un diplôme nommé '" + nomDiplome + "' existe déjà.");
        } else {
            // Ajout du diplome dans la HashMap
            Diplome dip = new Diplome(nomDiplome, type, annee, maxEtu, ects);
            diplomeHashMap.put(nomDiplome, dip);

            System.out.println("Diplôme créé");
            return dip;
        }
    }

  //  public Diplome consulterDiplome(String nomDiplome){


}
