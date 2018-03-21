package assignment4;
/* CRITTERS Critter.java
 * EE422C Project 4 submission by
 * Replace <...> with your actual data.
 * Dylan Cauwels
 * dmc3692
 * 15505
 * Srinjoy Majumdar
 * uhhhh
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
	
	protected final void walk(int direction) {
		energy -= Params.walk_energy_cost;					//Deducting energy cost from the Critter
		int oldX = x_coord;									//Storing the old coordinates to move the critter on the map
		int oldY = y_coord;
		walkCalc(this, direction);
		if(x_coord >= Params.world_width)						//Checking that new coordinates aren't out of bounds
			x_coord = x_coord - Params.world_width;
		else if(x_coord < 0)
			x_coord = x_coord + Params.world_width;
		if(y_coord >= Params.world_height)
			y_coord = y_coord - Params.world_height;
		else if(y_coord < 0)
			y_coord = y_coord - Params.world_height;
		CritterWorld.moveCritter(this, oldX, oldY);
	}

	//TODO compartmentalize similar functions of run and walk
	protected final void run(int direction) {
		energy -= Params.run_energy_cost;					//Deducting energy cost from the Critter
		int oldX = x_coord;									//Storing the old coordinates to move the critter on the map
		int oldY = y_coord;
		switch(direction) {									//Moving the Critter in the desired direction (Only straight lines)
			case 0:
				x_coord += 2;
				break;
			case 2:
				y_coord += 2;
				break;
			case 4:
				x_coord -= 2;
				break;
			case 6:
				y_coord -= 2;
				break;
			default:
				break;
		}
		if(x_coord >= Params.world_width)						//Checking that new coordinates aren't out of bounds
			x_coord = x_coord - Params.world_width;
		else if(x_coord < 0)
			x_coord = x_coord + Params.world_width;
		if(y_coord >= Params.world_height)
			y_coord = y_coord - Params.world_height;
		else if(y_coord < 0)
			y_coord = y_coord - Params.world_height;
		CritterWorld.moveCritter(this, oldX, oldY);
	}
	
	protected final void reproduce(Critter offspring, int direction) {
		if(this.energy < Params.min_reproduce_energy)
			return;
		offspring.energy = this.energy/2;
		this.energy /= 2;
		//TODO figure out how to do their stupid rounding shit
		babies.add(offspring);
		offspring.x_coord = x_coord;
		offspring.y_coord = y_coord;
		walkCalc(offspring, direction);
	}

	private static void walkCalc(Critter a, int direction) {
		switch(direction) {									//Moving the Critter in the desired direction
			case 0:
				a.x_coord++;
				break;
			case 1:
				a.x_coord++;
				a.y_coord++;
				break;
			case 2:
				a.y_coord++;
				break;
			case 3:
				a.x_coord--;
				a.y_coord++;
				break;
			case 4:
				a.x_coord--;
				break;
			case 5:
				a.x_coord--;
				a.y_coord--;
				break;
			case 6:
				a.y_coord--;
				break;
			case 7:
				a.y_coord--;
				a.x_coord++;
				break;
			default:
				break;
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
			Class classTemp = Class.forName(critter_class_name);
			Critter obj = (Critter)classTemp.newInstance();
			babies.add(obj);
			obj.energy = Params.start_energy;
			obj.x_coord = getRandomInt(Params.world_width - 1);
			obj.y_coord = getRandomInt(Params.world_height - 1);
			CritterWorld.addCritter(obj);
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException a) {
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
		List<Critter> result = new java.util.ArrayList<Critter>();
	
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
			super.x_coord = new_x_coord;
		}
		
		protected void setY_coord(int new_y_coord) {
			super.y_coord = new_y_coord;
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
	}
	
	public static void worldTimeStep() {
		for (Critter current: population) {
			current.doTimeStep();
		}
		CritterWorld.resolveConflicts();
		for (Critter survived: population) {
			survived.energy -= Params.rest_energy_cost;
			if(survived.energy < 0) {
				population.remove(survived);
			}
		}
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
	}

	static class CritterWorld{
		static ArrayList<Critter>[][] world = new ArrayList[Params.world_width][Params.world_height];


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
			while(world[x][y].size() > 1) {
				Critter a = (Critter)world[x][y].get(0);
				Critter b = (Critter)world[x][y].get(1);
				int aEnergy = 0;
				int bEnergy = 0;
				if(a.fight(b.toString()))
					aEnergy = getRandomInt(a.energy);
				if(b.fight(a.toString()))
					bEnergy = getRandomInt(b.energy);
				if(aEnergy >= bEnergy && world[x][y].contains(b)) {					//if bEnergy equals 0 and the b is gone -> no fight, else fight
					population.remove(b);
					world[x][y].remove(b);
					a.energy += b.energy/2;
				} else if(world[x][y].contains(a) && world[x][y].contains(b)){									//if a is gone then they don't fight anymore
					population.remove(a);
					world[x][y].remove(a);
					b.energy += a.energy/2;
				}
				if(x != a.x_coord || y != a.y_coord)
					moveCritter(a, x, y);
				if(x != b.x_coord || y != b.y_coord)
					moveCritter(b, x, y);
			}
		}

		public static void moveCritter(Critter a, int oldX, int oldY) {
			world[oldX][oldY].remove(a);
			world[a.x_coord][a.y_coord].add(a);
		}

		static void addCritter(Critter a) {
			world[a.x_coord][a.y_coord].add(a);
		}
	}
}
