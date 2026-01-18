public class Enseignant {
    String name;
    String firstName;


public Enseignant(String name, String firstName){
    this.name = name;
    this.firstName = firstName;
    System.out.println("L'enseignant " + name + " " + firstName + " créé");
    }

    @Override
    public String toString() {
        return firstName + " " + name;
    }

    public Enseignant() { }







}