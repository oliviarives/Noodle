import java.util.ArrayList;
import java.util.HashMap;

public class Diplome {
    String nomDiplome;
    TypeDiplome type;
    int annee;
    int maxEtu;
    int ects;
    ArrayList<UE> UEList = new ArrayList<>();
    ArrayList<Integer> anneeUn = new ArrayList<>();
    ArrayList<Integer> anneeDeux = new ArrayList<>();
    ArrayList<Integer> anneeTrois = new ArrayList<>();
    HashMap<ArrayList<Integer>, ArrayList<UE>> UEHashMap = new HashMap<>();

    public Diplome(String nomDiplome, TypeDiplome type, int annee, int maxEtu, int ects){
        this.nomDiplome = nomDiplome;
        this.type = type;
        this.annee = annee;
        this.maxEtu = maxEtu;
        this.ects = ects;
    }

    public UE creerUE(String nomUE, int ects, int cm, int td, int tp, int nbAnneeDip){
        int un = 1;
        UE mat = new UE(nomUE, ects, cm, td, tp);
        UEList.add(mat);
        if (nbAnneeDip == un){
            UEHashMap.put(anneeUn, UEList);
        }

        //UEHashMap.put(annee, UEList);
        return mat;
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
