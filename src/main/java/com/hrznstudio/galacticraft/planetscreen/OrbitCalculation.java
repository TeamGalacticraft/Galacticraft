package com.hrznstudio.galacticraft.planetscreen;

public class OrbitCalculation {

    /* constant macros */
    public static final double G = 6.673e-11; // Gravitational Constant
    public static final double MASS_OF_SUN = 1.98e30;   // Mass of the sun
    public static final double METERS_PER_AU = 1.495e11;  // Number of meters in one AU (for conversion) 

    /**
     * @param M
     * @param T
     * @return
     */
    public static double getOrbitalDistance(double M, double T) {
        double numerator;   // local variable - numerator                                     
        double denominator; // local variable - denominator 
        double r;           // local variable - orbital distance of planet in meters 
        double R;           // method output  - orbital distance of planet in astronomical units 

        // 1. Find the value of the numerator.  
        numerator = G * MASS_OF_SUN * M * Math.pow(86400 * 365.26 * T, 2);
        // 2. Find the value of the denominator.  
        denominator = 4 * Math.pow(Math.PI, 2);
        // 3. Calculate the orbital distance of the planet in meters.     
        r = Math.cbrt(numerator / denominator);
        // 4. Convert the orbital distance of the planet from meters to astronomical units.  
        R = toAstronomicalUnits(r);
        // 5. Return the value of the orbital distance of the planet in astronomical units.  
        return (R);
    }

    /**
     * @param M
     * @param R
     * @return
     */
    public static double getOrbitalVelocity(double M, double R) {

        double numerator;   // local variable - numerator  
        double denominator; // local variable - denominator  
        double v;           // local variable - orbital velocity of planet in meters per second  
        double V;           // method output  - orbital velocity of planet in kilometers per second  

        // 1. Find the value of the numerator.  
        numerator = G * MASS_OF_SUN * M;
        // 2. Find the value of the denominator. 
        denominator = R * METERS_PER_AU;
        // 3. Calculate the orbital velocity of the planet in meters per second.  
        v = Math.sqrt(numerator / denominator);
        // 4. Convert the orbital velocity of the planet from meters per second to kilometers per second.  
        V = toKilometersPerSecond(v);
        // 5. Return the value of the orbital velocity of the planet in kilometers per second. 
        return (V);
    }

    public static double toAstronomicalUnits(double r) {
        return r / METERS_PER_AU;
    }

    public static double toKilometersPerSecond(double v) {
        return v / 1000;
    }
}
