package com.hrznstudio.galacticraft.api.research;

import javax.annotation.Nullable;
import java.util.*;

public class ResearchPositioner {
    private final Map<ResearchNode, ResearchPos> map = new HashMap<>();

    public ResearchPositioner() {

    }

    public void position(ResearchNode root) {
        ResearchPos.arrangeForTree(root, this);
        map.clear();
    }

    public static class ResearchPos {

        private final ResearchNode node;
        private final ResearchPositioner positioner;
        private final List<ResearchNode> parents = new ArrayList<>();
        private final ResearchPos previousSibling;
        private final int childrenSize;
        private final List<ResearchPos> children = new ArrayList<>();
        private ResearchPos optionalLast;
        private ResearchPos substituteChild;
        private int depth;
        private float row;
        private float relativeRowInSiblings;
        private float field_1266;
        private float field_1265;

        public ResearchPos(ResearchNode node, List<ResearchNode> parents, @Nullable ResearchPos previousSibling, int childrenSize, int depth, ResearchPositioner positioner) {
            if (node.getInfo() == null) {
                throw new IllegalArgumentException("Can't position an invisible advancement!");
            } else {
                this.node = node;
                this.parents.addAll(parents);
                this.previousSibling = previousSibling;
                this.childrenSize = childrenSize;
                this.optionalLast = this;
                this.depth = depth;
                this.row = -1.0F;
                this.positioner = positioner;
                ResearchPos researchPos = null;

                ResearchNode node2;
                for (Iterator<ResearchNode> var7 = node.getChildren().iterator(); var7.hasNext(); researchPos = this.findChildrenRecursively(node2, researchPos)) {
                    node2 = var7.next();
                }
                this.positioner.map.put(node, this);
            }
        }

        public static void arrangeForTree(ResearchNode root, ResearchPositioner positioner) {
            if (root.getInfo() == null) {
                throw new IllegalArgumentException("Can't position children of an invisible root!");
            } else {
                ResearchPos researchPos = new ResearchPos(root, new ArrayList<>(), null, 1, 0, positioner);
                researchPos.calculateRecursively();
                float f = researchPos.findMinRowRecursively(0.0F, 0, researchPos.row);
                if (f < 0.0F) {
                    researchPos.increaseRowRecursively(-f);
                }

                researchPos.apply();
            }
        }

        @Nullable
        private ResearchPos findChildrenRecursively(ResearchNode node, @Nullable ResearchPos lastChild) {
            ResearchNode research;
            if (node.getInfo() != null) {
//             List<ResearchPos> parents = new ArrayList<>();
//             for (ResearchNode parent : node.getParents()) {
//                parents.add(positioner.map.get(parent));
//                if (positioner.map.get(parent) == null) {
//                   Galacticraft.logger.fatal("child loaded before parent?!");
//                }
//             }
                lastChild = new ResearchPos(node, Arrays.asList(node.getParents()), lastChild, this.children.size() + 1, this.depth + 1, this.positioner);
                this.children.add(lastChild);
            } else {
                for (Iterator<ResearchNode> var3 = node.getChildren().iterator(); var3.hasNext(); lastChild = this.findChildrenRecursively(research, lastChild)) {
                    research = var3.next();
                }
            }

            return lastChild;
        }

        private void calculateRecursively() {
            if (this.children.isEmpty()) {
                if (this.previousSibling != null) {
                    this.row = this.previousSibling.row + 1.0F;
                } else {
                    this.row = 0.0F;
                }

            } else {
                ResearchPos researchPos = null;

                ResearchPos researchPos2;
                for (Iterator<ResearchPos> var2 = this.children.iterator(); var2.hasNext(); researchPos = researchPos2.onFinishCalculation(researchPos == null ? researchPos2 : researchPos)) {
                    researchPos2 = var2.next();
                    researchPos2.calculateRecursively();
                }

                this.onFinishChildrenCalculation();
                float f = ((this.children.get(0)).row + (this.children.get(this.children.size() - 1)).row) / 2.0F;
                if (this.previousSibling != null) {
                    this.row = this.previousSibling.row + 1.0F;
                    this.relativeRowInSiblings = this.row - f;
                } else {
                    this.row = f;
                }

            }
        }

