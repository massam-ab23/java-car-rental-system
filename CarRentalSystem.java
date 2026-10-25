
import java.util.Scanner;

/**
 * Rent-a-Car Management System
 *
 * A Java console application for managing a car rental service.
 *
 * Features include registering vehicles, renting and returning
 * cars, tracking vehicle availability, calculating rental costs,
 * and displaying rental history and revenue.
 *
 * Uses two-dimensional arrays, methods, input validation,
 * and menu-driven interaction.
 */


public class CarRentalSystem {
    // Max limits for cars and rentals
    static final int MAX_CARS = 100;
    static final int MAX_RENTALS = 100;

    // Fleet array column indices
    static final int  REG_NUM= 0;
    static final int MAKE_MODEL= 1;
    static final int STATUS= 2;

    // Rental array column indices
    static final int NAME = 0;
    static final int RENT_REG = 1;
    static final int PICKUP = 2;
    static final int RETURN = 3;
    static final int COST = 4;

    static final int RENTAL_COST_PER_HOUR = 120;

    private static final Scanner scanner = new Scanner(System.in);


    /**
     * Main method: Program entry point.
     * @param args Command-line arguments (not used)
     */
    public static void main(final String[] args) {
        String[][] fleet = new String[MAX_CARS][3];
        String[][] rentals = new String[MAX_RENTALS][5];
        int fleetCount = 0;
        int rentalCount = 0;


        while (true) {
            // Display menu
            System.out.println("1. Add car to fleet");
            System.out.println("2. Rent a car");
            System.out.println("3. Return a car");
            System.out.println("4. Print car fleet");
            System.out.println("5. Print rental summary");
            System.out.println("q. End program");
            System.out.print("> Enter your option: ");
            String option = scanner.nextLine().trim();


            switch (option){
                case "1":
                    fleetCount = addCarToFleet(fleet, fleetCount);
                    break;
                case "2":
                    rentalCount = rentCar(fleet, rentals, fleetCount, rentalCount);
                    break;
                case "3":
                    returnCar(fleet,  rentals, fleetCount, rentalCount);
                    break;
                case "4":
                    printFleet(fleet, fleetCount);
                    break;
                case "5":
                    printRentalSummary(rentals, rentalCount);
                    break;
                case "q":
                    return;
                default:
                    System.out.println("invalid menu item");
            }

        }
    }

    /**
     * Adds a new car to the fleet if registration number is valid and unique.
     */
    static int addCarToFleet(String[][] fleet, int count) {
        if (count >= MAX_CARS) {
            System.out.println("Fleet is full. ");
            return count;
        }
        System.out.print("Enter car registration number: ");
        String reg = scanner.nextLine().trim();
        if (!reg.matches("[A-Z]{3}\\d{3}")) {
            System.out.println("Invalid registration number.");
            return count;
        }
        for (int i = 0; i < count; i++) {
            if (fleet[i][REG_NUM].equals(reg)) {
                System.out.printf("Number %s already exists\n", reg);
                return count;
            }
        }
        System.out.print("Enter car make and model: ");
        String model = scanner.nextLine().trim();
        fleet[count][REG_NUM] = reg;
        fleet[count][MAKE_MODEL] = model;
        fleet[count][STATUS] = "0"; //available
        System.out.printf("%s with registration number %s was added to car fleet.\n", model, reg);
        System.out.println("==========================================");

        return count + 1;
    }

    // Rent a car from the fleet
    static int rentCar(String[][] fleet, String[][] rentals, int fleetCount, int rentalCount) {

        // Check if rental storage is full
        if (rentalCount >= MAX_RENTALS) {
            System.out.println("Rental capacity reached.");
            return rentalCount;
        }

        System.out.print("Enter car's registration number: ");
        String reg = scanner.nextLine().trim();

        if (!reg.matches("[A-Z]{3}\\d{3}")) {
            System.out.println("Invalid registration number.");
            return rentalCount;
        }
        int index = findCar(fleet, reg, fleetCount);
        if (index == -1) {
            System.out.printf("Car %s not found\n", reg);
            return rentalCount;
        }
        if (fleet[index][STATUS].equals("1")) {
            System.out.printf("Car %s is not available\n", reg);
            return rentalCount;
        }
        System.out.print("Enter pickup time (hh:mm): ");
        String pickup = scanner.nextLine().trim();
        if (!isValidTime(pickup)) {
            System.out.println("Invalid time format.");
            return rentalCount;
        }
        System.out.print("Enter renter's name: ");
        String name = scanner.nextLine().trim();

        // Store rental info
        fleet[index][STATUS] = "1";
        rentals[rentalCount][NAME] = name;
        rentals[rentalCount][RENT_REG] = reg;
        rentals[rentalCount][PICKUP] = pickup;
        rentals[rentalCount][RETURN] = "";
        rentals[rentalCount][COST] = "";

        System.out.printf("Car with registration number %s was rented by %s at %s\n", reg, name, pickup);
        System.out.println("==========================================");

        return rentalCount + 1;

    }

