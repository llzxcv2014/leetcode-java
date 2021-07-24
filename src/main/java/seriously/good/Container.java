package seriously.good;

import java.util.HashSet;
import java.util.Set;

/**
 * a water container
 */
public class Container {

    private Set<Container> group;

    private double amount;

    public Container() {
        group = new HashSet<>();
        // 自己作为第一个元素
        group.add(this);
    }

    public double getAmount() {
        return this.amount;
    }

    public void connectTo(Container other) {
        if (group == other.group) {
            return;
        }

        int thisSize = group.size();
        int otherSize = other.group.size();

        double thisTotal = amount * thisSize;
        double otherTotal = amount * otherSize;

        double newAmount = (thisTotal + otherTotal) / (thisSize + otherSize);

        group.addAll(other.group);
        for (Container c : other.group) {
            c.group = group;
        }

        for (Container c : group) {
            c.amount = newAmount;
        }
    }

    public void addWater() {
        double amountPerContainer = amount / group.size();
        for (Container c: group) c.amount += amountPerContainer;
    }
}
