package assignment4;


//Stick Bug
public class Critter3 extends Critter {
    @Override
    public void doTimeStep() {
        if(getRandomInt(1) == 1)
            walk(getRandomInt(7));
        reproduce(new Critter3(), getRandomInt(7));
    }

    @Override
    public boolean fight(String critter) {
        if(critter.equals("|")) {
            return true;
        } else {
            walk(getRandomInt(7));
            return false;
        }
    }

    public String toString() {
        return "|";
    }
}
