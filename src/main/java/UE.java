import java.util.ArrayList;
import java.util.HashMap;

public class UE {
    String nomUE;
    int ects;
    int cm;
    int td;
    int tp;
    HashMap<Enseignant, Integer> EnseignantHashMap = new HashMap<>();

    public UE(String nomUE, int ects, int cm, int td, int tp) {
        this.nomUE = nomUE;
        this.ects = ects;
        this.cm = cm;
        this.td = td;
        this.tp = tp;
    }

    public String getNomUE() {
        return nomUE;
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
