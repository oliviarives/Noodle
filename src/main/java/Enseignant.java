import java.io.Serializable;
import java.util.HashMap;

public class Enseignant implements Serializable {
    private static final long serialVersionUID = 1L;

    // Identifiant = nom (selon ton choix)
    String name;
    String firstName;

    // UE -> heures (on stocke par nom d'UE pour rester simple et sérialisable)
    HashMap<String, Integer> heuresParUE = new HashMap<>();

    public Enseignant(String name, String firstName) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nom enseignant vide");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("Prenom enseignant vide");
        }
        this.name = name;
        this.firstName = firstName;
        System.out.println("L'enseignant " + name + " " + firstName + " cree");
    }

    public Enseignant() { }

    public void ajouterAffectation(String nomUE, int nbHeures) {
        if (nomUE == null || nomUE.isBlank()) {
            throw new IllegalArgumentException("Nom UE vide");
        }
        if (nbHeures <= 0) {
            throw new IllegalArgumentException("Nombre d'heures invalide");
        }
        int actuel = heuresParUE.getOrDefault(nomUE, 0);
        heuresParUE.put(nomUE, actuel + nbHeures);
    }

    public int getTotalHeures() {
        int total = 0;
        for (Integer h : heuresParUE.values()) {
            if (h != null) total += h;
        }
        return total;
    }

    @Override
    public String toString() {
        return firstName + " " + name;
    }
}
