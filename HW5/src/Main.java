import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {
	private static Set<Role> roles;
	private static HashSet<Role> topRoles;
	private static Set<String> objects;
	private static ArrayList<Constraint> constraints;
	private static Set<String> users;
	private static HashMap<String, Set<Role>> userRoles;

	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		readRoles("roleHierarchy.txt");
		for (Role topRole : topRoles) {
			printRelationships(topRole);
		}
		printLine();
		
		readObjects("resourceObjects.txt");
		printEmptyMatrix();
		printLine();
		
		for (Role topRole : topRoles) {
			addControlAndOwnRights(topRole);
		}
		readPermissions("permissionsToRoles.txt");
		printMatrix();
		printLine();
		
		readConstraints("roleSetsSSD.txt");
		displayConstraints();
		printLine();
		
		readUserRoles("usersRoles.txt");
		displayUserRoleMatrix();
		printLine();
		
		takeInput(in);
		
		String role = "";
		Role removed = null;
		boolean errorFound;
		do {
			errorFound = false;
			try {
			System.out.print("Enter role to delete: ");
			role = in.nextLine();
			removed = getRole(new Role(role));
			if (!roles.contains(removed)) throw new Exception("No such role.");
			if (topRoles.contains(removed)) throw new Exception("You can't remove " + role + " because it is the highest descendant.");
			} catch (Exception e) {
				System.err.println(e.getMessage());
				errorFound = true;
			}
		} while (errorFound);
		removeRole(removed);
		System.out.println("Role " + role + " has been removed.");
		printLine();
		
		printMatrix();
		printLine();
		
		takeInput(in);
		System.out.println("Program finished.");
		in.close();
	}
	public static void takeInput(Scanner in) {
		String answer = "";
		do {
			query(in);
			System.out.print("Continue entering queries? (Yes or Y to continue) ");
			answer = in.nextLine();
		} while (answer.toLowerCase().equals("y") || answer.toLowerCase().equals("yes"));
	}
	private static void removeRole(Role role) {
		for (Role topRole : topRoles) {
			topRole.removePermissions(role.getName());
		}
		roles.remove(role);
		
		for (Map.Entry<String, Set<Role>> entry : userRoles.entrySet()) {
			Set<Role> roles = entry.getValue();
			roles.remove(role);
		}
		
		objects.remove(role.getName());
		
		for (Constraint constraint : constraints) {
			constraint.removeRole(role);
		}
		
		role.remove();
	}
	public static void query(Scanner in) {
		boolean errorFound = false;
		String user = "";
		String object = "";
		String right = "";
		
		do {
			System.out.print("Enter the user in your query: ");
			user = in.nextLine();
		} while (!users.contains(user));
		
		System.out.print("Enter the object in your query (hit enter if it's for all): ");
		
		do {
			errorFound = false;
			try {
				object = in.nextLine();
				if (!object.equals("") && !objects.contains(object)) {
					throw new Exception("Object " + object + " doesn't exist.");
				}
			} catch (Exception e) {
				errorFound = true;
				System.err.print(e.getMessage() + System.lineSeparator());
				System.out.print("Enter the object in your query (hit enter if it's for all): ");
			}
		} while (errorFound);
		
		System.out.print("Enter the access right in your query (hit enter if it's for all): ");
		
		do {
			errorFound = false;
			try {
				right = in.nextLine();
			} catch (Exception e) {
				errorFound = true;
				System.err.println(e.getMessage());
				System.out.print("Enter the access right in your query (hit enter if it's for all): ");
			}
		} while (errorFound);
		
		switch (object) {
		
		case "":
			
			HashMap<String, Set<String>> objectsAndRights = new HashMap<String, Set<String>>();
			for (Role role : userRoles.get(user)) {
				
				HashMap<String, Set<String>> xirRights = role.getPermissions();
				
				for (Map.Entry<String, Set<String>> entry : xirRights.entrySet()) {
					String roleObject = entry.getKey();
					Set<String> roleRights = entry.getValue();
					Set<String> resultRights = objectsAndRights.get(roleObject);
					
					if (resultRights == null)
						objectsAndRights.put(roleObject, roleRights);
					else {
						for (String access : roleRights) {
							resultRights.add(access);
						}
					}
				}
			}
			for (Map.Entry<String, Set<String>> entry : objectsAndRights.entrySet()) {
				String obj = entry.getKey();
				Set<String> xirRights = entry.getValue();
				StringBuilder output = new StringBuilder(30);
				
				for (String access : xirRights) {
					output.append(access);
					output.append(", ");
				}
				
				String out = output.substring(0, output.length() - 2);
				System.out.printf("%s\t%s%s",obj,out,System.lineSeparator());
			}
			break;
		
		default:
			
			if (right.equals("")) {
				Set<String> rights = new HashSet<String>();
				Set<Role> xirRoles = userRoles.get(user);
				
				for (Role role : xirRoles) {
					HashMap<String, Set<String>> perms = role.getPermissions();
					Set<String> xirRights = (HashSet<String>) perms.get(object);
					if (xirRights != null)
						for (String accessRight : xirRights) {
							rights.add(accessRight);
						}
				}
				StringBuilder output = new StringBuilder(30);
				
				for (String accessRight : rights) {
					output.append(accessRight);
					output.append(", ");
				}
				
				String out = output.substring(0, output.length() - 2);
				System.out.printf("%s\t%s%s",object,out,System.lineSeparator());
			} else {
				Set<Role> xirRoles = userRoles.get(user);
				boolean authorized = false;
				for (Role role : xirRoles) {
					HashMap<String, Set<String>> perms = role.getPermissions();
					Set<String> xirRights = (HashSet<String>) perms.get(object);
					if (xirRights != null && (xirRights.contains(right) || xirRights.contains(right + "*")))
						authorized = true;
				}
				if (authorized)
					System.out.println("Authorized.");
				else
					System.out.println("Rejected.");
			}
		}

	}

	public static void displayUserRoleMatrix() {
		System.out.println("User-Role Matrix:" + System.lineSeparator());
		Object[] arr = roles.toArray();
		
		System.out.printf("%-10s ", "");
		for (int i = 0; i < arr.length; i++) {
			Role role = (Role) arr[i];
			
			if ((i + 1) % 5 == 0) {
				System.out.printf("%-10s%s", role.getName(),System.lineSeparator());
				for (Map.Entry<String, Set<Role>> entry : userRoles.entrySet()) {
					String key = entry.getKey();
					Set<Role> xirRoles = entry.getValue();
					System.out.printf("%-10s ", key);
					
					for (int k = (i - 5 < 0 ? 0 : i - 4); k <= i; k++) {
						System.out.printf("%-10s ", xirRoles.contains((Role) arr[k]) ? "+" : "");
					}
					System.out.printf("%s%s",System.lineSeparator(),System.lineSeparator());
				}
				System.out.printf("%s %10s", System.lineSeparator(),"");
			} else {
				System.out.printf("%-10s ", role.getName());
			}
		}
	}

	public static void displayConstraints() {
		int i = 0;
		
		for (Constraint constraint : constraints) {
			String cRoles = "";
			
			Object[] objects = constraint.getRoles();
			for (int k = 0; k < objects.length; k++) {
				Role role = (Role) objects[k];
				cRoles += role.getName() + ", ";
			}
			
			cRoles = cRoles.equals("") ? "{}" : "{" + cRoles.substring(0, cRoles.length() - 2) + "}";
			System.out.println("Constraint " + ++i + ", n = " + constraint.getMax() + ", set of roles = " + cRoles);
		}
	}

	private static void readUserRoles(String file) {
		boolean errorFound = false;
		do {
			users = new HashSet<String>();
			userRoles = new HashMap<String, Set<Role>>();
			BufferedReader br = null;
			
			try {
				br = new BufferedReader(new FileReader(new File(file)));
				String line;
				int lineNum = 0;
				
				while ((line = br.readLine()) != null) {
					
					lineNum++;
					String[] values = line.split("\t");
					
					if (values.length < 2) {
						throw new Exception("Line #" + lineNum + " in file '" + file
								+ "' must have at least 2 values seperated by tabspace. Please correct this.");
					}
					
					if (!users.add(values[0])) {
						throw new Exception("Line #" + lineNum + " in file '" + file + "' uses duplicate user '"
								+ values[0] + "'. Please fix this.");
					}
					
					Set<Role> hypotheticalRoles = new HashSet<Role>();
					for (int i = 1; i < values.length; i++) {
						
						Role role = getRole(new Role(values[i]));
						if (!roles.contains(role)) {
							throw new Exception("Line #" + lineNum + " in file '" + file + "' tries adding role "
									+ values[i] + " to a constraint when it doesn't exist.");
						}
						if (!hypotheticalRoles.add(role)) {
							throw new Exception("Line # " + lineNum + " in file '" + file + "' lists role " + values[i]
									+ " more than once. Please fix this.");
						}
					}
					
					Object[] rules = constraints.toArray();
					if (rules == null) {
						throw new Exception("Error: no constraints are set.");
					}
					
					for (int i = 0; i < rules.length; i++) {
						
						Constraint constraint = (Constraint) rules[i];
						int k = 0;
						
						for (Role role : hypotheticalRoles) {
							if (constraint.contains(role))
								k++;
							if (k >= constraint.getMax()) {
								
								String cRoles = "";
								Object[] objects = constraint.getRoles();
								for (int j = 0; j < objects.length; j++) {
									Role r = (Role) objects[j];
									cRoles += r.getName() + ", ";
								}
								cRoles = cRoles.equals("") ? "{}"
										: "{" + cRoles.substring(0, cRoles.length() - 2) + "}";
								throw new Exception("Error: " + values[0]
										+ " is not authorized to have all of those roles. (Constraint: n = "
										+ constraint.getMax() + ", roles = " + cRoles + "}");
							}
						}
					}
					
					userRoles.put(values[0], hypotheticalRoles);
				}
				errorFound = false;
			} catch (FileNotFoundException e) {
				errorFound = true;
				System.out.println("Error: could not find file '" + file + "'. Please fix this problem.");
				waitForUser();
			} catch (Exception e) {
				errorFound = true;
				System.err.println(e.getMessage());
				try {
					br.close();
				} catch (Exception f) {
					System.err.println(f.getMessage());
				}
				waitForUser();
			}
		} while (errorFound);
	}

	private static void readConstraints(String file) {
		boolean errorFound = false;
		do {
			
			constraints = new ArrayList<Constraint>();
			BufferedReader br = null;
			
			try {
				br = new BufferedReader(new FileReader(new File(file)));
				String line;
				int lineNum = 0;
				
				while ((line = br.readLine()) != null) {
					
					lineNum++;
					String[] values = line.split("\t");
					
					if (values.length < 2) {
						throw new Exception("Line #" + lineNum + " in file '" + file
								+ "' must have at least 2 values seperated by tabspace. Please correct this.");
					}
					
					int max = Integer.parseInt(values[0]);
					if (max < 2)
						throw new Exception("Error: on Line #" + lineNum + " in file '" + file + "': " + values[0]
								+ " is too low. Must be 2 or higher. Please fix this.");
					
					Constraint newConstraint = new Constraint(max);
					
					for (int i = 1; i < values.length; i++) {
						Role role = getRole(new Role(values[i]));
						
						if (!roles.contains(role)) {
							throw new Exception("Line #" + lineNum + " in file '" + file + "' tries adding role "
									+ values[i] + " to a constraint when it doesn't exist.");
						}
						newConstraint.add(role);
					
					}
					
					constraints.add(newConstraint);
				}
				errorFound = false;
			} catch (FileNotFoundException e) {
				errorFound = true;
				System.out.println("Error: could not find file '" + file + "'. Please fix this problem.");
				waitForUser();
				
			} catch (NumberFormatException e) {
				errorFound = true;
				System.err.println(
						"Error: first value on each line in file '" + file + "' should be a number. Please fix this.");
				waitForUser();
				
			} catch (Exception e) {
				errorFound = true;
				System.err.println(e.getMessage());
				try {
					br.close();
				} catch (Exception f) {
					System.err.println(f.getMessage());
				}
				waitForUser();
			}
		} while (errorFound);
	}

	public static int max(double num) {
		if (num < 1)
			return 1;
		return (int) num;
	}

	public static void printMatrix() {
		System.out.println("Updated matrix after applying default permissions:" + System.lineSeparator());

		Object[] s = objects.toArray();
		int i;
		System.out.printf("%-15s ", "");
		
		for (i = 0; i < s.length; i++) {
			String object = (String) s[i];
			
			if ((i + 1) % 5 == 0) {
				System.out.printf("%-15s%s", object,System.lineSeparator());
				Object[] r = roles.toArray();
				
				for (int j = 0; j < r.length; j++) {
					Role role = (Role) r[j];
					System.out.printf("%-15s ", role.getName());
					
					for (int k = i - 4; k <= i; k++) {
						String[] perms = role.getPermissions((String) s[k]);
						StringBuilder output = new StringBuilder();
						
						if (perms != null)
							for (int m = 0; m < perms.length; m++) {
								output.append(perms[m]);
								output.append("/");
							}
						
						String out = output.length() == 0 ? "" : output.substring(0, output.length() - 1);
						System.out.printf("%-15s ", out);
					}
					
					System.out.printf("%s%s",System.lineSeparator(),System.lineSeparator());
				}
				
				System.out.printf("%-15s ", "");
			} else
				System.out.printf("%-15s ", object);
		}
		
		if (i % 5 > 0) {
			
			System.out.printf(System.lineSeparator());
			Object[] r = roles.toArray();
			
			for (int j = 0; j < r.length; j++) {
				
				Role role = (Role) r[j];
				System.out.printf("%-15s ", role.getName());
				
				for (int k = i - 4; k < i; k++) {
					String[] perms = role.getPermissions((String) s[k]);
					StringBuilder output = new StringBuilder();
					
					if (perms != null)
						for (int m = 0; m < perms.length; m++) {
							output.append(perms[m]);
							if (m < perms.length - 1) output.append("/");
						}
					
					String out = output.equals("") ? "" : output.toString();
					System.out.printf("%-15s ", out);
				}
				System.out.printf("%s%s",System.lineSeparator(),System.lineSeparator());
			}
		}
	}

	private static void readPermissions(String file) {
		boolean errorFound = false;
		do {
			BufferedReader br = null;
			try {
				
				br = new BufferedReader(new FileReader(new File(file)));
				String line;
				int lineNum = 0;
				
				while ((line = br.readLine()) != null) {
					
					lineNum++;
					String[] values = line.split("\t");
					
					if (values.length != 3) {
						throw new Exception("Line #" + lineNum + " in file '" + file
								+ "' must have exactly 3 values seperated by tabspace. Please correct this.");
					}
					
					if (!objects.contains(values[2])) {
						throw new Exception("Line #" + lineNum + " in file '" + file + "' has permissions for object '"
								+ values[2] + "' which doesn't exist.");
					}
					
					Role role = getRole(new Role(values[0]));
					
					if (!roles.contains(role)) {
						throw new Exception("Line #" + lineNum + " in file '" + file
								+ "' tries to add permissions to role " + values[0] + " which doesn't exist.");
					}
					
					role.addPermission(values[2], values[1]);
				}
				errorFound = false;
			} catch (FileNotFoundException e) {
				errorFound = true;
				System.out.println("Error: could not find file '" + file + "'. Please fix this problem.");
				waitForUser();
				
			} catch (Exception e) {
				errorFound = true;
				System.err.println(e.getMessage());
				try {
					br.close();
				} catch (Exception f) {
					System.err.println(f.getMessage());
				}
				waitForUser();
				
			}
		} while (errorFound);

	}

	private static void addControlAndOwnRights(Role role) {
		if (role == null) return;
		
		giveRightsToRoleAndDescendants(role, role.getName(), "control");
		
		Role[] ascendants = role.getAscendants();
		if (ascendants == null)
			return;
		
		for (int i = 0; i < ascendants.length; i++) {
			giveRightsToRoleAndDescendants(ascendants[i], ascendants[i].getName(), "control");
			giveOwnRightOnlyToDescendants(ascendants[i]);
			addControlAndOwnRights(ascendants[i]);
		}
	}

	private static void giveOwnRightOnlyToDescendants(Role role) {
		if (role.getDescendant() != null)
			giveRightsToRoleAndDescendants(role.getDescendant(), role.getName(), "own");
	}

	private static void giveRightsToRoleAndDescendants(Role role, String object, String right) {
		role.addPermission(object, right);
		if (role.getDescendant() != null)
			giveRightsToRoleAndDescendants(role.getDescendant(), object, right);
	}

	public static void printLine() {
		System.out.println(System.lineSeparator() + "======================================================================" + System.lineSeparator());
	}

	public static void printEmptyMatrix() {
		System.out.printf("Empty Matrix:%s",System.lineSeparator());
		int i = 0;
		System.out.printf("%-10s ", "");
		
		for (String object : objects) {
			i++;
			
			if (i % 5 == 0) {
				System.out.printf("%-10s%s", object,System.lineSeparator());
				for (Role role : roles) {
					System.out.printf("%-10s%s", role.getName(),System.lineSeparator());
				}
				System.out.printf("%-10s ", "");
			} else
				System.out.printf("%-10s ", object);
		}
		
		System.out.printf(System.lineSeparator());
		
		if (i % 5 != 0) {
			for (Role role : roles) {
				System.out.printf("%-10s%s", role.getName(),System.lineSeparator());
			}
		}
	}

	private static void readObjects(String file) {
		boolean errorFound = false;
		do {
			
			BufferedReader br = null;
			objects = new HashSet<String>();
			
			for (Role role : roles) {
				objects.add(role.getName());
			}
			try {
				
				br = new BufferedReader(new FileReader(new File(file)));
				String line = br.readLine();
				if (line == null) {
					br.close();
					throw new Exception("Error: file '" + file + "' is empty. Please fix this.");
				}
				
				String[] input = line.split("\t");
				for (int i = 0; i < input.length; i++) {
					if (!input[i].equals("") && !objects.add(input[i]))
						throw new Exception("Error in file '" + file + "'. Object " + input[i]
								+ " already exists! Please correct this mistake.");
				}
				
				errorFound = false;
				
			} catch (FileNotFoundException e) {
				errorFound = true;
				System.err.println("File '" + file + "' not found. Please put this file in directory.");
				waitForUser();
				
			} catch (Exception e) {
				errorFound = true;
				try {
					br.close();
				} catch (Exception f) {
					System.err.println("Error: " + f.getMessage());
				}
				System.err.println(e.getMessage());
				waitForUser();
				
			}
		} while (errorFound);
	}

	private static void readRoles(String file) {
		boolean errorFound;
		do {
			
			BufferedReader br = null;
			roles = new HashSet<Role>();
			topRoles = new HashSet<Role>();
			
			try {
				br = new BufferedReader(new FileReader(new File(file)));
				String line;
				int lineNum = 0;
				int highest = 0;
				
				while ((line = br.readLine()) != null) {
					
					lineNum++;
					String[] values = line.split("\t");
					
					if (values.length != 2) {
						throw new Exception("Line #" + lineNum + " in file '" + file
								+ "' must have exactly 2 values seperated by tabspace. Please correct this.");
					}
					
					Role newRole = getRole(new Role(values[0]));
					Role descendant = getRole(new Role(values[1]));
					
					if (!descendant.addAscendant(newRole)) {
						throw new Exception("Line #" + lineNum + " in file '" + file + "': Descendant " + values[1]
								+ " already has ascendant " + values[0] + ". Please fix this in file '" + file + "'");
					}
					
					if (newRole.getDescendant() != null) {
						throw new Exception("Line #" + lineNum + " in file '" + file + "': Role " + values[0]
								+ " already has a descendant. Please fix this error in file '" + file + "' on line "
								+ lineNum);
					}
					if (topRoles.contains(newRole)) topRoles.remove(newRole);
					if (descendant.getDescendant() == null) topRoles.add(descendant);
					newRole.setDescendant(descendant);
					roles.add(newRole);
					roles.add(descendant);
					
				}
				errorFound = false;
				br.close();
				
			} catch (FileNotFoundException e) {
				errorFound = true;
				System.err.println("File '" + file + "' not found. Please create this file.");
				waitForUser();
				
			} catch (Exception e) {
				errorFound = true;
				System.err.println(e.getMessage());
				try {
					br.close();
				} catch (Exception f) {
					System.err.println("Error: " + f.getMessage());
				}
				waitForUser();
				
			}
		} while (errorFound);
	}

	public static void printRelationships(Role role) {
		if (role == null) {
			System.out.println("No roles to display.");
			return;
		}
		
		StringBuilder children = new StringBuilder(30);
		Object[] arr = role.getAscendants();
		
		if (arr == null)
			return;
		
		for (int i = 0; i < arr.length; i++) {
			Role ascendant = (Role) arr[i];
			children.append(", ");
			children.append(ascendant.getName());
		}
		String out = children.substring(1);
		
		System.out.println(role.getName() + " ---> " + out);
		
		for (int k = 0; k < arr.length; k++) {
			Role ascendant = (Role) arr[k];
			printRelationships(ascendant);
		}
	}

	public static void waitForUser() {
		System.out.println("Enter [Enter] to continue...");
		try {
			System.in.read();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	// Check if the Role already exists in the list of roles. If it does, return
	// the found role.
	// If it doesn't, return the new role.
	public static Role getRole(Role other) {
		for (Role role : roles) {
			if (role.equals(other))
				return role;
		}
		return other;
	}
}
