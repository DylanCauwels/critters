package assignment4;
/* CRITTERS Critter.java
 * EE422C Project 4 submission by
 * Replace <...> with your actual data.
 * Dylan Cauwels
 * dmc3692
 * 15505
 * Srinjoy Majumdar
 * sm64469
 * 15505
 * Slip days used: <0>
 * Fall 2016
 */


import org.omg.CORBA.DynAnyPackage.Invalid;
import sun.awt.Symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/* see the PDF for descriptions of the methods and fields in this class
 * you may add fields, methods or inner classes to Critter ONLY if you make your additions private
 * no new public, protected or default-package code or data can be added to Critter
 */

public abstract class Critter {
	private static String myPackage;
	private	static List<Critter> population = new java.util.ArrayList<Critter>();
	private static List<Critter> babies = new java.util.ArrayList<Critter>();

	// Gets the package name.  This assumes that Critter and its subclasses are all in the same package.
	static {
		myPackage = Critter.class.getPackage().toString().split(" ")[1];
	}
	
	private static java.util.Random rand = new java.util.Random();
	public static int getRandomInt(int max) {
		return rand.nextInt(max);
	}
	
	public static void setSeed(long new_seed) {
		rand = new java.util.Random(new_seed);
	}
	
	
	/* a one-character long string that visually depicts your critter in the ASCII interface */
	public String toString() { return ""; }
	
	private int energy = 0;
	protected int getEnergy() { return energy; }
	
	private int x_coord;
	private int y_coord;

	private boolean hasMoved = false;
	private boolean inFight = false;
	
	protected final void walk(int direction) {
		energy -= Params.walk_energy_cost; //Deducting energy cost from the Critter
		if(!hasMoved){
			hasMoved = true;

			int oldX = x_coord;									//Storing the old coordinates to move the critter on the map
			int oldY = y_coord;
			calcCoord(this, direction, 1);

			if(inFight && CritterWorld.world[x_coord][y_coord].size()>0){
				x_coord = oldX;
				y_coord = oldY;
			}
			CritterWorld.moveCritter(this, oldX, oldY);
		}
	}

	//TODO compartmentalize similar functions of run and walk
	protected final void run(int direction) {
		energy -= Params.run_energy_cost; //Deducting energy cost from the Critter
		if(!hasMoved) {
			int oldX = x_coord;                                    //Storing the old coordinates to move the critter on the map
			int oldY = y_coord;
			calcCoord(this, direction, 2);

			if(inFight && CritterWorld.world[x_coord][y_coord] != null && CritterWorld.world[x_coord][y_coord].size()>0){
				x_coord = oldX;
				y_coord = oldY;
			}
			CritterWorld.moveCritter(this, oldX, oldY);
		}
	}
	
	protected final void reproduce(Critter offspring, int direction) {
		if(this.energy < Params.min_reproduce_energy || this.energy <= 0)
			return;
		offspring.energy = this.energy/2;
		this.energy = this.energy - this.energy/2;  //does this work?? for the rounding
		babies.add(offspring);
		offspring.x_coord = x_coord;
		offspring.y_coord = y_coord;
		CritterWorld.addCritter(offspring);
		offspring.walk(direction);
	}

	private static void calcCoord(Critter a, int direction, int increment) {
		switch(direction) {									//Moving the Critter in the desired direction
			case 0:
				a.x_coord+=increment;
				break;
			case 1:
				a.x_coord+=increment;
				a.y_coord+=increment;
				break;
			case 2:
				a.y_coord+=increment;
				break;
			case 3:
				a.x_coord-=increment;
				a.y_coord+=increment;
				break;
			case 4:
				a.x_coord-=increment;
				break;
			case 5:
				a.x_coord-=increment;
				a.y_coord-=increment;
				break;
			case 6:
				a.y_coord-=increment;
				break;
			case 7:
				a.y_coord-=increment;
				a.x_coord+=increment;
				break;
			default:
				break;
		}
		if (a.x_coord >= Params.world_width) {                        //Checking that new coordinates aren't out of bounds
			a.x_coord = a.x_coord - Params.world_width;
		} else if (a.x_coord < 0){
			a.x_coord = a.x_coord + Params.world_width;
		}
		if (a.y_coord >= Params.world_height) {
			a.y_coord = a.y_coord - Params.world_height;
		} else if (a.y_coord < 0){
			a.y_coord = a.y_coord + Params.world_height;
		}
	}
	public abstract void doTimeStep();
	public abstract boolean fight(String opponent);
	
