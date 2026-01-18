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

    public void supprimerUE(String nomUE, int nbAnneeDip) {
        ArrayList<UE> uesAnnee = UEHashMap.get(nbAnneeDip);
        if (uesAnnee == null) {
            throw new IllegalArgumentException("Année invalide: " + nbAnneeDip);
        }

        UE cible = null;
        for (UE ue : uesAnnee) {
            if (ue.nomUE.equals(nomUE)) {
                cible = ue;
                break;
            }
        }

        if (cible == null) {
            throw new IllegalArgumentException("UE introuvable: " + nomUE);
        }

        // cohérence: suppression dans l'année + dans la liste globale
        uesAnnee.remove(cible);
        UEList.remove(cible);
    }




    @Override
    public String toString() {
        return String.format("%s (%s, %d an(s), %d ECTS)",
                nomDiplome, type, annee, ects);
    }

    public UE getDerniereUEAnnee(int annee) {
        ArrayList<UE> ues = UEHashMap.get(annee);
        if (ues == null || ues.isEmpty()) {
            throw new IllegalStateException("Aucune UE dans l'année " + annee);
        }
        return ues.get(ues.size() - 1);
    }


    public TypeDiplome getType(){
        return this.type;
    }

    public Diplome() { }


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
