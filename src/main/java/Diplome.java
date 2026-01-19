import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Diplome implements Serializable {
    private static final long serialVersionUID = 1L;
    String nomDiplome;
    TypeDiplome type;
    int annee;
    int maxEtu;
    int ects;

    // Liste globale des UE du diplome
    ArrayList<UE> UEList = new ArrayList<>();

    // UE par annee (cle = numero d'annee)
    HashMap<Integer, ArrayList<UE>> UEHashMap = new HashMap<>();

    public Diplome() {
        // Requis par Jackson
    }

    public Diplome(String nomDiplome, TypeDiplome type, int annee, int maxEtu, int ects) {
        this.nomDiplome = nomDiplome;
        this.type = type;
        this.annee = annee;
        this.maxEtu = maxEtu;
        this.ects = ects;

        // Initialisation des annees
        for (int i = 1; i <= annee; i++) {
            UEHashMap.put(i, new ArrayList<>());
        }
    }

    // =========================
    // ITE-1 - Creation d'une UE
    // =========================
    public UE creerUE(String nomUE, int ects, int cm, int td, int tp, int nbAnneeDip) {
        if (!UEHashMap.containsKey(nbAnneeDip)) {
            throw new IllegalArgumentException("Annee invalide: " + nbAnneeDip);
        }

        UE nouvelleUE = new UE(nomUE, ects, cm, td, tp);

        // Ajout a la liste globale du diplome
        UEList.add(nouvelleUE);

        // Ajout dans la bonne annee
        UEHashMap.get(nbAnneeDip).add(nouvelleUE);

        System.out.println(
                "UE " + nouvelleUE + " ajoutee a l'annee " + nbAnneeDip + " du diplome " + this.nomDiplome);

        return nouvelleUE;
    }

    // =========================
    // ITE-2 - Suppression d'une UE
    // =========================
    public void supprimerUE(String nomUE, int nbAnneeDip) {
        ArrayList<UE> uesAnnee = UEHashMap.get(nbAnneeDip);
        if (uesAnnee == null) {
            throw new IllegalArgumentException("Annee invalide: " + nbAnneeDip);
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

        uesAnnee.remove(cible);
        UEList.remove(cible);
    }

    // =========================
    // Utilitaire - derniere UE d'une annee
    // =========================
    public UE getDerniereUEAnnee(int nbAnneeDip) {
        ArrayList<UE> ues = UEHashMap.get(nbAnneeDip);
        if (ues == null || ues.isEmpty()) {
            throw new IllegalStateException("Aucune UE dans l'annee " + nbAnneeDip);
        }
        return ues.get(ues.size() - 1);
    }

    public void editerUE(String nomUE, int ects, int cm, int td, int tp, int year) {
        if (ects <= 0) throw new IllegalArgumentException("ECTS invalide");
        if (cm < 0 || td < 0 || tp < 0) throw new IllegalArgumentException("Heures invalides");

        ArrayList<UE> ues = UEHashMap.get(year);
        if (ues == null) throw new IllegalArgumentException("Annee invalide: " + year);

        UE ue = null;
        for (UE x : ues) {
            if (x.nomUE.equals(nomUE)) { ue = x; break; }
        }
        if (ue == null) throw new IllegalArgumentException("UE introuvable dans l'annee selectionnee: " + nomUE);

        ue.ects = ects;
        ue.cm = cm;
        ue.td = td;
        ue.tp = tp;
    }


    public void lierUEExistante(UE ue, int year) {
        ArrayList<UE> ues = UEHashMap.get(year);
        if (ues == null) throw new IllegalArgumentException("Annee invalide: " + year);

        // éviter doublon (même instance) dans la même année
        for (UE x : ues) {
            if (x == ue) return;
            if (x.nomUE.equals(ue.nomUE)) {
                // même nom mais pas la même instance => ambigu / incohérent
                throw new IllegalArgumentException("Une UE du meme nom existe deja dans cette annee: " + ue.nomUE);
            }
        }

        ues.add(ue);

        // si tu as un UEList global, l’ajouter aussi si absent
        if (!UEList.contains(ue)) {
            UEList.add(ue);
        }
    }


    @Override
    public String toString() {
        return String.format("%s (%s, %d an(s), %d ECTS)", nomDiplome, type, annee, ects);
    }

    public TypeDiplome getType() {
        return this.type;
    }

    public int getAnnee() {
        return this.annee;
    }

    public int getMaxEtu() {
        return this.maxEtu;
    }

    public int getEcts() {
        return this.ects;
    }
}
