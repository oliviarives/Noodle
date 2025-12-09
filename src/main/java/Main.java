public class Main {
    public static void main(String[] args) {

        Noodle noodle = new Noodle();

        // Creation d'un diplome
        noodle.creerDiplome("LicenceInfo", TypeDiplome.LICENCE, 3, 180, 180);
        noodle.afficherDiplomes();
        // Tentative de doublon levé de l'exception
        noodle.creerDiplome("LicenceInfo", TypeDiplome.LICENCE, 3, 180, 180);
    }
}