    // Return a rented car and calculate cost
    static void returnCar(String[][] fleet, String[][] rentals, int fleetCount, int rentalCount) {
        System.out.print("Enter car's registration number: ");
        String reg = scanner.nextLine().trim();
        if (!reg.matches("[A-Z]{3}\\d{3}")) {
            System.out.println("Invalid registration number.");
            return;
        }
        int index = findCar(fleet, reg, fleetCount);
        if (index == -1) {
            System.out.printf("Car %s not found\n", reg);
            return;
        }
        if (fleet[index][STATUS].equals("0")) {
            System.out.printf("Car %s not rented\n", reg);
            return;
        }
        System.out.print("> Enter return time : ");
        String returnTime = scanner.nextLine().trim();
        if (!isValidTime(returnTime)) {
            System.out.println("Invalid time format.");
            return;
        }

        for (int i = 0; i < rentalCount; i++) {
            if (rentals[i][RENT_REG].equals(reg) && rentals[i][RETURN].isEmpty()) {
                double hours = calculateHours(rentals[i][PICKUP], returnTime);
                if (hours < 0) {
                    System.out.println("Invalid time format.");
                    return;
                }
                int cost = (int) Math.round(hours * RENTAL_COST_PER_HOUR);
                rentals[i][RETURN] = returnTime;
                rentals[i][COST] = cost + " SEK";
                fleet[index][STATUS] = "0"; // 0 for available
                System.out.println("==========================================");
                System.out.println("Rent-a-Car System");
                System.out.println("==========================================");
                System.out.printf("Name: %s\n", rentals[i][NAME]);
                System.out.printf("Car: %s (%s)\n", fleet[index][MAKE_MODEL], reg);
                System.out.printf("Time: %s-%s (%.1f hours)\n", rentals[i][PICKUP], returnTime, hours);
                System.out.printf("Total cost: %d SEK\n", cost);
                System.out.println();
                return;
            }
        }
    }

    // Print all cars in the fleet
    static void printFleet (String[][] fleet, int count) {
        System.out.println("Rent-a-Car Fleet");
        System.out.println("Fleet: ");
        System.out.println("Model           Numberplate Status");
        int available = 0;
        for (int i = 0; i < count; i++) {
            String status = fleet[i][STATUS].equals("1") ?  "Rented": "Available";
            if (status.equals("Available")) available++;
            System.out.printf("%-18s %-12s %s\n", fleet[i][MAKE_MODEL], fleet[i][REG_NUM], status);
        }
        System.out.printf("Total number of cars:  %d\n", count);
        System.out.printf("Total number of available cars:  %d\n", available);
        System.out.println("==========================================");

    }

    // Print all rentals and total revenue
    static void printRentalSummary(String[][] rentals, int count) {
        System.out.println("Rent-a-Car Rental Summary");
        System.out.println("Rentals: ");
        System.out.println("Name           Numberplate Pickup Return Cost");
        int revenue = 0;
        for (int i = 0; i < count; i++) {
            String cost =  rentals[i][COST].isEmpty() ? "" : rentals[i][COST];
            String returnTime = rentals[i][RETURN].isEmpty() ? "" : rentals[i][RETURN];
            if (!cost.isEmpty()) revenue += Integer.parseInt(cost.split(" ")[0]);
            System.out.printf("%-20s %-12s %-7s %-7s %s\n", rentals[i][NAME], rentals[i][RENT_REG], rentals[i][PICKUP], returnTime, cost);

        }
        System.out.printf("Total number of rentals:  %d\n", count);
        System.out.printf("Total revenue:  %d SEK\n", revenue);
        System.out.println("==========================================");

    }

    // Find car index in fleet by reg number
    static int findRental(String[][] fleet, String reg, int count) {
        for (int i = 0; i < count; i++) {
            if (fleet[i][RENT_REG].equals(reg)) {
                return i;
            }
        }
        return -1;
    }

    // Validate hh:mm time format
    static boolean isValidTime(String time) {
        if (!time.matches("\\d{2}:\\d{2}")) {
            return false;
        }
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return hour >= 0 && hour < 24 && minute >= 0 && minute < 60;
    }

    // Calculate time difference in hours between pickup and return
    static double calculateHours(String start, String end) {
        String[] s = start.split(":");
        String[] e = end.split(":");
        int sh = Integer.parseInt(s[0]);
        int sm = Integer.parseInt(s[1]);
        int eh = Integer.parseInt(e[0]);
        int em = Integer.parseInt(e[1]);
        return (eh + em / 60.0) - (sh + sm / 60.0);

    }

    static int findCar(String[][] fleet, String reg, int count) {
        for (int i = 0; i < count; i++) {
            if (fleet[i][REG_NUM].equals(reg)) {
                return i;
            }
        }
        return -1;
    }

}
