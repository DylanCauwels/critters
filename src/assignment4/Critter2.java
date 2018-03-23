package assignment4;

//Elephant
public class Critter2 extends Critter{
    @Override
    public void doTimeStep() {
        if(getEnergy() > 200)
            reproduce(new Critter2(), getRandomInt(7)); //Elephants reproduce rarely and do so with great caution
        else {
            walk(getRandomInt(7));
        }
    }

    @Override
    public boolean fight(String opponent) {
        if(getEnergy() < 50 || opponent.equals("E"))         //Critter2 wont find when its recently had a child is too weak and hungry, or is facing another elephant
            return true;
        else {
            run(2);
            return false;
        }
    }

    public String toString() {
        return "E";
    }
}
