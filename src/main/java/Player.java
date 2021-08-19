class Player{
    int id;
    String firstName;
    String lastName;

    Player(int id, String firstName, String lastName){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    boolean isThis(String player_name){
        return player_name.equals(firstName + " " + lastName) || player_name.equals(lastName + " " + firstName);
    }
}