        private float findMinRowRecursively(float deltaRow, int depth, float minRow) {
            this.row += deltaRow;
            this.depth = depth;
            if (this.row < minRow) {
                minRow = this.row;
            }

            ResearchPos researchPos;
            for (Iterator<ResearchPos> var4 = this.children.iterator(); var4.hasNext(); minRow = researchPos.findMinRowRecursively(deltaRow + this.relativeRowInSiblings, depth + 1, minRow)) {
                researchPos = var4.next();
            }

            return minRow;
        }

        private void increaseRowRecursively(float deltaRow) {
            this.row += deltaRow;

            for (ResearchPos researchPos : this.children) {
                researchPos.increaseRowRecursively(deltaRow);
            }

        }

        private void onFinishChildrenCalculation() {
            float f = 0.0F;
            float g = 0.0F;

            for (int i = this.children.size() - 1; i >= 0; --i) {
                ResearchPos researchPos = this.children.get(i);
                researchPos.row += f;
                researchPos.relativeRowInSiblings += f;
                g += researchPos.field_1266;
                f += researchPos.field_1265 + g;
            }

        }

        @Nullable
        private ResearchPos getFirstChild() {
            if (this.substituteChild != null) {
                return this.substituteChild;
            } else {
                return !this.children.isEmpty() ? this.children.get(0) : null;
            }
        }

        @Nullable
        private ResearchPos getLastChild() {
            if (this.substituteChild != null) {
                return this.substituteChild;
            } else {
                return !this.children.isEmpty() ? this.children.get(this.children.size() - 1) : null;
            }
        }

        private ResearchPos onFinishCalculation(ResearchPos last) {
            if (this.previousSibling != null) {
                ResearchPos research = this;
                ResearchPos node = this;
                ResearchPos siblingNode = this.previousSibling;
                ResearchPos child = positioner.map.get(this.parents.get(0)).children.get(0);
                float f = this.relativeRowInSiblings;
                float g = this.relativeRowInSiblings;
                float h = siblingNode.relativeRowInSiblings;

                float i;
                for (i = child.relativeRowInSiblings; siblingNode.getLastChild() != null && research.getFirstChild() != null; g += node.relativeRowInSiblings) {
                    siblingNode = siblingNode.getLastChild();
                    research = research.getFirstChild();
                    child = child.getFirstChild();
                    node = node.getLastChild();
                    node.optionalLast = this;
                    float j = siblingNode.row + h - (research.row + f) + 1.0F;
                    if (j > 0.0F) {
                        siblingNode.getLast(this, last).pushDown(this, j);
                        f += j;
                        g += j;
                    }

                    h += siblingNode.relativeRowInSiblings;
                    f += research.relativeRowInSiblings;
                    i += child.relativeRowInSiblings;
                }

                if (siblingNode.getLastChild() != null && node.getLastChild() == null) {
                    node.substituteChild = siblingNode.getLastChild();
                    node.relativeRowInSiblings += h - g;
                } else {
                    if (research.getFirstChild() != null && child.getFirstChild() == null) {
                        child.substituteChild = research.getFirstChild();
                        child.relativeRowInSiblings += f - i;
                    }

                    last = this;
                }

            }
            return last;
        }

        private void pushDown(ResearchPos researchPos, float extraRowDistance) {
            float f = (float) (researchPos.childrenSize - this.childrenSize);
            if (f != 0.0F) {
                researchPos.field_1266 -= extraRowDistance / f;
                this.field_1266 += extraRowDistance / f;
            }

            researchPos.field_1265 += extraRowDistance;
            researchPos.row += extraRowDistance;
            researchPos.relativeRowInSiblings += extraRowDistance;
        }

        private ResearchPos getLast(ResearchPos researchPos, ResearchPos researchPos2) {
            if (optionalLast != null) {
                boolean b = false;
                for (ResearchNode parent : researchPos.parents) {
                    b = positioner.map.get(parent).children.contains(optionalLast);
                }
                if (b) {
                    return optionalLast;
                }
            }
            return researchPos2;
        }

        private void apply() {
            if (this.node.getInfo() != null) {
                this.node.getInfo().setPos((float) this.depth, this.row);
            }

            if (!this.children.isEmpty()) {

                for (ResearchPos researchPos : this.children) {
                    researchPos.apply();
                }
            }

        }
    }
}
