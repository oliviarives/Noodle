import java.util.ArrayList;
import java.util.HashMap;

public class Diplome {
    String nomDiplome;
    TypeDiplome type;
    int annee;
    int maxEtu;
    int ects;
    ArrayList<UE> UEList = new ArrayList<>();
    HashMap<Integer, ArrayList<UE>> UEHashMap = new HashMap<>();

    public Diplome(String nomDiplome, TypeDiplome type, int annee, int maxEtu, int ects){
        this.nomDiplome = nomDiplome;
        this.type = type;
        this.annee = annee;
        this.maxEtu = maxEtu;
        this.ects = ects;
    }

    public UE creerUE(String nomUE, int ects, int cm, int td, int tp, int nbAnneeDip){

        UE nouvelleUE = new UE(nomUE, ects, cm, td, tp);

        //On ajoute l'UE à la liste générale d'UE
        UEList.add(nouvelleUE);


        //On ajoute l'UE dans la bonne année du diplome
        ArrayList<UE> listeAInserer = UEHashMap.get(nbAnneeDip);
        //On ajoute l'UE dans la bonne année du diplome
        listeAInserer.add(nouvelleUE);

        System.out.println("UE " + nouvelleUE + " ajoutée à l'année " + nbAnneeDip + " du diplôme " + this.nomDiplome );

        //UEHashMap.put(annee, UEList);
        return nouvelleUE;
    }



    public String getName(){
        return this.nomDiplome;
    }

    public TypeDiplome getType(){
        return this.type;
    }

    public int getAnnee(){
        return this.annee;
    }

    public int getMaxEtu() {
        return this.maxEtu;
    }

    public int getEcts(){
        return this.ects;
    }

}
