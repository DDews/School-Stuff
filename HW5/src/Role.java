import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Role {
	private String name;
	private Role descendant;
	private Set<Role> ascendants;
	private HashMap<String, Set<String>> permissions;

	public Role() {
		this("", null);
	}

	public Role(String name) {
		this(name, null);
	}

	public Role(String name, Role descendant) {
		this.name = name;
		this.descendant = descendant;
		ascendants = new HashSet<Role>();
		permissions = new HashMap<String, Set<String>>();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setDescendant(Role desc) {
		descendant = desc;
	}

	public Role getDescendant() {
		return descendant;
	}

	public boolean addAscendant(Role asc) {
		return ascendants.add(asc);
	}

	public void remove() {
		if (descendant == null)
			return;
		for (Role ascendant : ascendants) {
			ascendant.setDescendant(descendant);
			descendant.addAscendant(ascendant);
		}
		
		ascendants.clear();
	}

	public void removePermissions(String object) {
		permissions.remove(object);
		for (Role ascendant : ascendants) {
			ascendant.removePermissions(object);
		}
	}

	public void addPermission(String object, String permission) {
		Set<String> perms = (HashSet<String>) permissions.get(object);
		if (perms == null)
			perms = new HashSet<String>();
		perms.add(permission);
		permissions.put(object, perms);
	}

	public void addPermissions(String object, String[] permissions) {
		Set<String> perms = (HashSet<String>) this.permissions.get(object);
		if (perms == null)
			perms = new HashSet<String>();
		for (int i = 0; i < permissions.length; i++) {
			perms.add(permissions[i]);
		}
		this.permissions.put(object, perms);
	}

	public String[] getPermissions(String object) {
		Set<String> perms = permissions.get(object);
		if (perms == null)
			return null;
		String[] result = new String[perms.size()];
		int i = 0;
		for (String perm : perms) {
			result[i++] = perm;
		}
		return result;
	}

	private HashMap<String, Set<String>> uniquePermissions() {
		HashMap<String, Set<String>> result = new HashMap<String, Set<String>>();
		for (Map.Entry<String, Set<String>> entry : permissions.entrySet()) {
			String key = entry.getKey();
			Set<String> perms = entry.getValue();
			result.put(key, perms);
		}
		return result;
	}

	public HashMap<String, Set<String>> getPermissions() {
		return getRecursivePermissions(uniquePermissions());
	}

	public HashMap<String, Set<String>> getRecursivePermissions(HashMap<String, Set<String>> map) {
		HashMap<String, Set<String>> perms = uniquePermissions();
		for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
			String theirObj = entry.getKey();
			Set<String> theirPerms = entry.getValue();
			Set<String> myObjPerms = perms.get(theirObj);
			if (myObjPerms != null)
				for (String perm : myObjPerms) {
					theirPerms.add(perm);
				}
		}
		for (Map.Entry<String, Set<String>> entry : perms.entrySet()) {
			String myObj = entry.getKey();
			if (map.get(myObj) == null) map.put(myObj, entry.getValue());
		}
		for (Role ascendant : ascendants) {
			map = ascendant.getRecursivePermissions(map);
		}
		return map;
	}

	public Role[] getAscendants() {
		if (ascendants.size() == 0)
			return null;
		Role[] result = new Role[ascendants.size()];
		int i = 0;
		for (Role child : ascendants) {
			result[i++] = child;
		}
		return result;
	}

	public int numberOfInherited() {
		int result = 0;
		result += ascendants.size();
		for (Role child : ascendants) {
			result += child.numberOfInherited();
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		Role other = (Role) obj;
		if (other.getName().equals(name))
			return true;
		return false;
	}
}