	/**
	 * create and initialize a Critter subclass.
	 * critter_class_name must be the unqualified name of a concrete subclass of Critter, if not,
	 * an InvalidCritterException must be thrown.
	 * (Java weirdness: Exception throwing does not work properly if the parameter has lower-case instead of
	 * upper. For example, if craig is supplied instead of Craig, an error is thrown instead of
	 * an Exception.)
	 * @param critter_class_name
	 * @throws InvalidCritterException
	 */
	public static void makeCritter(String critter_class_name) throws InvalidCritterException {
		try {
			Class classTemp = Class.forName(myPackage+"."+critter_class_name);
			Critter obj = (Critter)classTemp.newInstance();
			obj.energy = Params.start_energy;
			obj.x_coord = getRandomInt(Params.world_width);
			obj.y_coord = getRandomInt(Params.world_height);
			CritterWorld.addCritter(obj);
			population.add(obj);
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoClassDefFoundError a) {
			throw new InvalidCritterException(critter_class_name);
		}
	}
	
	/**
	 * Gets a list of critters of a specific type.
	 * @param critter_class_name What kind of Critter is to be listed.  Unqualified class name.
	 * @return List of Critters.
	 * @throws InvalidCritterException
	 */
	public static List<Critter> getInstances(String critter_class_name) throws InvalidCritterException {
		List<Critter> result = new ArrayList<Critter>();
		try{
			Class classTemp = Class.forName(myPackage+"."+critter_class_name);
			Critter obj = (Critter)classTemp.newInstance();
			for(Critter critter: population){
				if (critter.getClass().equals(classTemp)){
					result.add(critter);
				}
			}
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException a) {
			throw new InvalidCritterException(critter_class_name);
		}

		return result;
	}
	
	/**
	 * Prints out how many Critters of each type there are on the board.
	 * @param critters List of Critters.
	 */
	public static void runStats(List<Critter> critters) {
		System.out.print("" + critters.size() + " critters as follows -- ");
		java.util.Map<String, Integer> critter_count = new java.util.HashMap<String, Integer>();
		for (Critter crit : critters) {
			String crit_string = crit.toString();
			Integer old_count = critter_count.get(crit_string);
			if (old_count == null) {
				critter_count.put(crit_string,  1);
			} else {
				critter_count.put(crit_string, old_count.intValue() + 1);
			}
		}
		String prefix = "";
		for (String s : critter_count.keySet()) {
			System.out.print(prefix + s + ":" + critter_count.get(s));
			prefix = ", ";
		}
		System.out.println();		
	}

	/* the TestCritter class allows some critters to "cheat". If you want to 
	 * create tests of your Critter model, you can create subclasses of this class
	 * and then use the setter functions contained here. 
	 * 
	 * NOTE: you must make sure that the setter functions work with your implementation
	 * of Critter. That means, if you're recording the positions of your critters
	 * using some sort of external grid or some other data structure in addition
	 * to the x_coord and y_coord functions, then you MUST update these setter functions
	 * so that they correctly update your grid/data structure.
	 */
	static abstract class TestCritter extends Critter {
		protected void setEnergy(int new_energy_value) {
			super.energy = new_energy_value;
		}
		
		protected void setX_coord(int new_x_coord) {
			int old_x_coord = super.x_coord;
			super.x_coord = new_x_coord;
			CritterWorld.moveCritter(this, old_x_coord, super.y_coord);

		}
		
		protected void setY_coord(int new_y_coord) {
			int old_y_coord = super.y_coord;
			super.y_coord = new_y_coord;
			CritterWorld.moveCritter(this, super.x_coord, old_y_coord);
		}
		
		protected int getX_coord() {
			return super.x_coord;
		}

		protected int getY_coord() {
			return super.y_coord;
		}
		

		/*
		 * This method getPopulation has to be modified by you if you are not using the population
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.
		 */
		protected static List<Critter> getPopulation() {
			return population;
		}
		
		/*
		 * This method getBabies has to be modified by you if you are not using the babies
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.  Babies should be added to the general population 
		 * at either the beginning OR the end of every timestep.
		 */
		protected static List<Critter> getBabies() {
			return babies;
		}
	}

