import java.util.HashSet;
import java.util.Set;

public class Constraint {
		private int max;
		private Set<Role> roles;
		
		public Constraint(int max) {
			this.max = max;
			roles = new HashSet<Role>();
		}
		public boolean add(Role role) {
			return roles.add(role);
		}
		public boolean contains(Role role) {
			return roles.contains(role);
		}
		public int getMax() {
			return max;
		}
		public boolean removeRole(Role role) {
			return roles.remove(role);
		}
		public void setMax(int max) {
			this.max = max;
		}
		public Role[] getRoles() {
			Object[] objects = roles.toArray();
			if (objects == null) return null;
			Role[] result = new Role[objects.length];
			for (int i = 0; i < objects.length; i++) {
				result[i] = (Role) objects[i];
			}
			return result;
		}
	}