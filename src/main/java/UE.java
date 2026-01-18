import java.util.ArrayList;
import java.util.HashMap;

public class UE {
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
