package com.hrznstudio.galacticraft.api.research;

import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;

public class ResearchConditionsContainer<T extends CriterionConditions> extends Criterion.ConditionsContainer<T> {
    private final T conditions;
    private final ResearchNode node;
    private final String id;

    public ResearchConditionsContainer(T conditions, ResearchNode node, String id) {
        super(null, null, null);
        this.conditions = conditions;
        this.node = node;
        this.id = id;
    }

    @Override
    public T getConditions() {
        return this.conditions;
    }

    public void grant(PlayerResearchTracker tracker) {
        tracker.grantCriterion(this.node, this.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ResearchConditionsContainer<?> researchConditionsContainer = (ResearchConditionsContainer<?>) o;
            if (!this.conditions.equals(researchConditionsContainer.conditions)) {
                return false;
            } else {
                return this.node.equals(researchConditionsContainer.node) && this.id.equals(researchConditionsContainer.id);
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = this.conditions.hashCode();
        i = 31 * i + this.node.hashCode();
        i = 31 * i + this.id.hashCode();
        return i;
    }

    @Override
    public String toString() {
        return "ResearchConditionsContainer{" +
                "conditions=" + conditions +
                ", node=" + node +
                ", id='" + id + '\'' +
                '}';
    }
}