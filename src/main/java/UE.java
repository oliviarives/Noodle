import java.io.Serializable;
import java.util.HashMap;

public class UE implements Serializable {
    private static final long serialVersionUID = 1L;
    String nomUE;
    int ects;
    int cm;
    int td;
    int tp;
    HashMap<String, Integer> heuresParEnseignant = new HashMap<>();



    public UE(String nomUE, int ects, int cm, int td, int tp) {
        this.nomUE = nomUE;
        this.ects = ects;
        this.cm = cm;
        this.td = td;
        this.tp = tp;
    }

    @Override
    public String toString() {
        return String.format("%s - %d ECTS (CM:%dh TD:%dh TP:%dh)",
                nomUE, ects, cm, td, tp);
    }

    public UE() { }

    public HashMap<String, Integer> getHeuresParEnseignant() {
        return heuresParEnseignant;
    }

    // =========================
    // ITE-3 - Affectation enseignant -> UE
    // =========================
    public void affecterEnseignant(String nomEnseignant, int nbHeures) {
        if (nbHeures <= 0) {
            throw new IllegalArgumentException("Nombre d'heures invalide");
        }

        int actuel = heuresParEnseignant.getOrDefault(nomEnseignant, 0);
        heuresParEnseignant.put(nomEnseignant, actuel + nbHeures);
    }

    public int getTotalHeures() {
        return cm + td + tp;
    }

    public int getHeuresAffectees() {
        int total = 0;
        for (Integer h : heuresParEnseignant.values()) {
            if (h != null) total += h;
        }
        return total;
    }

    public int getCover() {
        int total = getTotalHeures();
        if (total <= 0) return 0;

        double p = (getHeuresAffectees() * 100.0) / total;
        return (int) Math.round(p);
    }





    public int getEcts() {
        return ects;
    }

    public int getCm() {
        return cm;
    }

    public int getTd() {
        return td;
    }

    public int getTp() {
        return tp;
    }
}
