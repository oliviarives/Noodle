public class Main {
    public static void main(String[] args) {

        Noodle noodle = new Noodle();
        Diplome dip;

        // Creation d'un diplome
        dip = noodle.creerDiplome("LicenceInfo", TypeDiplome.LICENCE, 3, 180, 180);
        noodle.afficherDiplomes();
        // Tentative de doublon levé de l'exception
        //noodle.creerDiplome("LicenceInfo", TypeDiplome.LICENCE, 3, 180, 180);

        dip.creerUE("Maths",6,10,10,10,2);
        dip.creerUE("CAca",6,10,10,10,2);
        dip.creerUE("Philosophie",6,10,10,10,1);
    }
}