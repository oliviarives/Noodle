public class Main {
    public static void main(String[] args) {

        Noodle noodle = new Noodle();
        Diplome dip;

        // Creation d'un diplome
        dip = noodle.creerDiplome("LicenceInfo", TypeDiplome.LICENCE, 3, 180, 180);

        // Tentative de doublon levé de l'exception
        //noodle.creerDiplome("LicenceInfo", TypeDiplome.LICENCE, 3, 180, 180);

        UE ueTest = dip.creerUE("Maths",6,10,10,10,2);
        dip.creerUE("Français",6,10,10,10,2);
        dip.creerUE("Philosophie",6,10,10,10,1);

        noodle.consulterDiplome(dip);
        noodle.consulterAnneeDiplome(dip,2);

        Enseignant enseignant1 = new Enseignant("Damien", "Gouteux");
        noodle.ajouterEnseigantUE(ueTest,enseignant1,10);

        Diplome dip2 = noodle.creerDiplome("LicenceMIASHS", TypeDiplome.LICENCE, 3, 180, 180);
        noodle.assignerUE(dip2,ueTest,2);

        noodle.afficherDiplomes();



    }
}