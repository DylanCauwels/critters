package assignment4;

//Snek
public class Critter4 extends Critter.TestCritter {

	@Override
	public void doTimeStep() {
		if(getRandomInt(9) == 9) {
			reproduce(new Critter4(), getRandomInt(7));
		} else {
			int integer = getRandomInt(8);                                  //always run never walk
			if(integer % 2 == 1) {
				run(integer - 1);
			} else {
				run(integer);
			}
		}
	}

	@Override
	public boolean fight(String opponent) {										//very contentious animals
		return true;
	}
	
	public String toString() {
		return "S";
	}
}
