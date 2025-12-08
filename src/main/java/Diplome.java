import java.util.HashMap;

public class Diplome {
    String nomDiplome;
    TypeDiplome type;
    int annee;
    int maxEtu;
    int ects;

    public Diplome(String nomDiplome, TypeDiplome type, int annee, int maxEtu, int ects){
        this.nomDiplome = nomDiplome;
        this.type = type;
        this.annee = annee;
        this.maxEtu = maxEtu;
        this.ects = ects;
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
