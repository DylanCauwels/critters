package assignment4;

public class Rabbit extends Critter{

    @Override
    public void doTimeStep() {
        if(getEnergy() > Params.rest_energy_cost + Params.min_reproduce_energy)
            reproduce(new Rabbit(), getRandomInt(7));                       //Reproducing like rabbits
        else {
            int integer = getRandomInt(8);                                  //always run never walk
            if(integer % 2 == 1) {
                run(integer - 1);
            } else {
                run(integer);
            }
        }
    }

    @Override
    public boolean fight(String opponent) {                                      //fight if they have high energy *for a rabbit*
        if (getEnergy() > 30) {
            return true;
        } else {
            run(getRandomInt(0));
            return false;
        }
    }

    public String toString() {
        return "^";
    }
}