	/**
	 * Clear the world of all critters, dead and alive
	 */
	public static void clearWorld() {
		population.clear();
		babies.clear();
		CritterWorld.clearWorld();

	}
	
	public static void worldTimeStep() {
		for (Critter current: population) {
			current.doTimeStep();
		}
		CritterWorld.resolveConflicts();
		List<Critter> dead = new ArrayList();
		for (Critter survived: population) {
			survived.energy -= Params.rest_energy_cost;
			if(survived.energy < 0) {
				dead.add(survived);
				CritterWorld.world[survived.x_coord][survived.y_coord].remove(0);
			}
		}
		population.removeAll(dead);
		for(int i = 0; i < Params.refresh_algae_count; i++) {
			try {
				Critter.makeCritter("Algae");
			} catch(InvalidCritterException a) {
				System.out.println("lel");
			}
		}
		population.addAll(babies);
		babies.clear();
	}
	
	public static void displayWorld() {
		// Complete this method.

		System.out.print("+");
		for(int i = 0; i<Params.world_width; i++){
			System.out.print("-");
		}
		System.out.print("+");
		System.out.println();
		for(int y = 0; y<Params.world_height; y++){
			System.out.print("|");
			for(int x = 0; x<Params.world_width;x++){
				if(CritterWorld.world[x][y] != null && CritterWorld.world[x][y].size()>0){
					System.out.print((Critter)CritterWorld.world[x][y].get(0));
				}else{
					System.out.print(" ");
				}
			}
			System.out.print("|");
			System.out.println();
		}
		System.out.print("+");
		for(int i = 0; i<Params.world_width; i++){
			System.out.print("-");
		}
		System.out.print("+");
		System.out.println();
	}

	static class CritterWorld{
		static ArrayList<Critter>[][] world = new ArrayList[Params.world_width][Params.world_height];

		static void clearWorld(){
			for(int i = 0; i < Params.world_width; i++) {
				for(int j = 0; j < Params.world_height; j++) {
					world[i][j] = null;
				}
			}
		}

		static void resolveConflicts() {
			for(int i = 0; i < Params.world_width; i++) {
				for(int j = 0; j < Params.world_height; j++) {
					if(world[i][j]!=null && world[i][j].size() > 1) {
						resolveConflict(world, i, j);
					}
				}
			}
		}


		static void resolveConflict(ArrayList[][] world, int x, int y) {
			while (world[x][y].size() > 1) {
				Critter a = (Critter) world[x][y].get(0);
				Critter b = (Critter) world[x][y].get(1);
				int aRand = 0;
				int bRand = 0;
				a.inFight = true;
				b.inFight = true;
				boolean aFight = a.fight(b.toString());
				boolean bFight = b.fight(a.toString());
				a.inFight = false;
				b.inFight = false;

				if (a.energy > 0 && b.energy > 0) {
					if (a.x_coord == x && a.y_coord == y && b.x_coord == x && b.y_coord == y) {
						if (aFight) {
							aRand = getRandomInt(a.energy);
						}
						if (bFight) {
							bRand = getRandomInt(b.energy);
						}
						if (aRand >= bRand) {                    //if bEnergy equals 0 and the b is gone -> no fight, else fight
							population.remove(b);
							world[x][y].remove(b);
							a.energy += b.energy / 2;
						} else {                                    //if a is gone then they don't fight anymore
							population.remove(a);
							world[x][y].remove(a);
							b.energy += a.energy / 2;
						}
					}
				} else { //Critters died trying to move or were dead already
					if (a.energy <= 0) {
						population.remove(a);
						world[x][y].remove(a);
					}
					if (b.energy <= 0) {
						population.remove(b);
						world[x][y].remove(b);
					}
				}

			}
		}

		public static void moveCritter(Critter a, int oldX, int oldY) {
//			if(world[oldX][oldY]!=null){
				world[oldX][oldY].remove(a);
//			}
			addCritter(a);
//			world[a.x_coord][a.y_coord].add(a);
		}

		static void addCritter(Critter a) {
			if(world[a.x_coord][a.y_coord]==null){
				world[a.x_coord][a.y_coord] = new ArrayList<Critter>();
			}
			world[a.x_coord][a.y_coord].add(a);
		}
	}
}
